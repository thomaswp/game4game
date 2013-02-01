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
import com.eujeux.data.WebSettings.SortType;
import com.google.appengine.api.datastore.Cursor;


public class BrowseServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	int MAX_REQUEST = 30;

	@Override
	protected void doFetch(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		String cursorString = req.getParameter("cursor");
		boolean desc = IOUtils.getBoolParameter(req, "desc");
		
		String sortTypeS = getForcedParameter(req, "sort");
		SortType sortType = SortType.valueOf(sortTypeS);
		if (sortType == null) {
			throw new BadRequest("No sort type");
		}
		
		int count = IOUtils.getIntParameter(req, "count");
		if (count > MAX_REQUEST) {
			LoginUtils.sendBadRequestError(resp, "Request too large");
			return;
		}
		
		
		String sort;
		if (sortType == SortType.UploadDate) {
			sort = "uploadDate";
		} else if (sortType == SortType.Downloads) {
			sort = "downloads";
		} else {
			sort = "rating";
		}
		sort += " " + (desc ? "desc" : "asc");
		
		List<EJGame> games = QueryUtils.queryRange(pm, EJGame.class, 
				sort, count, cursorString);
		Cursor cursor = JDOCursorHelper.getCursor(games);
		
		LinkedList<GameInfo> infos = new LinkedList<GameInfo>();
		for (EJGame game : games) infos.add(game.getInfo());
		
		GameList list = new GameList(infos, cursor.toWebSafeString());
		IOUtils.writeObject(resp, list);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {

	}

	@Override
	protected void doCreate(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
	
	}
	
}
