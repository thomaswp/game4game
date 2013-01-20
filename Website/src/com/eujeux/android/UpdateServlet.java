package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.PMF;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJData;

public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		
		ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
		try {
			EJData data = (EJData) ois.readObject();
			if (data.hasPermission()) {
				PersistenceManager pm = PMF.get().getPersistenceManager(); 
				pm.makePersistent(data);
				pm.close();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}
	
}
