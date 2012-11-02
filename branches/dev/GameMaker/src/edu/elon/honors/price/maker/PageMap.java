package edu.elon.honors.price.maker;

import java.util.ArrayList;

import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.ScrollContainer;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorMapBase.SelectorMapView;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageMap extends PageList<Map> {

	public PageMap(Database parent) {
		super(parent);
	}
	
	@Override
	public void onCreate(ViewGroup parentView) {
		super.onCreate(parentView);
		
		Button buttonSelect = addButton();
		buttonSelect.setText("Select Map");
		buttonSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getGame().selectedMapId = getSelectedIndex();
				listView.invalidateViews();
			}
		});
	}
	

	@Override
	protected void editItem(int index) {
		DatabaseEditMap.startForResult(parent, index, REQUEST_EDIT_ITEM);
	}

	@Override
	protected void resetItem(int index) {
		getGame().maps.set(index, new Map(getGame()));
	}

	@Override
	protected void addItem() {
		getGame().maps.add(new Map(getGame()));
	}

	@Override
	protected CheckableArrayAdapter<Map> getAdapter() {
		return new CheckableArrayAdapter<Map>(parent, 
				R.layout.array_adapter_row_string, getGame().maps) {

			@Override
			protected void setRow(int position, Map item, View view) {
				TextView tv = (TextView)view.findViewById(R.id.textViewTitle);
				String name = item.name;
				if (getGame().selectedMapId == position) {
					name = TextUtils.getColoredText(name, TextUtils.COLOR_VALUE);
					name = String.format("<b>%s</b>", name);
					tv.setText(Html.fromHtml(name));
				} else {
					tv.setText(name);
				}
			}
		};
	}

	@Override
	protected Map getItem(int index) {
		return getGame().maps.get(index);
	}

	@Override
	protected String getItemCategory() {
		return "Map";
	}

	@Override
	public String getName() {
		return "Maps";
	}

//	@Override
//	public int getLayoutId() {
//		return R.layout.page_map;
//		//return R.layout.drawer;
//	}
//
//
//	@Override
//	public void onCreate(ViewGroup parentView) {
//		super.onCreate(parentView);
//		Button buttonEditBackground = (Button)findViewById(R.id.buttonEditBackground);
//		buttonEditBackground.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditMapBackground.class);
//				intent.putExtra("game", getGame());
//				parent.startActivityForResult(intent, 
//						DatabaseActivity.REQUEST_RETURN_GAME);
//			}
//		});
//		
//		Button buttonEditMidground = (Button)findViewById(R.id.buttonEditMidground);
//		buttonEditMidground.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditMapMidground.class);
//				intent.putExtra("game", getGame());
//				parent.startActivityForResult(intent, 
//						DatabaseActivity.REQUEST_RETURN_GAME);
//			}
//		});
//		
//		Button buttonEditMapSize = (Button)findViewById(R.id.buttonEditMapSize);
//		buttonEditMapSize.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditMapSize.class);
//				intent.putExtra("game", getGame());
//				parent.startActivityForResult(intent, 
//						DatabaseActivity.REQUEST_RETURN_GAME);
//			}
//		});
//		
//		Button buttonEditTileset = (Button)findViewById(R.id.buttonEditTileset);
//		buttonEditTileset.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditMapTileset.class);
//				intent.putExtra("game", getGame());
//				parent.startActivityForResult(intent, 
//						DatabaseActivity.REQUEST_RETURN_GAME);
//			}
//		});
//		
//		Button buttonEditHorizon = (Button)findViewById(R.id.buttonEditHorizon);
//		buttonEditHorizon.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(parent, DatabaseEditMapHorizon.class);
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
//		final DragNDropListView lv1 = ((ScrollContainer)findViewById(R.id.scrollContainer2)).getListView();
//		final DragNDropListView lv2 = ((ScrollContainer)findViewById(R.id.scrollContainer1)).getListView();
//		lv1.setAdapter(new DragNDropAdapter(parent, list1));
//		lv2.setAdapter(new DragNDropAdapter(parent, list2));
//		group.addListView(lv1);
//		group.addListView(lv2);
//		
//		SelectorMapView mv = new SelectorMapView(parent, getGame(), null) {
//			@Override
//			protected boolean showRightButton() {
//				return false;
//			}
//
//			@Override
//			protected int getBackgroundTransparency() {
//				return 255;
//			}
//		};
//		LinearLayout content = (LinearLayout)findViewById(R.id.content);
//		content.addView(mv);
//	}
//
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, Intent data) {
//		super.onActivityResult(requestCode, data);
//		setGame((PlatformGame)data.getExtras().getSerializable("game"));
//	}
//
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		
//	}

}
