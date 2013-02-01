package com.eujeux.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.eujeux.PMF;
import com.eujeux.data.EJGame;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(UploadServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

		try {
			resp.setContentType("text/plain");

			EJGame aGame = new EJGame();
			aGame.setCreatorId(Long.parseLong(req.getParameter("creatorId")));
			aGame.setName(req.getParameter("name"));
			aGame.setMajorVersion(Integer.parseInt(req.getParameter("version")));

			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
			List<BlobKey> blobKeyList = blobs.get("blob");
			BlobKey blobKey = blobKeyList.get(0);
			aGame.setBlobKey(blobKey);

			if (aGame.getBlobKey() != null) {
				// persist the game, if one exists
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					pm.makePersistent(aGame);
				} finally {
					pm.close();
				}
			} else {
				log.warning("blob key was not found.");
			}
		} catch (Exception ex) {

		}

		resp.sendRedirect("/eujeux");
	}
}
