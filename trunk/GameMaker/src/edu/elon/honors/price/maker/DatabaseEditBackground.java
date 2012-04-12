package edu.elon.honors.price.maker;

import java.util.LinkedList;
import java.util.List;

import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
import edu.elon.honors.price.maker.SelectorMapBase.SelectorMapView;

public class DatabaseEditBackground extends DatabaseActivity {

	RadioGroup groupGround, groupSky;
	LinearLayout layoutUsed, layoutUnused;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_edit_background);

		setDefaultButtonActions();

		LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutMap);
		SelectorMapView mv = new SelectorMapView(this, game) {
			@Override
			protected boolean showRightButton() {
				return false;
			}

			@Override
			protected int getBackgroundTransparency() {
				return 255;
			}
		};
		ll.addView(mv);

		layoutUsed = (LinearLayout)findViewById(R.id.linearLayoutUsed);
		layoutUnused = (LinearLayout)findViewById(R.id.linearLayoutUnused);
		groupSky = (RadioGroup)findViewById(R.id.radioGroupSky);
		groupGround = (RadioGroup)findViewById(R.id.radioGroupGround);

		final Map map = game.getSelectedMap();

		List<String> bgs = Data.getResources(Data.BACKGROUNDS_DIR, this);
		List<String> mgs = Data.getResources(Data.MIDGROUNDS_DIR, this);
		List<String> fgs = Data.getResources(Data.FOREGROUNDS_DIR, this);
		List<String> used = map.midGrounds;

		for (String mg : used) {
			mgs.remove(mg);
		}

		
		
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

		for (String mg : used) {
			final String fmg = mg;
			TextView tv = new TextView(this);
			tv.setText(mg);
			tv.setTextSize(20);
			layoutUsed.addView(tv);
		}

		for (String mg : mgs) {
			TextView tv = new TextView(this);
			tv.setText(mg);
			tv.setTextSize(20);
			layoutUnused.addView(tv);
		}
	}
}

