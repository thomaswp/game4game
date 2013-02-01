package edu.elon.honors.price.maker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import edu.elon.honors.price.game.Debug;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class TestActivity extends DatabaseActivity {

	String token;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		for (Account a : accounts) {
			Debug.write("%s: %s", a.name, a.type);
		}

		Bundle options = new Bundle();

		am.getAuthToken(
				accounts[0],                    // Account retrieved using getAccountsByType()
				"cp",            				// Auth scope
				options,                        // Authenticator-specific options
				this,                           // Your activity
				new OnTokenAcquired(),          // Callback called when a token is successfully acquired
				new Handler(new OnError()));
		
		
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//
//				String token = getUserToken(TestActivity.this);
//				Debug.write(token);
//			}
//		});

	}
	
	@SuppressWarnings("unused")
	public static String getUserToken(Activity activity)
	{
	    AccountManager accountManager = AccountManager.get(activity);
	    AccountManagerFuture<Bundle> amf = accountManager.getAuthTokenByFeatures("com.google", "cp", null, activity, Bundle.EMPTY, Bundle.EMPTY, null, null );

	    Bundle bundle = null;
	    try {
	        bundle = amf.getResult();
	        String name = (String) bundle.get(AccountManager.KEY_ACCOUNT_NAME);
	        String type = (String) bundle.get(AccountManager.KEY_ACCOUNT_TYPE);
	        String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	        return token;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	private class OnError implements Callback {
		@Override
		public boolean handleMessage(Message arg0) {
			Debug.write("Error: %s", arg0);
			return true;
		}

	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			try {
				// Get the result of the operation from the AccountManagerFuture.
				Bundle bundle = result.getResult();

				// The token is a named value in the bundle. The name of the value
				// is stored in the constant AccountManager.KEY_AUTHTOKEN.
				token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
				Debug.write(token);

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							
							
							
//							URL url = new URL("http://eujeux-test.appspot.com/eujeux");
//							//URL url = new URL("http://www.google.com");
//							URLConnection conn = (HttpURLConnection) url.openConnection();
//
//							conn.setRequestProperty("Authorization", "OAuth " + token);
//
//							Debug.write(conn.getContentLength());
//							InputStream is = conn.getInputStream();
//
//							String out = convertStreamToString(is);
//							Debug.write(out);
							
							HttpClient client = new DefaultHttpClient();
							
							HttpGet get = new HttpGet("https://192.168.1.108:8888/oauth?token=" + token);

							HttpResponse resp = client.execute(get);
							HttpEntity entity = resp.getEntity();
							Debug.write(EntityUtils.toString(entity));

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				t.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}
