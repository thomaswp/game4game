package com.eujeux.android;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.LoginUtils;
import com.eujeux.PMF;
import com.eujeux.data.EJUser;
import com.eujeux.data.WebSettings;


public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		EJUser user = LoginUtils.getUser(pm, false);

		if (user == null) {
			LoginUtils.sendNoUserError(resp);
			pm.close();
			return;
		}

		String action = req.getParameter(WebSettings.PARAM_ACTION);

		try {
			if (WebSettings.ACTION_FETCH.equals(action)) {
				doFetch(req, resp, user, pm);
			} else if (WebSettings.ACTION_POST.equals(action)) {
				doPost(req, resp, user, pm);
			} else if (WebSettings.ACTION_CREATE.equals(action)) {
				doCreate(req, resp, user, pm);
			} else if (action == null) {
				throw new BadRequest("No action requested");
			} else {
				throw new BadRequest("Unknown action: %s", action);
			}
		} catch (BadRequest e) {
			LoginUtils.sendBadRequestError(resp, e.getMessage());
		}

		pm.close();
	}
	
	protected String getForcedParameter(HttpServletRequest req, String name) 
		throws BadRequest {
		String p = req.getParameter(name);
		if (p == null) {
			throw new BadRequest("Missing parameter: %s", name);
		}
		return p;
	}
	
	protected abstract void doFetch(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest;
	
	protected abstract void doPost(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest;
	
	protected abstract void doCreate(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest;
	
	public static class BadRequest extends Exception {
		private static final long serialVersionUID = 1L;

		public BadRequest(String message) {
			super(message);
		}
		
		public BadRequest(String message, Object... params) {
			this(String.format(message, params));
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}
}
