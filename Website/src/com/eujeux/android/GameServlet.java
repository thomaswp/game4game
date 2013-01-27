package com.eujeux.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.FileUtils;
import com.eujeux.LoginUtils;
import com.eujeux.PMF;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.EJUser;
import com.eujeux.data.GameInfo;
import com.eujeux.data.WebSettings;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServicePb.BlobstoreService;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		EJUser user = LoginUtils.getUser();
		if (user == null) {
			LoginUtils.sendNoUserError(resp);
			return;
		}
		
		String action = req.getParameter(WebSettings.PARAM_ACTION);
		
		if (WebSettings.ACTION_FETCH.equals(action)) {
			
		} else if (WebSettings.ACTION_POST.equals(action)) {
			GameInfo info = FileUtils.readObject(req, GameInfo.class);
			if (info != null) {
				EJGame game = QueryUtils.queryUnique(EJGame.class, "id == %s", info.id);
				if (game == null) {
					LoginUtils.sendBadRequestError(resp, 
							"No such game to update");
					return;
				}
				if (!game.getCreatorId().equals(user.getId())) {
					LoginUtils.sendBadRequestError(resp, 
							"User does not have permission to update this game.");
					return;
				}
				
				game.update(info);
				PMF.get().getPersistenceManager().makePersistent(game);
				return;
			}
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} else if (WebSettings.ACTION_CREATE.equals(action)) {
			
			String name = req.getParameter("name");
			if (name == null) {
				LoginUtils.sendBadRequestError(resp, 
						"Must provide a name");
				return;
			}
			
			byte[] bytes = FileUtils.readBytes(req);
			
			if (bytes.length == 0) {
				LoginUtils.sendBadRequestError(resp, 
						"No game to upload");
				return;
			} 
			
			FileService fileService = FileServiceFactory.getFileService();
			AppEngineFile file = fileService.createNewBlobFile("application/octet-stream");
			FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
			writeChannel.write(ByteBuffer.wrap(bytes));
			writeChannel.closeFinally();
			
			BlobKey key = fileService.getBlobKey(file);
			System.out.println(user.getId());
			EJGame game = new EJGame(name, user.getId(), key);
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			pm.makePersistent(game);
			
			System.out.println(game.getId());
			
			boolean success = FileUtils.writeObject(resp, game.getInfo());
			
			if (!success) {
				LoginUtils.sendBadRequestError(resp, 
						"Error serving game");
				return;
			}
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {
		doGet(req, resp);
	}
	
}
