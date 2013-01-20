package com.eujeux.data;

public class WebSettings {
	public final static String IP = "192.168.1.108";
	public final static boolean LOCAL = true;
	public final static String LOCAL_SITE = "http://" + IP + ":8888";
	public final static String APP_SITE = "https://eujeux-test.appspot.com";
	public final static String SITE = LOCAL ? LOCAL_SITE : APP_SITE;
	public final static String ACSID = "SACSID";
	public final static String DEV_LOGIN = "dev_appserver_login";
	public final static String COOKIE_NAME = LOCAL ? DEV_LOGIN : ACSID;
}
