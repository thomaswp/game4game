package com.eujeux.secure;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.LoginUtils;



public class OAuthTest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String token = req.getParameter("token");
		resp.getWriter().print( LoginUtils.getUserByToken(token).getUserName() );
	}
	
	
}
