package edu.elon.honors.price.maker.share;

import com.eujeux.data.GameInfo;

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
import edu.elon.honors.price.data.PlatformGame;
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
	private Button buttonOk, buttonCancel, buttonPublish, buttonDownload;
	
	private GameInfo gameInfo;
	private boolean owner;
	private String gamePath;
	
	private ProgressDialog loadingDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.web_edit_game);
		
		gameInfo = getCancelResult();
		
		AutoAssignUtils.autoAssign(this);
		
		//Debug.write("%d vs %d", gameInfo.creatorId, LoginUtils.getUserId(this));
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
	}
	
	private void download() {
		loadingDialog.show();
		DataUtils.fetchGameBlob(WebEditGame.this, gameInfo, new FetchCallback<PlatformGame>() {
			@Override
			public void fetchFailed() {
				DialogUtils.createAlertDialog(WebEditGame.this, 
						"Download Failed", "Could not download the " +
								"requested game. Check your connection " +
								"and try again later.").show();
				loadingDialog.dismiss();
			}
			
			@Override
			public void fetchComplete(PlatformGame result) {
				loadingDialog.dismiss();
				buttonDownload.setEnabled(false);
				String name = gamePath;
				if (name == null) {
					name = MainMenu.getNewMapName(
							WebEditGame.this, result.name);
				}
				try {
					GameInfo cancelResult = getCancelResult();
					cancelResult.downloads++;
					setCancelResult(cancelResult);
					
					gameInfo.downloads++;
					result.websiteInfo = gameInfo;
					Data.saveGame(name, WebEditGame.this, result);
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
		final PlatformGame game = (PlatformGame)Data.loadGame(gamePath, WebEditGame.this);
		if (game == null) {
			DialogUtils.createAlertDialog(WebEditGame.this, 
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
					public void postComplete(boolean success) {
						if (success) {
							DataUtils.updateGame(WebEditGame.this, game, which == 0, 
									new CreateCallback<GameInfo>() {
								@Override
								public void createComplete(GameInfo result) {
									if (result == null) {
										publishFailed();
									} else {
										loadingDialog.dismiss();
										gameInfo = result;
										setCancelResult(result);
										game.websiteInfo = result;
										Data.saveGame(gamePath, WebEditGame.this, game);
										loadInfo();
									}
								}
							});
						} else {
							publishFailed();
						}
					}
				});
			}
		})
		.show();
	}
	
	private void publishFailed() {
		loadingDialog.dismiss();
		DialogUtils.createAlertDialog(WebEditGame.this, 
				"Publish Failed", 
				"Failed to publish game. Check connection and try again");
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
		
		editTextName.setVisibility(owner ? View.VISIBLE : View.GONE); 
		editTextDescription.setVisibility(owner ? View.VISIBLE : View.GONE);
		textViewName.setVisibility(owner ? View.GONE : View.VISIBLE);
		textViewDescription.setVisibility(owner ? View.GONE : View.VISIBLE);
		
		buttonPublish.setEnabled(false);
		
		for (String file : MainMenu.getGameFilenames(this)) {
			PlatformGame game = (PlatformGame)Data.loadGame(file, this);
			if (game != null && game.hasWebisiteId(gameInfo.id)) {
				gamePath = file;
				if (game.lastEdited < gameInfo.lastEdited) {
					buttonDownload.setText("Update Version");
				} else {
					buttonDownload.setEnabled(false);
				}
				if (game.lastEdited > gameInfo.lastEdited && owner) {
					buttonPublish.setEnabled(true);
				}
				break;
			}
		}
		
	}
	
	@Override
	public boolean hasChanged() {
		if (!owner) return false;
		GameInfo oldInfo = getCancelResult();
		return !(gameInfo.name.equals(oldInfo.name) &&
				gameInfo.description.equals(oldInfo.description));
	}
	
	@Override
	public void onFinishing() {
		gameInfo.name = editTextName.getText().toString();
		gameInfo.description = editTextDescription.getText().toString();
	}
	
	@Override
	public void finishOk(final Intent data) {
		if (hasChanged()) {
			loadingDialog.show();
			DataUtils.updateGame(this, gameInfo, new PostCallback() {
				@Override
				public void postComplete(boolean success) {
					loadingDialog.dismiss();
					if (success) {
						data.putExtra("gameInfo", gameInfo);
						if (gamePath != null) {
							PlatformGame game = (PlatformGame)Data.loadGame(gamePath, WebEditGame.this);
							if (game != null) {
								game.websiteInfo = gameInfo;
								Data.saveGame(gamePath, WebEditGame.this, game);
							}
						}
						WebEditGame.super.finishOk(data);
					} else {
						new AlertDialog.Builder(WebEditGame.this)
						.setTitle("Update Failed")
						.setMessage("Failed to update game info. Check connection " +
								"and try again.")
						.setNeutralButton("Ok", null)
						.show();
					}
				}
			});
		} else {
			data.putExtra("gameInfo", gameInfo);
			super.finishOk(data);
		}
	}
}
