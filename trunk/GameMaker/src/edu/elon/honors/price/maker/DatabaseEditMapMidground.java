package edu.elon.honors.price.maker;

import java.util.List;

import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropGroup;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DragNDropListView.DragNDropListener;
import com.ericharlow.DragNDrop.ScrollContainer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;

public class DatabaseEditMapMidground extends DatabaseActivity {

	DragNDropListView<String> listUsed, listUnused;
	SelectorMapPreview selectorMapPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_edit_map_midground);

		setDefaultButtonActions();

		LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutMap);
		selectorMapPreview = new SelectorMapPreview(this, game, null);
		ll.addView(selectorMapPreview);
		
		LinearLayout linearLayoutUsed = (LinearLayout)findViewById(R.id.linearLayoutUsed);
		LinearLayout linearLayoutUnused = (LinearLayout)findViewById(R.id.linearLayoutUnused);

		ScrollContainer<String> scrollUsed = new ScrollContainer<String>(this);
		linearLayoutUsed.addView(scrollUsed);
		ScrollContainer<String> scrollUnused = new ScrollContainer<String>(this);
		linearLayoutUnused.addView(scrollUnused);
		
		listUsed = scrollUsed.getListView();
		listUnused = scrollUnused.getListView();
		
		final Map map = game.getSelectedMap();

		List<String> mgs = Data.getResources(Data.MIDGROUNDS_DIR, this);
		List<String> used = map.midGrounds;

		for (String mg : used) {
			mgs.remove(mg);
		}


		DragNDropGroup<String> group = new DragNDropGroup<String>();
		group.addListView(listUsed);
		group.addListView(listUnused);
		
		listUsed.addOnDragNDropListener(new DragNDropListener<String>() {
			@Override
			public void onItemDroppedTo(String item, int to) {
				selectorMapPreview.refreshMidgrounds();
			}
			
			@Override
			public void onItemDroppedFrom(String item, int from) {
				selectorMapPreview.refreshMidgrounds();
			}
		});
		
		DragNDropAdapter<String> adapterUsed = new DragNDropAdapter<String>(this, used, R.layout.array_adapter_row_only_image) {
			@Override
			protected void setView(View view, int position, String item) {
				setAdapterView(view, position, item);
			}
		};
		
		DragNDropAdapter<String> adapterUnused = new DragNDropAdapter<String>(this, mgs, R.layout.array_adapter_row_only_image) {
			@Override
			protected void setView(View view, int position, String item) {
				setAdapterView(view, position, item);
			}
		};
		
		listUsed.setAdapter(adapterUsed);
		listUnused.setAdapter(adapterUnused);
	}
	
	private static void setAdapterView(View view, int position, String item) {
		ImageView iv = (ImageView)view.findViewById(R.id.imageView);
		iv.setImageBitmap(Data.loadMidground(item));
	}
}

