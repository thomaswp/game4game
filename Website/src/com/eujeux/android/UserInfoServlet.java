package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.QueryUtils;
import com.eujeux.data.EJUser;


public class UserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String username = req.getParameter("user");
		EJUser user = QueryUtils.queryUnique(EJUser.class, "userName == %s", username);
		
		if (user != null) {
			ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
			oos.writeObject(user.getInfo());
		}
	}
	
}
