package edu.elon.honors.price.maker;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;

public class DatabaseEditMapBackground extends DatabaseActivity {

	RadioGroup groupGround, groupSky;
	SelectorMapPreview selectorMapPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_edit_map_background);

		setDefaultButtonActions();

		LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutMap);
		selectorMapPreview = new SelectorMapPreview(this, game, null);
		ll.addView(selectorMapPreview);

		groupSky = (RadioGroup)findViewById(R.id.radioGroupSky);
		groupGround = (RadioGroup)findViewById(R.id.radioGroupGround);

		final Map map = game.getSelectedMap();

		List<String> bgs = Data.getResources(Data.BACKGROUNDS_DIR, this);
		List<String> fgs = Data.getResources(Data.FOREGROUNDS_DIR, this);

		
		for (String bg : bgs) {
			final RadioButton radio = new RadioButton(this);
			final String fbg = bg;
			radio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					map.skyImageName = fbg;
				}
			});
			radio.setText(bg);
			if (map.skyImageName.equals(bg)) {
				radio.post(new Runnable() {
					@Override
					public void run() {
						radio.setChecked(true);
					}
				});
			}
			groupSky.addView(radio);
		}

		for (String fg : fgs) {
			final RadioButton radio = new RadioButton(this);
			final String ffg = fg;
			radio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					map.groundImageName = ffg;
				}
			});
			radio.setText(fg);
			if (map.groundImageName.equals(fg)) {
				radio.post(new Runnable() {
					@Override
					public void run() {
						radio.setChecked(true);
					}
				});
			}
			groupGround.addView(radio);
		}
	}
	
	@Override
	protected EditorButton getOkEditorButton() {
		return EditorButton.EditMapBackgroundOk;
	}
	
}

