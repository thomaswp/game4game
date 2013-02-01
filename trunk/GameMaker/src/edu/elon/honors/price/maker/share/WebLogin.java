package edu.elon.honors.price.maker.share;

import java.util.LinkedList;
import java.util.List;

import com.eujeux.data.GameInfo;
import com.eujeux.data.MyUserInfo;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.MainMenu;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.share.DataUtils.CreateCallback;
import edu.elon.honors.price.maker.share.DataUtils.FetchCallback;
import edu.elon.honors.price.maker.share.LoginUtils.LoginCallback;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@AutoAssign
public class WebLogin extends Activity implements IViewContainer {

	private static final int DIALOG_ACCOUNTS = 0;
	private static final int DIALOG_CONFIRM_LOGIN = 1;
	private static final int DIALOG_LOADING = 3;
	private static final int DIALOG_ADD_GAME = 6;
	private static final int DIALOG_NO_GAMES = 7;
	private static final int DIALOG_NO_ACCOUNT = 9;
	private static final int DIALOG_ERROR = 10;

	private TextView textViewEmail;
	private EditText editTextUsername;
	private LinearLayout linearLayoutGames;
	private ProgressDialog progressDialog;
	private Button buttonLogin, buttonAddGame, buttonBrowse;

	private Handler handler = new Handler();

