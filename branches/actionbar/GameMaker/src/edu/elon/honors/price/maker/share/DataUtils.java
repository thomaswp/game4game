package edu.elon.honors.price.maker.share;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import com.eujeux.data.GameInfo;
import com.eujeux.data.GameList;
import com.eujeux.data.MyUserInfo;
import com.eujeux.data.RatingInfo;
import com.eujeux.data.UserInfo;
import com.eujeux.data.WebSettings;
import com.eujeux.data.WebSettings.SortType;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;

import android.content.Context;
import android.os.AsyncTask;

public class DataUtils {
	public final static String SITE = LoginUtils.SITE;

	public final static String USER_INFO = SITE + "/android/userInfo";
	public final static String MY_USER_INFO = SITE + "/android/myInfo";
	public final static String GAME = SITE + "/android/game"; 
	public final static String BLOB = SITE + "/android/serve";
	public final static String GAME_LIST = SITE + "/android/browse";
	public final static String RATING = SITE + "/android/rating"; 

	public static void fetchUserInfo(Context context, String username, FetchCallback<UserInfo> callback) {
		FetchTask<UserInfo> task = new FetchTask<UserInfo>(context, USER_INFO, callback);
		task.execute("user=" + username);
	}

	public static void fetchMyUserInfo(Context context, FetchCallback<MyUserInfo> callback) {
		FetchTask<MyUserInfo> task = new FetchTask<MyUserInfo>(context, MY_USER_INFO, callback);
		task.execute("version=" + WebSettings.VERSION);
	}
	
	public static void fetchGameBlob(Context context, GameInfo info, FetchCallback<PlatformGame> callback) {
		FetchTask<PlatformGame> task = new FetchTask<PlatformGame>(context, BLOB, callback);
		task.execute("blobKey=" + info.blobKeyString + "&id=" + info.id);
	}
	
	public static void fetchGameList(Context context, int count, String cursor, 
			SortType sortType, boolean sortDesc, FetchCallback<GameList> callback) {
		FetchTask<GameList> task = new FetchTask<GameList>(context, GAME_LIST, callback);
		String params = String.format(Locale.US, "count=%d&sort=%s&desc=%s", count, sortType.toString(), "" + sortDesc);
		if (cursor != null) {
			params += "&cursor=" + cursor;
		}
		task.execute(params);
	}
	
	public static void fetchRating(Context context, long gameId, long userId, FetchCallback<RatingInfo> callback) {
		FetchTask<RatingInfo> task = new FetchTask<RatingInfo>(context, RATING, callback);
		String params = String.format(Locale.US, "gameId=%d&userId=%d", gameId, userId);
		task.execute(params);
	}

	public static void updateRating(Context context, RatingInfo rating, PostCallback callback) {
		PostTask<RatingInfo> task = new PostTask<RatingInfo>(context, RATING, callback);
		task.execute(rating);
	}
	
	public static void updateMyUserInfo(Context context, MyUserInfo info, PostCallback callback) {
		PostTask<MyUserInfo> task = new PostTask<MyUserInfo>(context, MY_USER_INFO, callback);
		task.execute(info);
	}

	public static void updateGame(Context context, GameInfo info, PostCallback callback) {
		PostTask<GameInfo> task = new PostTask<GameInfo>(context, GAME, callback);
		task.execute(info);
	}
	
	public static void createGame(Context context, PlatformGame game, CreateCallback<GameInfo> callback) {
		String escapedName = URLEncoder.encode(game.name);
		
		CreateTask<PlatformGame, GameInfo> task = new CreateTask<PlatformGame, GameInfo>(context, 
				GAME + "?name=" + escapedName + "&lastEdited=" + game.lastEdited, callback);
		task.execute(game);
	}
	
