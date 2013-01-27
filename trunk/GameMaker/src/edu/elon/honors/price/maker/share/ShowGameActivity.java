package edu.elon.honors.price.maker.share;

import com.eujeux.data.GameInfo;

import android.app.Activity;
import android.app.AlertDialog;
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
import edu.elon.honors.price.maker.share.DataUtils.PostCallback;

@AutoAssign
public class ShowGameActivity extends SaveableActivity implements IViewContainer {

	private EditText editTextName, editTextDescription;
	private TextView textViewName, textViewDescription, textViewVersion, textViewDateCreated, textViewCreator, textViewDownloads; 
	private Button buttonOk, buttonCancel, buttonPublish, buttonDownload;
	
	private GameInfo gameInfo;
	private boolean owner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.show_game);
		
		gameInfo = (GameInfo)getIntent().getExtras().getSerializable("gameInfo");
		
		AutoAssignUtils.autoAssign(this);
		
		owner = gameInfo.creator.id == LoginUtils.getUserId(this);
		
		editTextName.setText(gameInfo.name);
		textViewName.setText(gameInfo.name);
		editTextDescription.setText(gameInfo.description);
		textViewDescription.setText(gameInfo.description);
		textViewVersion.setText(gameInfo.getVersionString());
		textViewDateCreated.setText(gameInfo.getUploadDateString());
		textViewCreator.setText(gameInfo.creator.userName);
		textViewDownloads.setText("" + gameInfo.downloads);
		
		editTextName.setVisibility(owner ? View.VISIBLE : View.GONE); 
		editTextDescription.setVisibility(owner ? View.VISIBLE : View.GONE);
		textViewName.setVisibility(owner ? View.GONE : View.VISIBLE);
		textViewDescription.setVisibility(owner ? View.GONE : View.VISIBLE);
		
		buttonPublish.setEnabled(owner);
		
		for (String file : MainMenu.getGameFilenames(this)) {
			if (((PlatformGame)Data.loadGame(file, this)).websiteId == gameInfo.id) {
				buttonDownload.setEnabled(false);
				break;
			}
		}
		
		setDefaultButtonActions();
		
		buttonDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//DataUtils.
			}
		});
	}
	
	@Override
	public boolean hasChanged() {
		if (!owner) return false;
		GameInfo oldInfo = (GameInfo)getIntent().getExtras().getSerializable("gameInfo");
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
			DataUtils.updateGame(this, gameInfo, new PostCallback() {
				@Override
				public void postComplete(boolean success) {
					if (success) {
						data.putExtra("gameInfo", gameInfo);
						ShowGameActivity.super.finishOk(data);
					} else {
						new AlertDialog.Builder(ShowGameActivity.this)
						.setTitle("Update Failed")
						.setMessage("Failed to update game info. Check connection " +
								"and try again.")
						.setNeutralButton("Ok", null)
						.show();
					}
				}
			});
		} else {
			super.finishOk(data);
		}
	}
}