	private MyUserInfo userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_login);

		AutoAssignUtils.autoAssign(this);

		progressDialog = DialogUtils.createLoadingDialog(this);

		buttonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userInfo != null) {
					logOut();
				} else {
					logIn();
				}
			}
		});

		buttonAddGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addGame();
			}
		});

		buttonBrowse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WebLogin.this, WebBrowseGames.class);
				startActivity(intent);
			}
		});

		editTextUsername.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				changeUsername();
				return false;
			}
		});

		if (LoginUtils.hasLogin(this)) {
			logIn();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			for (int i = 0; i < linearLayoutGames.getChildCount(); i++) {
				((WebGameView)linearLayoutGames.getChildAt(i))
				.onActivityResult(requestCode, data);
			}
		}
	}

	private void addGame() {
		List<String> allGames = MainMenu.getGameFilenames(this);

		LinkedList<String> files = new LinkedList<String>();
		LinkedList<String> names = new LinkedList<String>();
		for (String gameName : allGames) {
			PlatformGame game = (PlatformGame)Data.loadGame(gameName, this);
			if (game.websiteInfo == null) {
				names.add(gameName.substring(MainMenu.PREFIX.length()));
				files.add(gameName);
			}
		}
		Bundle bundle = new Bundle();



		if (files.size() > 0) {
			String[] filesA = files.toArray(new String[files.size()]);
			String[] namesA = names.toArray(new String[names.size()]);
			bundle.putStringArray("files", filesA);
			bundle.putStringArray("names", namesA);

			showDialog(DIALOG_ADD_GAME, bundle);
		} else {
			showDialog(DIALOG_NO_GAMES);
		}
	}

	private void addGame(final String filename) {
		Debug.write(filename);
		final PlatformGame game = (PlatformGame)Data.loadGame(filename, this);
		showDialog(DIALOG_LOADING);
		DataUtils.createGame(this, game, new CreateCallback<GameInfo>() {
			@Override
			public void createComplete(GameInfo result) {
				progressDialog.dismiss();
				game.websiteInfo = result;
				Data.saveGame(filename, WebLogin.this, game);
				WebGameView gameView = new WebGameView(
						WebLogin.this, result);
				linearLayoutGames.addView(gameView);
				gameView.editGameInfo();
			}

			@Override
			public void createFailed(String error) {
				progressDialog.dismiss();
				DialogUtils.showErrorDialog(WebLogin.this,
						"Upload Failed", "Could not upload the game.", error);
			}
		});
	}

	private void changeUsername() {
		if (userInfo != null) {
			String name = editTextUsername.getText().toString();
			if (name != userInfo.userName) {
				if (MyUserInfo.validUsername(name)) {
					final String oldName = userInfo.userName;
					userInfo.userName = name;
					DataUtils.updateMyUserInfo(WebLogin.this, userInfo, new DataUtils.PostCallback() {

						@Override
						public void postComplete() { }

						@Override
						public void postFailed(String error) {
							userInfo.userName = oldName;
							editTextUsername.setText(oldName);
							DialogUtils.showErrorDialog(WebLogin.this,
									"Username Update Failed",
									"The name you have chosen is already in use. " +
									"Please choose another.");
						}
					});
				} else {
					DialogUtils.showErrorDialog(WebLogin.this, "Invalid Username",
							"Usernames must be 3-15 lowercase alphanumeric characters, " +
							"underscores (_) dashes (-), at signs (@) or periods (.). " +
							"Ex: user_name");
					editTextUsername.setText(userInfo.userName);
				}
			}
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
							LoginUtils.logIn(WebLogin.this, account, new LoginCallbackHandler());
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
		} else if (id == DIALOG_LOADING) {
			return progressDialog;
		} else if (id == DIALOG_ADD_GAME) {

			String[] names = args.getStringArray("names");
			final String[] files = args.getStringArray("files");

			return new AlertDialog.Builder(this)
			.setTitle("Choose game to upload")
			.setItems(names, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addGame(files[which]);
					removeDialog(DIALOG_ADD_GAME);
				}
			})
			.setNeutralButton("Cancel", null)
			.create();
		} else if (id == DIALOG_NO_GAMES) {
			return alertDialog("No Games", "There are no games to upload. " +
					"All games have already been uploaded.");
		} else if (id == DIALOG_NO_ACCOUNT) {
			return new AlertDialog.Builder(this)
			.setTitle("No Accounts")
			.setMessage("You have no Google accounts on this decive. " +
					"Would you like to add one?")
					.setPositiveButton("Ok", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(Settings.ACTION_SYNC_SETTINGS));
						}
					})
					.setNegativeButton("Not Now", null)
					.create();
		}
		return DialogUtils.handleDialog(this, id, args);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		super.onPrepareDialog(id, dialog, args);
		DialogUtils.prepareDialog(id, dialog, args);
		
	}

	private AlertDialog alertDialog(String title, String message) {
		return DialogUtils.createAlertDialog(this, title, message);
	}

	private void loginWithAccountIndex(int index) {
		Account account = LoginUtils.getAccount(this, index);
		showDialog(DIALOG_LOADING);
		LoginUtils.tryLogIn(this, account, new LoginCallbackHandler());
	}

	private void loginCompleted(boolean success) {
		if (success) {

			DataUtils.fetchMyUserInfo(this, 
					new FetchCallback<MyUserInfo>() {
				@Override
				public void fetchComplete(MyUserInfo result) {
					progressDialog.dismiss();
					buttonLogin.setText("Log Out");
					buttonLogin.setEnabled(true);
					LoginUtils.registerUser(WebLogin.this, result);
					userInfo = result;
					updateUIForLogin();
				}

				@Override
				public void fetchFailed(String error) {
					progressDialog.dismiss();
					loginFailed();
					DialogUtils.showErrorDialog(WebLogin.this, "Login Failed",
							"Login failed! Please ensure this app is " +
							"updated and try again.", error);
				}
			});
		} else {
			progressDialog.dismiss();
			loginFailed();
			DialogUtils.showErrorDialog(this, "Login Failed",
				"Login failed! Please ensure your device is connected " +
						"to the internet and that you have accepted all requested " +
						"permissions and try again.");
		}
	}

	private void loginFailed() {
		cancelLogin();
		LoginUtils.logOut(this);
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
			if (LoginUtils.getAccountNames(this).length > 0) {
				showDialog(DIALOG_ACCOUNTS);
			} else {
				cancelLogin();
				showDialog(DIALOG_NO_ACCOUNT);
			}
		}
	}

	private void logOut() {
		LoginUtils.logOut(this);
		userInfo = null;
		buttonLogin.setText("Log In");
		updateUIForLogin();
	}

	private void updateUIForLogin() {
		boolean loggedIn = userInfo != null;

		editTextUsername.setVisibility(loggedIn ? View.VISIBLE : View.INVISIBLE);
		editTextUsername.setText(loggedIn ? userInfo.userName : "");
		textViewEmail.setText(loggedIn ? userInfo.email : "");
		buttonAddGame.setEnabled(loggedIn);
		buttonBrowse.setEnabled(loggedIn);
		linearLayoutGames.removeAllViews();
		if (loggedIn) {
			for (GameInfo info : userInfo.games) {
				WebGameView gameView = new WebGameView(
						WebLogin.this, info);
				linearLayoutGames.addView(gameView);
			}
		}
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
