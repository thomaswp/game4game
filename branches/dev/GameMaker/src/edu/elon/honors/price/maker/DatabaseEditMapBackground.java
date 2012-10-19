package edu.elon.honors.price.maker;

import java.util.List;

import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DragNDropListView.DragNDropListener;
import com.ericharlow.DragNDrop.DropListener;
import com.ericharlow.DragNDrop.ScrollContainer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;

public class DatabaseEditMapBackground extends DatabaseActivity {

	RadioGroup groupGround, groupSky;
	DragNDropListView listUsed, listUnused;
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
		listUsed = ((ScrollContainer)findViewById(R.id.scrollContainerUsed)).getListView();
		listUnused = ((ScrollContainer)findViewById(R.id.scrollContainerUnused)).getListView();

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

		DragNDropGroup group = new DragNDropGroup();
		group.addListView(listUsed);
		group.addListView(listUnused);
		
		listUsed.addOnDragNDropListener(new DragNDropListener() {
			@Override
			public void onItemDroppedTo(String item, int to) {
				selectorMapPreview.refreshMidgrounds();
			}
			
			@Override
			public void onItemDroppedFrom(String item, int from) {
				selectorMapPreview.refreshMidgrounds();
			}
		});
		
		listUsed.setAdapter(new DragNDropAdapter(this, used));
		listUnused.setAdapter(new DragNDropAdapter(this, mgs));
	}
}

