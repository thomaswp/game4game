package com.eujeux.android;

import java.io.IOException;

import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.IOUtils;
import com.eujeux.LoginUtils;
import com.eujeux.PMF;
import com.eujeux.data.EJUser;
import com.eujeux.data.WebSettings;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		EJUser user = LoginUtils.getUser(true);
		
		if (user != null) {
			resp.getWriter().write(user.getEmail());
		}
	}
}
