package edu.elon.honors.price.maker.share;

import com.eujeux.data.GameInfo;
import com.eujeux.data.MyUserInfo;
import com.eujeux.data.UserInfo;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.share.DataUtils.FetchCallback;
import edu.elon.honors.price.maker.share.LoginUtils.LoginCallback;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@AutoAssign
public class MyGamesActivity extends Activity implements IViewContainer {

	private static final int DIALOG_ACCOUNTS = 0;
	private static final int DIALOG_CONFIRM_LOGIN = 1;
	private static final int DIALOG_FAIL = 2;
	private static final int DIALOG_LOADING = 3;

	private Button buttonLogin;
	private TextView textViewEmail;
	private EditText editTextUsername;
	private LinearLayout linearLayoutGames;
	private ProgressDialog progressDialog;
	
	private boolean loggedIn;
	private Handler handler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_games);

		AutoAssignUtils.autoAssign(this);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Logging in");
		progressDialog.setMessage("Logging in to you account... Please wait.");
		progressDialog.setCancelable(true);
		progressDialog.setIndeterminate(true);

		buttonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (loggedIn) {
					logOut();
				} else {
					logIn();
				}
			}
		});

		if (LoginUtils.hasLogin(this)) {
			logIn();
		}
	}



	@Override
	protected Dialog onCreateDialog(int id, final Bundle args) {
		if (id == DIALOG_ACCOUNTS) {
			return new AlertDialog.Builder(this)
			.setTitle("Choose account")
			//.setMessage("Choose a Google Account with which to log in:")
			.setItems(LoginUtils.getAccountNames(this), new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					loginWithAccountIndex(which);
				}
			})
			.setNeutralButton("Cancel", new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelLogin();
				}
			})
			.setOnCancelListener(new Dialog.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancelLogin();
				}
			})
			.create();
		} else if (id == DIALOG_CONFIRM_LOGIN) {
			return new AlertDialog.Builder(this)
			.setTitle("Permission Request")
			.setMessage("We are about to request permission to access your Google Account. " +
					"This for identification purposes only and lets us use your email " +
					"username to log on to our website, without sharing your passwords or " +
					"personal information. " +
					"The permission will be called \"Google App Engine.\" To continue " +
					"click Ok.")
					.setPositiveButton("Ok", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Account account = (Account)args.get("account");
							LoginUtils.logIn(MyGamesActivity.this, account, new LoginCallbackHandler());
						}
					})
					.setOnCancelListener(new Dialog.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							cancelLogin();
						}
					})
					.setNeutralButton("Cancel", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancelLogin();
						}
					})
					.create();
		} else if (id == DIALOG_FAIL) {
			return new AlertDialog.Builder(this)
			.setTitle("Login Failed")
			.setMessage("Login failed! Please ensure your device is connected " +
					"to the internet and that you have accepted all requested " +
					"permissions and try again.")
			.setNeutralButton("Ok", null)
			.create();
		} else if (id == DIALOG_LOADING) {
			return progressDialog;
		}
		return null;
	}

	private void loginWithAccountIndex(int index) {
		Account account = LoginUtils.getAccount(this, index);
		showDialog(DIALOG_LOADING);
		LoginUtils.tryLogIn(this, account, new LoginCallbackHandler());
	}

	private void loginCompleted(boolean success) {
		if (success) {
			buttonLogin.setEnabled(true);
			buttonLogin.setText("Log Out");
			loggedIn = true;

			DataUtils.fetchMyUserInfo(this, 
					new FetchCallback<MyUserInfo>() {
				@Override
				public void fetchComplete(MyUserInfo result) {
					editTextUsername.setVisibility(View.VISIBLE);
					editTextUsername.setText(result.userName);
					textViewEmail.setText(result.email);
					linearLayoutGames.removeAllViews();
					for (GameInfo info : result.games) {
						GameView gameView = new GameView(
								MyGamesActivity.this, info);
						linearLayoutGames.addView(gameView);
					}
				}
			});
		} else {
			loginFailed();
		}
	}

	private void loginFailed() {
		cancelLogin();
		LoginUtils.logOut(this);
		showDialog(DIALOG_FAIL);
	}

	private void cancelLogin() {
		buttonLogin.setEnabled(true);
	}

	private void logIn() {
		buttonLogin.setEnabled(false);

		Account account = LoginUtils.getCurrentAccount(this); 
		if (account != null) {
			showDialog(DIALOG_LOADING);
			LoginUtils.tryLogIn(this, account, new LoginCallbackHandler());
		} else {
			showDialog(DIALOG_ACCOUNTS, null);
		}
	}

	private void logOut() {
		buttonLogin.setText("Log In");
		LoginUtils.logOut(this);
		loggedIn = false;
		editTextUsername.setVisibility(View.INVISIBLE);
		editTextUsername.setText("");
		textViewEmail.setText("");
		linearLayoutGames.removeAllViews();
	}

	private class LoginCallbackHandler implements LoginCallback {
		@Override
		public void loginFinished(final boolean success) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					loginCompleted(success);
				}
			});
		}

		@Override
		public void loginNeedPermission(Account account) {
			final Bundle bundle = new Bundle();
			bundle.putParcelable("account", account);
			handler.post(new Runnable() {
				@Override
				public void run() {
					showDialog(DIALOG_CONFIRM_LOGIN, bundle);
				}
			});
		}
	}
}
