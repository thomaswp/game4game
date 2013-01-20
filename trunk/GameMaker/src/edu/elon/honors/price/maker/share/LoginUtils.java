package edu.elon.honors.price.maker.share;


import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.eujeux.data.WebSettings;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;

public class LoginUtils {

	public final static boolean LOCAL= WebSettings.LOCAL;
	public final static String SITE = WebSettings.SITE;
	public final static String ASCID = WebSettings.COOKIE_NAME;

	public final static String ACCOUNT_TYPE = "com.google";
	public final static String SCOPE = "ah";
	public final static String PREF = "currentAccount";
	public final static String PREF_ACCOUNT_KEY = "currentAccount";
	public final static String PREF_ACSID_KEY = "acsid";
	public final static String PREF_DOMAIN_KEY = "domain";

	public final static int TIMEOUT = 5000;
	
	private static Thread loginThread;

	public static AccountManager getManager(Context context) {
		return AccountManager.get(context);
	}

	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
	}

	public static String getACSID(Context context) {
		SharedPreferences pref = getPrefs(context);
		return pref.getString(PREF_ACSID_KEY, null);
	}

	/**
	 * Returns an HttpClient with the necessary cookies for accessing
	 * protected resources. Must be logged in to access the client.
	 */
	public static HttpClient getClient(Context context) {
		if (!hasLogin(context)) return null;
		SharedPreferences pref = getPrefs(context);
		BasicClientCookie cookie = new BasicClientCookie(ASCID, getACSID(context));
		cookie.setDomain(pref.getString(PREF_DOMAIN_KEY, ""));
		DefaultHttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		CookieStore store = client.getCookieStore();
		store.addCookie(cookie);
		return client;
	}

	/**
	 * Returns the Account from the last successful login. Cleared
	 * after logging out.
	 */
	public static Account getCurrentAccount(Context context) {
		SharedPreferences pref = getPrefs(context);
		String currentAccount = pref.getString(PREF_ACCOUNT_KEY, null);
		return getAccount(context, currentAccount);
	}

	public static String[] getAccountNames(Context context) {
		Account[] accounts = getManager(context).getAccountsByType(ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < accounts.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	public static Account getAccount(Context context, int index) {
		return getAccount(context, getAccountNames(context)[index]);
	}

	public static Account getAccount(Context context, String name) {
		if (name == null) return null;
		Account[] accounts = getManager(context).getAccountsByType(ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (account.name.equals(name)) {
				return account;
			}
		}
		return null;
	}

	/**
	 * Returns whether or not there exists a stored login account,
	 * including a name and ASCID.
	 * If an account is stored, there is every reason to expect that
	 * the user has given permissions. However, the stored token and ASCID
	 * may no longer be valid.
	 */
	public static boolean hasLogin(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		return pref.contains(PREF_ACCOUNT_KEY) && pref.contains(PREF_ACSID_KEY);
	}

	/**
	 * Clears any stored login information.
	 */
	public static void logOut(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.clear();
		editor.apply();
	} 

	public static boolean isLoggingIn() {
		return loginThread != null;
	}
	
	private static void checkLoginException() {
		if (isLoggingIn()) {
			throw new RuntimeException("Already logging in!");
		}
	}

	/**
	 * Attempts to log in, but if permissions are needed, it will call the
	 * appropriate method in the callback instead of continuing.
	 */
	public static void tryLogIn(final Activity context, final Account account, 
			final LoginCallback callback) {
		checkLoginException();

		loginThread = new Thread(new Runnable() {
			@Override
			public void run() {
				AccountManagerFuture<Bundle> amf = getManager(context).getAuthToken(
						account, SCOPE, false, null, null);
				try {
					Bundle bundle = amf.getResult();
					handleResule(context, callback, account, bundle);
				} catch (Exception e) {
					e.printStackTrace();
				}
				loginThread = null;
			}
		});
		loginThread.start();
	}

	/**
	 * Logs in without checking for necessary permissions first. 
	 */
	public static void logIn(final Activity context, final Account account, 
			final LoginCallback callback) {
		checkLoginException();

		loginThread = new Thread(new Runnable() {
			@Override
			public void run() {
				AccountManagerFuture<Bundle> amf = getManager(context).getAuthToken(
						account, SCOPE, null, context, null, null);

				try {
					Bundle bundle = amf.getResult();
					handleResule(context, callback, account, bundle);
				} catch (OperationCanceledException e) { 
					callback.loginFinished(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				loginThread = null;
			}
		});
		loginThread.start();
	}

	private static void handleResule(Activity context, LoginCallback callback, Account account, Bundle bundle) {
		if (bundle.containsKey(AccountManager.KEY_INTENT)) {
			//will only happen if called from tryLogin()
			callback.loginNeedPermission(account);
		} else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
			String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			logIn(context, account, token, callback);
		} else {
			callback.loginFinished(false);
		}
	}

	/**
	 * Logs in after permissions and a token have been acquired.
	 */
	public static void logIn(Activity context, Account account, String token, LoginCallback callback) {
		AccountManager am = getManager(context);

		//invalidate and get a new token, in case this one's expired
		am.invalidateAuthToken(ACCOUNT_TYPE, token);
		AccountManagerFuture<Bundle> amf = am.getAuthToken(
				account, SCOPE, null, context, null, null);

		try {
			Bundle bundle = amf.getResult();
			if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
				token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), TIMEOUT);

				HttpUriRequest request;

				if (LOCAL) {
					//If it's local, we just emulate a form post
					HttpPost httppost = new HttpPost(SITE + "/_ah/login?continue=" + SITE + "/android/login");
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("email", account.name));
					params.add(new BasicNameValuePair("isAdmin", "false"));
					httppost.setEntity(new UrlEncodedFormEntity(params));
					
					request = httppost;

				} else {

					HttpGet httpget = new HttpGet(SITE + "/_ah/login?continue=" + SITE + "/android/login&auth=" + token);
					request = httpget;
				}

				//Either way, it comes back with a cookie to snag
				HttpResponse resp = httpclient.execute(request);

				HttpEntity entity = resp.getEntity();
				String body = EntityUtils.toString(entity);

				if (body.length() > 0) {
					for(Cookie cookie : httpclient.getCookieStore().getCookies()) {
						if(cookie.getName().equals(ASCID)) {
							Debug.write("cookie: " + cookie);
							String acsid = cookie.getValue();

							SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
							Editor editor = prefs.edit();
							editor.putString(PREF_ACCOUNT_KEY, account.name);
							editor.putString(PREF_ACSID_KEY, acsid);
							editor.putString(PREF_DOMAIN_KEY, cookie.getDomain());
							editor.apply();

							callback.loginFinished(true);
							return;
						}
					}
				}	
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		callback.loginFinished(false);
	}

	public interface LoginCallback {
		public void loginNeedPermission(Account account);
		public void loginFinished(boolean success);
	}
}
