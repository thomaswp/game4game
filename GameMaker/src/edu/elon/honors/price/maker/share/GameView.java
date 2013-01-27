package edu.elon.honors.price.maker.share;

import java.text.SimpleDateFormat;

import com.eujeux.data.GameInfo;

import edu.elon.honors.price.maker.AutoAssign;
import edu.elon.honors.price.maker.AutoAssignUtils;
import edu.elon.honors.price.maker.IViewContainer;
import edu.elon.honors.price.maker.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@AutoAssign
public class GameView extends LinearLayout implements IViewContainer {

	private GameInfo info;
	private TextView textViewName, textViewCreator, 
	textViewDownloads, textViewCreated;
	
	public GameView(Context context, GameInfo info) {
		super(context);
		this.info = info;
		LayoutInflater inflator = LayoutInflater.from(context);
		inflator.inflate(R.layout.gameinfo, this);
		AutoAssignUtils.autoAssign(this);
		
		textViewName.setText(info.name + " v" +  info.getVersionString());
		textViewCreator.setText("Created by: " + info.creator.userName);
		textViewDownloads.setText("Downloads: " + info.downloads);
		textViewCreated.setText("Uploaded: " + info.getUploadDateString());
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), ShowGameActivity.class);
				intent.putExtra("gameInfo", GameView.this.info);
				getContext().startActivity(intent);
			}
		});
	}

}
