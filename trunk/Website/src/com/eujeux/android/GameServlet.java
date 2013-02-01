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

import com.eujeux.Debug;
import com.eujeux.IOUtils;
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

public class GameServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFetch(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		GameInfo info = IOUtils.readObject(req, GameInfo.class);
		if (info == null) { 
			throw new BadRequest("No game info sent");
		}
		
		EJGame game = QueryUtils.queryUnique(EJGame.class, "id == %s", info.id);
		if (game == null) {
			throw new BadRequest("No such game to update");
		}
		
		if (!game.getCreatorId().equals(user.getId())) {
			throw new BadRequest(
					"User does not have permission to update this game.");
		}
		
		game.update(info);
		pm.makePersistent(game);
	}

	@Override
	protected void doCreate(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		long lastEdited = IOUtils.getLongParameter(req, "lastEdited");
		
		byte[] bytes = IOUtils.readBytes(req);
		
		if (bytes.length == 0) {
			throw new BadRequest("No game to upload");
		} 
		
		BlobKey oldKey = null;
		
		EJGame game;
		String name = req.getParameter("name");
		if (name != null) {
			game = new EJGame(name, user.getId(), null);
		} else {
			long id = IOUtils.getLongParameter(req, "id");
			boolean majorUpdate = IOUtils.getBoolParameter(req, "majorUpdate");
			
			game = QueryUtils.queryUnique(pm, EJGame.class, "id == %s", id);
			
			if (game == null) { 
				throw new BadRequest("No game to update");
			}
			if (!game.getCreatorId().equals(user.getId())) {
				throw new BadRequest("No permission to publish an update");
			}

			oldKey = game.getBlobKey();
			if (majorUpdate) {
				game.setMinorVersion(0);
				game.setMajorVersion(game.getMajorVersion() + 1);
			} else {
				game.setMinorVersion(game.getMinorVersion() + 1);
			}
		}
		
		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile file = fileService.createNewBlobFile("application/octet-stream");
		FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
		writeChannel.write(ByteBuffer.wrap(bytes));
		writeChannel.closeFinally();
		
		BlobKey key = fileService.getBlobKey(file);
		
		game.setBlobKey(key);
		game.setLastEdited(lastEdited);
		pm.makePersistent(game);
		
		if (oldKey != null) {
			try {
				AppEngineFile oldFile = fileService.getBlobFile(oldKey);
				fileService.delete(oldFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		boolean success = IOUtils.writeObject(resp, game.getInfo(user.getInfo()));
		
		if (!success) {
			throw new BadRequest("Error serving game");
		}
	}
	
}
