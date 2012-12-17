package edu.elon.honors.price.maker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
				"ah",            				// Auth scope
				options,                        // Authenticator-specific options
				this,                           // Your activity
				new OnTokenAcquired(),          // Callback called when a token is successfully acquired
				new Handler(new OnError()));

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
							
							HttpGet get = new HttpGet("https://eujeux-test.appspot.com/eujeux");
							get.setHeader("Authorization", token);

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
