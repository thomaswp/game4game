package edu.elon.honors.price.maker.share;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.eujeux.data.MyUserInfo;
import com.eujeux.data.UserInfo;
import com.eujeux.data.WebSettings;

import edu.elon.honors.price.game.Debug;

import android.content.Context;
import android.os.AsyncTask;

public class DataUtils {
	public final static String SITE = LoginUtils.SITE;

	public final static String USER_INFO = SITE + "/android/userInfo";
	public final static String MY_USER_INFO = SITE + "/android/myInfo"; 

	public static void fetchUserInfo(Context context, String username, FetchCallback<UserInfo> callback) {
		FetchTask<UserInfo> task = new FetchTask<UserInfo>(context, USER_INFO, callback);
		task.execute("user=" + username);
	}

	public static void fetchMyUserInfo(Context context, FetchCallback<MyUserInfo> callback) {
		FetchTask<MyUserInfo> task = new FetchTask<MyUserInfo>(context, MY_USER_INFO, callback);
		task.execute(WebSettings.PARAM_ACTION + "=" + WebSettings.ACTION_FETCH);
	}

	public static void updateMyUserInfo(Context context, MyUserInfo info, PostCallback callback) {
		PostTask<MyUserInfo> task = new PostTask<MyUserInfo>(context, MY_USER_INFO, callback);
		task.execute(info);
	}

	public static interface PostCallback {
		public void postComplete(boolean success);
	}

	public static class PostTask<T> extends AsyncTask<T, Void, Void> {
		private PostCallback callback;
		private String url;
		private Context context;
		private boolean success;

		public PostTask(Context context, String url, PostCallback callback) {
			this.context = context;
			this.callback = callback;
			this.url = url;
		}
		
		@Override
		protected Void doInBackground(T... params) {
			HttpClient client = LoginUtils.getClient(context);
			if (client == null) return null;

			for (T param : params) {
				HttpPost post = new HttpPost(url + "?" + WebSettings.PARAM_ACTION + 
						"=" + WebSettings.ACTION_POST);
				try {
					ByteArrayOutputStream boas = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(boas);
					oos.writeObject(param);
					ByteArrayEntity bae = new ByteArrayEntity(boas.toByteArray());
					post.setEntity(bae);
					HttpResponse response = client.execute(post);
					success = response.getStatusLine().getStatusCode() == 200; 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			callback.postComplete(success);
		}
	}

	public static interface FetchCallback<T> {
		public void fetchComplete(T result);
		public void fetchFailed();
	}

	private static class FetchTask<T> extends AsyncTask<String, Void, Void> {

		private FetchCallback<T> callback;
		private String url;
		private Context context;
		private LinkedList<T> results = new LinkedList<T>();

		public FetchTask(Context context, String url, FetchCallback<T> callback) {
			this.context = context;
			this.callback = callback;
			this.url = url;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = LoginUtils.getClient(context);
			if (client == null) return null;

			for (String param : params) {
				HttpGet get = new HttpGet(url + "?" + param);
				try {
					HttpResponse resp = client.execute(get);
					ByteArrayOutputStream boas = new ByteArrayOutputStream();
					resp.getEntity().writeTo(boas);
					byte[] ba = boas.toByteArray();
					ByteArrayInputStream bais = new ByteArrayInputStream(ba);
					ObjectInputStream ois = new ObjectInputStream(bais);
					T item = (T)ois.readObject();
					results.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (results.size() == 0) {
				callback.fetchFailed();
			} else {
				for (T res : results) {
					callback.fetchComplete(res);
				}
			}
		}
	}
}
