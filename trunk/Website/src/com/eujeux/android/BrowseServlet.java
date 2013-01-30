package com.eujeux.android;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.eujeux.IOUtils;
import com.eujeux.LoginUtils;
import com.eujeux.PMF;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.EJUser;
import com.eujeux.data.GameInfo;
import com.eujeux.data.GameList;
import com.google.appengine.api.datastore.Cursor;


public class BrowseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	int MAX_REQUEST = 30;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		int count = IOUtils.getIntParameter(req, resp, "count");
		String cursorString = req.getParameter("cursor");
		
		if (count > MAX_REQUEST) {
			LoginUtils.sendBadRequestError(resp, "Request too large");
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		List<EJGame> games = QueryUtils.queryRange(pm, EJGame.class, 
				"uploadDate desc", count, cursorString);
		Cursor cursor = JDOCursorHelper.getCursor(games);
		
		LinkedList<GameInfo> infos = new LinkedList<GameInfo>();
		for (EJGame game : games) infos.add(game.getInfo());

		pm.close();
		
		GameList list = new GameList(infos, cursor.toWebSafeString());
		IOUtils.writeObject(resp, list);
	}
	
}
