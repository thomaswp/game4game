package com.eujeux.android;

import java.io.IOException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.IOUtils;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.EJRating;
import com.eujeux.data.EJUser;
import com.eujeux.data.RatingInfo;

public class RatingServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFetch(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		long gameId = IOUtils.getLongParameter(req, "gameId");
		long userId = IOUtils.getLongParameter(req, "userId");
		
		if (!user.getId().equals(userId)) {
			throw new BadRequest("No permission to access this rating");
		}
		
		
		EJRating rating = QueryUtils.queryUnique(pm, EJRating.class, 
				"gameId == %s && userId == %s", gameId, userId);
		
		if (rating == null) {
			rating = new EJRating(userId, gameId);
			pm.makePersistent(rating);
		}
		
		IOUtils.writeObject(resp, rating.getInfo());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {
		
		RatingInfo info = IOUtils.readObject(req, RatingInfo.class);
		
		if (info == null) {
			throw new BadRequest("No rating uploaded");
		}
		
		if (!user.getId().equals(info.userId)) {
			throw new BadRequest("No permission to access this rating");
		}
		
		EJRating rating = QueryUtils.queryUnique(pm, EJRating.class, 
				"gameId == %s && userId == %s", info.gameId, info.userId);
		if (rating == null) {
			throw new BadRequest("No permission to access this rating");
		}

		EJGame game = QueryUtils.queryUnique(EJGame.class, "id == %s", rating.getGameId());
		if (game == null) {
			throw new BadRequest("Game does not exist");
		}
		
		rating.update(info, game);
		pm.makePersistent(rating);
		pm.makePersistent(game);
	}

	@Override
	protected void doCreate(HttpServletRequest req, HttpServletResponse resp,
			EJUser user, PersistenceManager pm) throws IOException, BadRequest {

	}
}

