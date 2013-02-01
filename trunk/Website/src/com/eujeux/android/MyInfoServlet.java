package com.eujeux.android;

import java.io.IOException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.IOUtils;
import com.eujeux.data.EJUser;
import com.eujeux.data.MyUserInfo;
import com.eujeux.data.WebSettings;

public class MyInfoServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFetch(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		int version = IOUtils.getIntParameter(req, "version");
		if (version != WebSettings.VERSION) {
			throw new BadRequest("Outdated app version (v%d) does not match " +
							"website version (v%d)", version, WebSettings.VERSION);
		}
		
		boolean success = IOUtils.writeObject(resp, user.getMyInfo());
		if (!success) {
			throw new BadRequest("Error serving user info");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		
		MyUserInfo info = IOUtils.readObject(req, MyUserInfo.class);
		if (info == null) { 
			throw new BadRequest("No info uploaded");
		}
		
		if (user.update(info)) {
			pm.makePersistent(user);
		}
	}

	@Override
	protected void doCreate(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {

	}
}
