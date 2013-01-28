package com.eujeux;

import java.io.IOException;
import java.net.URL;

import javax.jdo.PersistenceManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eujeux.data.EJUser;
import com.eujeux.data.WebSettings;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.contacts.ContactFeed;


public class LoginUtils {
	
	public final static int TIMEOUT = 1000 * 60 * 60; //Reauthenticates every hour
	public final static boolean LOCAL = WebSettings.LOCAL;
	public final static String COOKIE_NAME = WebSettings.COOKIE_NAME;
	
	public static String getUserId(String token)
	{
		if (token == null) return null;
		
	    ContactsService contactsService = new ContactsService("Taxi");
	    contactsService.setUserToken(token);

	    IFeed feed = null;
	    try {
	        feed = contactsService.getFeed(new URL("https://www.google.com/m8/feeds/contacts/default/full?max-results=1"), ContactFeed.class);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	    if (feed == null)
	        return null;

	    String externalId = feed.getId();
//	    IPerson person = feed.getAuthors().get(0);
//	    String email = person.getEmail();
//	    String name = person.getName();
//	    String nameLang = person.getNameLang();

	    return externalId;
	}
	
	public static void sendNoUserError(HttpServletResponse resp) 
			throws IOException {
		String message = "Must be logged in to access this resource.";
		resp.sendError(HttpServletResponse.SC_FORBIDDEN, 
				message);
		Debug.write(message);
	}
	
	public static void sendBadRequestError(HttpServletResponse resp, String message) 
			throws IOException {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
		Debug.write(message);
	}
	
	public static EJUser getUser() {
		return getUser(false);
	}
	
	public static EJUser getUser(boolean createNew) {
		UserService us = UserServiceFactory.getUserService();
		User user = us.getCurrentUser();
		if (user == null) return null;
		return getUserByEmail(user.getEmail(), createNew);
	}
	
	public static EJUser getUserByEmail(String email, boolean createNew) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		EJUser user = QueryUtils.queryUnique(pm, EJUser.class, "email == %s", email);

		if (user == null && createNew) {
			user = new EJUser(email);
			pm.makePersistent(user);
			user.generateDefaultName();
		}
		pm.close();
		
		return user;
	}
	
	public static EJUser getUserByToken(String token) {
		if (token == null) return null;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Debug.write(token);
		
		EJUser user = QueryUtils.queryUnique(pm, EJUser.class, 
				"lastToken == %s && lastLogin > %s", token, 
				System.currentTimeMillis() - TIMEOUT);
		
		if (user != null) {
			user.setLastLogin(System.currentTimeMillis());
			pm.close();
			Debug.write("token");
			return user;
		}
		
		String email = getUserId(token);
		if (email == null) return null;
		
		user = QueryUtils.queryUnique(pm, EJUser.class, 
				"email == %s", email);
		
		if (user != null) {
			user.setLastLogin(System.currentTimeMillis());
			user.setLastToken(token);
			pm.close();
			Debug.write("email");
			return user;
		}
		
		user = new EJUser(email);
		user.setLastToken(token);
		pm.makePersistent(user);
		pm.close();
		Debug.write("new");
		
		return user;
	}
}
