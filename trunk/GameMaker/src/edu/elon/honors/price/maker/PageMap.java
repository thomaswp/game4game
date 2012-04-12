package edu.elon.honors.price.maker;

import java.util.ArrayList;

import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DropListener;
import com.ericharlow.DragNDrop.RemoveListener;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class PageMap extends Page {

	public PageMap(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		//return R.layout.page_map;
		return R.layout.drawer;
	}

	@Override
	public String getName() {
		return "Map";
	}

	@Override
	public void onCreate() {
//		Button buttonEditBackground = (Button)findViewById(R.id.buttonEditBackground);
//		buttonEditBackground.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditBackground.class);
//				intent.putExtra("game", getGame());
//				parent.startActivityForResult(intent, 
//						DatabaseActivity.REQUEST_RETURN_GAME);
//			}
//		});
//
//		DragNDropGroup group = new DragNDropGroup();
//		ArrayList<String> list1 = new ArrayList<String>() {
//			private static final long serialVersionUID = 1L;
//			{add("One"); add("Two"); add("Three");}
//		},
//		list2 = new ArrayList<String>() {
//			private static final long serialVersionUID = 1L;
//			{add("Four"); add("Five"); add("Six");}
//		};
//		DragNDropListView lv1 = (DragNDropListView)findViewById(R.id.dragNDropListView1);
//		DragNDropListView lv2 = (DragNDropListView)findViewById(R.id.dragNDropListView2);
//		lv1.setAdapter(new DragNDropAdapter(parent, list1));
//		lv2.setAdapter(new DragNDropAdapter(parent, list2));
//		group.addListView(lv1);
//		group.addListView(lv2);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		setGame((PlatformGame)data.getExtras().getSerializable("game"));
	}

}
