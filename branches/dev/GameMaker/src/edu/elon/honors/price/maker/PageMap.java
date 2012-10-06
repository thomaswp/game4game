package edu.elon.honors.price.maker;

import java.util.ArrayList;

import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.ScrollContainer;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorMapBase.SelectorMapView;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class PageMap extends Page {

	public PageMap(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.page_map;
		//return R.layout.drawer;
	}

	@Override
	public String getName() {
		return "Map";
	}

	@Override
	public void onCreate() {
		Button buttonEditBackground = (Button)findViewById(R.id.buttonEditBackground);
		buttonEditBackground.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, DatabaseEditMapBackground.class);
				intent.putExtra("game", getGame());
				parent.startActivityForResult(intent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});
		
		Button buttonEditMapSize = (Button)findViewById(R.id.buttonEditMapSize);
		buttonEditMapSize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, DatabaseEditMapSize.class);
				intent.putExtra("game", getGame());
				parent.startActivityForResult(intent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});
		
		Button buttonEditTileset = (Button)findViewById(R.id.buttonEditTileset);
		buttonEditTileset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, DatabaseEditMapTileset.class);
				intent.putExtra("game", getGame());
				parent.startActivityForResult(intent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});
		
		Button buttonEditHorizon = (Button)findViewById(R.id.buttonEditHorizon);
		buttonEditHorizon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, DatabaseEditMapHorizon.class);
				intent.putExtra("game", getGame());
				parent.startActivityForResult(intent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});

		DragNDropGroup group = new DragNDropGroup();
		ArrayList<String> list1 = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{add("One"); add("Two"); add("Three");}
		},
		list2 = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{add("Four"); add("Five"); add("Six");}
		};
		final DragNDropListView lv1 = ((ScrollContainer)findViewById(R.id.scrollContainer2)).getListView();
		final DragNDropListView lv2 = ((ScrollContainer)findViewById(R.id.scrollContainer1)).getListView();
		lv1.setAdapter(new DragNDropAdapter(parent, list1));
		lv2.setAdapter(new DragNDropAdapter(parent, list2));
		group.addListView(lv1);
		group.addListView(lv2);
		
		SelectorMapView mv = new SelectorMapView(parent, getGame(), null) {
			@Override
			protected boolean showRightButton() {
				return false;
			}

			@Override
			protected int getBackgroundTransparency() {
				return 255;
			}
		};
		LinearLayout content = (LinearLayout)findViewById(R.id.content);
		content.addView(mv);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		setGame((PlatformGame)data.getExtras().getSerializable("game"));
		Game.debug(getGame().getSelectedMap().skyImageName);
	}

}