	public static void updateGame(Context context, PlatformGame game, boolean majorUpdate, CreateCallback<GameInfo> callback) {
		CreateTask<PlatformGame, GameInfo> task = new CreateTask<PlatformGame, GameInfo>(context, 
			String.format("%s?lastEdited=%d&majorUpdate=%s&id=%d", GAME,
						game.lastEdited, "" + majorUpdate, game.websiteInfo.id), callback);
		task.execute(game);
	}
	
	private static String getResponseError(HttpResponse response) {
		StatusLine status = response.getStatusLine(); 
		if (status != null && status.getStatusCode() != 200) {
			Debug.write("Error %d: %s", status.getStatusCode(), status.getReasonPhrase());
			return status.getReasonPhrase();
		}
		return null;
	}

	public static interface CreateCallback<T> {
		public void createComplete(T result);
		public void createFailed(String error);
	}
	
	public static class CreateTask<T,S> extends AsyncTask<T, Void, Void> {
		private CreateCallback<S> callback;
		private String url;
		private Context context;
		private S result;
		private String failMessage;

		public CreateTask(Context context, String url, CreateCallback<S> callback) {
			this.context = context;
			this.callback = callback;
			this.url = url;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(T... params) {
			HttpClient client = LoginUtils.getClient(context);
			if (client == null) return null;

			for (T param : params) {
				String urlAndParams = url;
				urlAndParams += urlAndParams.contains("?") ? "&" : "?";
				urlAndParams += WebSettings.PARAM_ACTION + "=" + WebSettings.ACTION_CREATE;
				
				HttpPost post = new HttpPost(urlAndParams);
				try {
					ByteArrayOutputStream boas = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(boas);
					oos.writeObject(param);
					ByteArrayEntity bae = new ByteArrayEntity(boas.toByteArray());
					post.setEntity(bae);
					
					HttpResponse response = client.execute(post);
					failMessage = getResponseError(response);
					ObjectInputStream ois = new ObjectInputStream(
							response.getEntity().getContent());
					result = (S) ois.readObject();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (this.result != null) {
				callback.createComplete(this.result);
			} else {
				callback.createFailed(failMessage);
			}
		}
	}
	
	public static interface PostCallback {
		public void postComplete();
		public void postFailed(String error);
	}

	public static class PostTask<T> extends AsyncTask<T, Void, Void> {
		private PostCallback callback;
		private String url;
		private Context context;
		private boolean success;
		private String failMessage;

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
				String urlAndParams = url;
				urlAndParams += urlAndParams.contains("?") ? "&" : "?";
				urlAndParams += WebSettings.PARAM_ACTION + "=" + WebSettings.ACTION_POST;
				
				HttpPost post = new HttpPost(urlAndParams);
				try {
					ByteArrayOutputStream boas = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(boas);
					oos.writeObject(param);
					ByteArrayEntity bae = new ByteArrayEntity(boas.toByteArray());
					post.setEntity(bae);
					HttpResponse response = client.execute(post);
					failMessage = getResponseError(response);
					success = response.getStatusLine().getStatusCode() == 200; 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (success) {
				callback.postComplete();
			} else {
				callback.postFailed(failMessage);
			}
		}
	}

	public static interface FetchCallback<T> {
		public void fetchComplete(T result);
		public void fetchFailed(String error);
	}

	private static class FetchTask<T> extends AsyncTask<String, Void, Void> {

		private FetchCallback<T> callback;
		private String url;
		private Context context;
		private LinkedList<T> results = new LinkedList<T>();
		private String failMessage;

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
				String urlAndParams = url + "?" + param + "&" +
						WebSettings.PARAM_ACTION + "=" + WebSettings.ACTION_FETCH;
				HttpGet get = new HttpGet(urlAndParams);
				
				try {
					HttpResponse resp = client.execute(get);
					failMessage = getResponseError(resp);
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
			if (results.size() > 0) {
				for (T res : results) {
					callback.fetchComplete(res);
				}
			} else {
				callback.fetchFailed(failMessage);
			}
		}
	}
}
