package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.LoginUtils;
import com.eujeux.data.EJUser;

public class MyInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		EJUser user = LoginUtils.getUser();
		if (user == null) return;
		
		ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
		oos.writeObject(user.getMyInfo());
	}
}
