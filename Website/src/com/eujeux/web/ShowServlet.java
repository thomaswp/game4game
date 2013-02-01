package com.eujeux.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.EJUser;

@SuppressWarnings("serial")
public class ShowServlet extends HttpServlet {
	@Override
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.setContentType("text/html");
		resp.getWriter().println("<html>");
		setHeader(resp.getWriter());
		
		PrintWriter pw = resp.getWriter();
		
		String gameIdS = req.getParameter("game");
		Long gameId = gameIdS == null ? null : Long.parseLong(gameIdS);
		
		EJGame game = QueryUtils.queryUnique(EJGame.class, "id == %s", gameId);
		
		if (game == null) {
			pw.println("no such game");
		} else {
			EJUser creator = QueryUtils.queryUnique(EJUser.class, "id == %s", 
					game.getCreatorId());
			
			pw.println("<p>" + game.getName() + 
					" (v" + game.getMajorVersion() + "." + game.getMinorVersion() + ")" +
					" by " + creator.getUserName() + " " +
					"<a href=\"/serve?blobKey=" + game.getBlobKey().getKeyString() + "\">the game</a>" + 
					"</p> <br/>");
		}

		
		resp.getWriter().println("</html>");
	}
	
	private void setHeader(PrintWriter pw) {
		pw.println("<head>");
		pw.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"/stylesheets/main.css\" />");
		pw.println("</head>");
	}
}
