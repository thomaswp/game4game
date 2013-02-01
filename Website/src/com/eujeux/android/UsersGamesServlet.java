package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.GameInfo;
import com.eujeux.data.EJUser;


public class UsersGamesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String username = req.getParameter("user");
		EJUser user = QueryUtils.queryUnique(EJUser.class, "userName == %s", username);
		
		if (user != null) {
			LinkedList<GameInfo> infos = new LinkedList<GameInfo>();
			
			List<EJGame> games = QueryUtils.query(EJGame.class, 
					"creatorId == %s", user.getId());
			
			for (EJGame game : games) {
				infos.add(game.getInfo());
			}
			
			ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
			oos.writeObject(infos);
		}
	}
	
}
