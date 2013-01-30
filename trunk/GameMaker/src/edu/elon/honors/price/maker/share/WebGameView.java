package edu.elon.honors.price.maker.share;

import com.eujeux.data.GameInfo;

import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@AutoAssign
public class WebGameView extends LinearLayout implements IViewContainer {

	private GameInfo info;
	private TextView textViewName, textViewCreator, 
	textViewDownloads, textViewCreated, textViewDescription;
	
	public WebGameView(Activity context, GameInfo info) {
		super(context);
		setId((int)info.id);
		
		this.info = info;
		LayoutInflater inflator = LayoutInflater.from(context);
		inflator.inflate(R.layout.web_gameview, this);
		AutoAssignUtils.autoAssign(this);
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editGameInfo();
			}
		});
		
		populate();
	}
	
	public void editGameInfo() {
		WebEditGame.startForResult((Activity)getContext(), getId(), info);
	}
	
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			info = (GameInfo)data.getExtras().getSerializable("gameInfo");
			populate();
			return true;
		}
		return false;
	}
	
	private void populate() {
		textViewName.setText(info.name + " v" +  info.getVersionString());
		textViewCreator.setText("Created by: " + info.creatorName);
		textViewDownloads.setText("Downloads: " + info.downloads);
		textViewCreated.setText("Uploaded: " + info.getUploadDateString());
		textViewDescription.setText(info.description);
		
	}

	public void setGame(GameInfo info) {
		this.info = info;
		populate();
	}
}
