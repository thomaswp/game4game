package com.eujeux.web;

/**
 * Servlet that shows a single user's games to that user.
 * 
 * @author J. Hollingsworth
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.*;

import com.eujeux.LoginUtils;
import com.eujeux.QueryUtils;
import com.eujeux.data.EJGame;
import com.eujeux.data.EJUser;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class EujeuxServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// get a user
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user == null) {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
			return;
		} 
		
		resp.setContentType("text/html");
		resp.getWriter().println("<html>");
		setHeader(resp.getWriter());
		
		PrintWriter pw = resp.getWriter();
		
		pw.println("<body>");
		pw.println("<p>Hello, " + user.getNickname() + 
				"! (You can <a href=\"" + userService.createLoginURL(req.getRequestURI()) +
				"\">sign out</a>.)</p>");
		
		EJUser ejUser = LoginUtils.getUserByEmail(user.getEmail(), true);
		
		List<EJGame> games = QueryUtils.query(EJGame.class, 
				"creatorId == %s", ejUser.getId());
		
		if (games.isEmpty()) {
			pw.println("<p>" + user.getNickname() + " has no games.</p>");
		} else {
			pw.println("<p>" + user.getNickname() + "'s games:");
			for (EJGame game: games) {
				pw.println("<p><b><a href=\"show?game=" + game.getId() + "\">" + game.getName() + "</a></b></p>");
			}
		}
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String blobStr = blobstoreService.createUploadUrl("/upload");
		
		pw.println("<form action=\"" + blobStr + "\")\" method=\"post\" enctype=\"multipart/form-data\">");
		pw.println("<input name=\"name\" type=\"text\" value=\"Name\"> <br/>");
		pw.println("<input name=\"version\" type=\"number\" value=\"1\" min=\"0\" max=\"100\"><br />");
		pw.println("<input name=\"blob\" type=\"file\" size=\"30\"> <br/>");
		pw.println("<input name=\"creatorId\" type=\"hidden\" value=\"" + ejUser.getId() + "\"> <br/>");
		pw.println("<input name=\"Submit\" type=\"submit\" value=\"Submit\"> <br/>");
		pw.println("</form>");
		
		pw.println("</body>");
		
		resp.getWriter().println("</html>");
	}
	
	private void setHeader(PrintWriter pw) {
		pw.println("<head>");
		pw.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"/stylesheets/main.css\" />");
		pw.println("</head>");
	}
}
