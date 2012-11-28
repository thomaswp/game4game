package edu.elon.honors.price.maker;

import java.util.List;

import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DragNDropListView.DragNDropListener;
import com.ericharlow.DragNDrop.ScrollContainer;

import android.os.Bundle;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;

public class DatabaseEditMapMidground extends DatabaseActivity {

	DragNDropListView listUsed, listUnused;
	SelectorMapPreview selectorMapPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_edit_map_midground);

		setDefaultButtonActions();

		LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutMap);
		selectorMapPreview = new SelectorMapPreview(this, game, null);
		ll.addView(selectorMapPreview);
		
		listUsed = ((ScrollContainer)findViewById(R.id.scrollContainerUsed)).getListView();
		listUnused = ((ScrollContainer)findViewById(R.id.scrollContainerUnused)).getListView();

		final Map map = game.getSelectedMap();

		List<String> mgs = Data.getResources(Data.MIDGROUNDS_DIR, this);
		List<String> used = map.midGrounds;

		for (String mg : used) {
			mgs.remove(mg);
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

