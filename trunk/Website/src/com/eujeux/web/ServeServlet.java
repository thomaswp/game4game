package com.eujeux.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.PMF;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class ServeServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			String idS = req.getParameter("id");
			if (idS != null) {
				long id = Long.parseLong(idS);
				EJGame game = QueryUtils.queryUnique(EJGame.class, "id == %s", id);
				game.setDownloads(game.getDownloads() + 1);
				PMF.makePersistent(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BlobKey blobKey = new BlobKey(req.getParameter("blobKey"));
		blobstoreService.serve(blobKey, resp);
	}
}