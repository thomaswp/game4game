package edu.elon.honors.price.maker.share;

import java.util.EnumSet;

import com.eujeux.data.GameInfo;
import com.eujeux.data.RatingInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.GameCache;
import edu.elon.honors.price.data.GameCache.GameType;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.GameCache.GameDetails;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.MainMenu;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.SaveableActivity;
import edu.elon.honors.price.maker.share.DataUtils.CreateCallback;
import edu.elon.honors.price.maker.share.DataUtils.FetchCallback;
import edu.elon.honors.price.maker.share.DataUtils.PostCallback;

@AutoAssign
public class WebEditGame extends SaveableActivity implements IViewContainer {

	private EditText editTextName, editTextDescription;
	private TextView textViewName, textViewDescription, textViewVersion, textViewDateCreated, textViewCreator, textViewDownloads; 
	private Button buttonPublish, buttonDownload;
	
	private TextView textViewRating, textViewCreative, textViewImpressive, textViewFun;
	private Button buttonPlusCreative, buttonPlusImpressive, buttonPlusFun;
	
	private RatingInfo ratings;
	private boolean ratingsChanged;
	private boolean downloaded;
	
	private GameInfo gameInfo;
	private boolean owner;
	private GameDetails gameDetails;
	private GameCache gameCache;

	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web_edit_game);

		gameInfo = getCancelResult();
		gameCache = GameCache.getGameCache(this);

		AutoAssignUtils.autoAssign(this);

		owner = gameInfo.creatorId == LoginUtils.getUserId(this);

		loadingDialog = DialogUtils.createLoadingDialog(this);

		loadInfo();

		setDefaultButtonActions();

		buttonDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				download();
			}
		});

		buttonPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publish();
			}
		});
		
		loadRatings();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		return DialogUtils.handleDialog(this, id, args);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		super.onPrepareDialog(id, dialog, args);
		DialogUtils.prepareDialog(id, dialog, args);
	}
	
	private void loadRatings() {
		if (owner || !downloaded) return;
		
		DataUtils.fetchRating(this, gameInfo.id, LoginUtils.getUserId(this), new FetchCallback<RatingInfo>() {

			@Override
			public void fetchComplete(RatingInfo result) {
				ratings = result;
				
				Debug.write(result.toString());
				
				buttonPlusCreative.setVisibility(View.VISIBLE);
				buttonPlusImpressive.setVisibility(View.VISIBLE);
				buttonPlusFun.setVisibility(View.VISIBLE);
				
				buttonPlusCreative.setEnabled(!result.plusCreative);
				buttonPlusImpressive.setEnabled(!result.plusImpressive);
				buttonPlusFun.setEnabled(!result.plusFun);
				
				Button[] plusButtons = new Button[] {
						buttonPlusCreative, 
						buttonPlusImpressive, 
						buttonPlusFun
				};
				
				for (Button plusButton : plusButtons) {
					plusButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
							if (v == buttonPlusCreative) {
								ratings.plusCreative = true;
								gameInfo.ratingCreative++;
							} else if (v == buttonPlusImpressive) {
								ratings.plusImpressive = true;
								gameInfo.ratingImpressive++;
							} else {
								ratings.plusFun = true;
								gameInfo.ratingFun++;
							}

							v.setEnabled(false);
							ratingsChanged = true;
							gameInfo.rating++;
							loadInfo();
						}
					});
				}
			}

			@Override
			public void fetchFailed(String error) {
				
			}
		});
	}

	private void download() {
		loadingDialog.show();
		DataUtils.fetchGameBlob(WebEditGame.this, gameInfo, new FetchCallback<PlatformGame>() {
			@Override
			public void fetchFailed(String message) {
				DialogUtils.showErrorDialog(WebEditGame.this, 
						"Download Failed", "Could not download the " +
								"requested game. Check your connection " +
								"and try again later.", message);
				loadingDialog.dismiss();
			}

			@Override
			public void fetchComplete(PlatformGame result) {
				loadingDialog.dismiss();
				buttonDownload.setEnabled(false);
				try {
					GameInfo cancelResult = getCancelResult();
					cancelResult.downloads++;
					setCancelResult(cancelResult);

					gameInfo.downloads++;
					if (gameDetails != null) {
						gameCache.updateGame(gameDetails, result, gameInfo, WebEditGame.this);	
					} else {
						GameType type = owner ? GameType.Edit : GameType.Play;
						gameDetails = gameCache.addGame(gameInfo.name, 
								type, result, gameInfo, WebEditGame.this);
					}
					
					loadInfo();
					DialogUtils.createAlertDialog(WebEditGame.this, 
							"Download Successful", 
							"The game has been successfully downloaded.").show();

				} catch (Exception e) {
					Debug.write(e);
					DialogUtils.createAlertDialog(WebEditGame.this, 
							"Download Failed", 
							"The game has been successfully downloaded, " +
							"but could not be saved. Try again later.").show();
				}
			}
		});
	}

	private void publish() {
		final PlatformGame game = gameDetails.loadGame(this);
		if (game == null) {
			DialogUtils.showErrorDialog(WebEditGame.this, 
					"No game to publish", 
					"An error has occured, and this game cannot be found on " +
					"this device.");
			return;
		}
		//Try to update gameinfo first...

		new AlertDialog.Builder(this)
		.setTitle("Publish Update - Cannot be Undone")
		.setItems(new String[] {"Major Update",  "Minor Update" }, 
				new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, final int which) {
				loadingDialog.show();
				onFinishing();
				DataUtils.updateGame(WebEditGame.this, gameInfo, 
						new PostCallback() {
					@Override
					public void postComplete() {
						DataUtils.updateGame(WebEditGame.this, game, which == 0, 
								new CreateCallback<GameInfo>() {
							@Override
							public void createComplete(GameInfo result) {
								if (result == null) {
								} else {
									loadingDialog.dismiss();
									gameInfo = result;
									setCancelResult(result);
									gameCache.updateGame(gameDetails, game, result, WebEditGame.this);
									loadInfo();
								}
							}

							@Override
							public void createFailed(String error) {
								publishFailed(error);
							}
						});
					}

					@Override
					public void postFailed(String error) {
						publishFailed(error);
					}
				});
			}
		})
		.show();
	}

	private void publishFailed(String error) {
		loadingDialog.dismiss();
		DialogUtils.showErrorDialog(WebEditGame.this, 
				"Publish Failed", 
				"Failed to publish game. Check connection and try again", error);
	}

	private void setCancelResult(GameInfo info) {
		getIntent().putExtra("gameInfo", info.clone());
	}

	public GameInfo getCancelResult() {
		return (GameInfo)getIntent().getExtras().getSerializable("gameInfo");
	}

	@Override
	public void finishCancel() {
		setResult(RESULT_OK, getIntent());
		super.finishCancel();
	}

	public static void startForResult(Activity activity, int requestCode, GameInfo gameInfo) {
		Intent intent = new Intent(activity, WebEditGame.class);
		intent.putExtra("gameInfo", gameInfo);
		activity.startActivityForResult(intent, requestCode);
	}

	private void loadInfo() {
		editTextName.setText(gameInfo.name);
		textViewName.setText(gameInfo.name);
		editTextDescription.setText(gameInfo.description);
		textViewDescription.setText(gameInfo.description);
		textViewVersion.setText(gameInfo.getVersionString());
		textViewDateCreated.setText(gameInfo.getUploadDateString());
		textViewCreator.setText(gameInfo.creatorName);
		textViewDownloads.setText("" + gameInfo.downloads);

		textViewRating.setText("+" + gameInfo.rating);
		textViewCreative.setText("+" + gameInfo.ratingCreative);
		textViewImpressive.setText("+" + gameInfo.ratingImpressive);
		textViewFun.setText("+" + gameInfo.ratingFun);
		
		editTextName.setVisibility(owner ? View.VISIBLE : View.GONE); 
		editTextDescription.setVisibility(owner ? View.VISIBLE : View.GONE);
		textViewName.setVisibility(owner ? View.GONE : View.VISIBLE);
		textViewDescription.setVisibility(owner ? View.GONE : View.VISIBLE);

		buttonPublish.setEnabled(false);

		GameCache gameCache = GameCache.getGameCache(this);
		Iterable<GameDetails> games = gameCache.getGames(
				EnumSet.of(GameType.Edit, GameType.Play));
		for (GameDetails details : games) {
			if (details.hasWebisiteId(gameInfo.id)) {
				gameDetails = details;
				downloaded = true;
				if (details.lastEdited < gameInfo.lastEdited) {
					buttonDownload.setText("Update Version");
				} else {
					buttonDownload.setEnabled(false);
				}
				if (details.lastEdited > gameInfo.lastEdited && owner) {
					buttonPublish.setEnabled(true);
				}
				break;
			}
		}

	}

	@Override
	public boolean hasChanged() {
		if (owner) {
			GameInfo oldInfo = getCancelResult();
			return !(gameInfo.name.equals(oldInfo.name) &&
					gameInfo.description.equals(oldInfo.description));
		} else {
			return ratingsChanged;
		}
	}

	@Override
	public void onFinishing() {
		gameInfo.name = editTextName.getText().toString();
		gameInfo.description = editTextDescription.getText().toString();
	}

	@Override
	public void finishOk(final Intent data) {
		if (owner) {
			if (hasChanged()) {
				loadingDialog.show();
				DataUtils.updateGame(this, gameInfo, new PostCallback() {
					@Override
					public void postComplete() {
						loadingDialog.dismiss();
						data.putExtra("gameInfo", gameInfo);
						if (gameDetails != null) {
							PlatformGame game = gameDetails.loadGame(WebEditGame.this);
							if (game != null) {
								gameCache.updateGame(gameDetails, game, 
										gameInfo, WebEditGame.this);
							}
						}
						WebEditGame.super.finishOk(data);
					}
	
					@Override
					public void postFailed(String error) {
						DialogUtils.showErrorDialog(WebEditGame.this, "Update Failed",
								"Failed to update game info. Check connection " +
								"and try again.", error);
					}
				});
			} else {
				data.putExtra("gameInfo", gameInfo);
				super.finishOk(data);
			}
		} else if (ratingsChanged) {
			loadingDialog.show();
			DataUtils.updateRating(this, ratings, new PostCallback() {
				@Override
				public void postFailed(String error) {
					loadingDialog.dismiss();
					DialogUtils.showErrorDialog(WebEditGame.this, "Rating Failed", 
							"Failed to update ratings.", error);
				}
				
				@Override
				public void postComplete() {
					loadingDialog.dismiss();
					data.putExtra("gameInfo", gameInfo);
					WebEditGame.super.finishOk(data);
				}
			});
		} else {
			super.finishCancel();
		}
	}
}
