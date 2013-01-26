package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.LoginUtils;
import com.eujeux.PMF;
import com.eujeux.data.EJUser;
import com.eujeux.data.MyUserInfo;
import com.eujeux.data.WebSettings;

public class MyInfoServlet extends HttpServlet {
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
		
		if (WebSettings.ACTION_POST.equals(action)) {
			ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
			try {
				MyUserInfo info = (MyUserInfo) ois.readObject();
				if (user.update(info)) {
					PMF.get().getPersistenceManager().makePersistent(user);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} else if (WebSettings.ACTION_FETCH.equals(action)) {
			ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
			oos.writeObject(user.getMyInfo());
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}
}
