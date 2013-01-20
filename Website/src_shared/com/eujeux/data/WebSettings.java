package com.eujeux.data;

public class WebSettings {
	public final static int VERSION = 1;
	
	public final static String IP = "192.168.0.12";
	public final static boolean LOCAL = true;
	public final static String LOCAL_SITE = "http://" + IP + ":8888";
	public final static String APP_SITE = "https://eujeux-test.appspot.com";
	public final static String SITE = LOCAL ? LOCAL_SITE : APP_SITE;
	public final static String ACSID = "SACSID";
	public final static String DEV_LOGIN = "dev_appserver_login";
	public final static String COOKIE_NAME = LOCAL ? DEV_LOGIN : ACSID;
	
	public final static int MAX_USERNAME_LENGTH = 20;
	
	public final static String PARAM_ACTION = "action";
	public final static String ACTION_FETCH = "fetch";
	public final static String ACTION_POST = "post";
}
