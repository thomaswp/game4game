package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Tileset;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class DatabaseEditMapTileset extends DatabaseActivity {

	ImageView imageViewPreivew;
	SelectorMapPreview selectorMapPreview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.database_edit_map_tileset);
		setDefaultButtonActions();
		
		LinearLayout layoutPreview = 
			(LinearLayout)findViewById(R.id.linearLayoutPreview);
		RadioGroup group = (RadioGroup)findViewById(R.id.radioGroupTilesets);
		imageViewPreivew = (ImageView)findViewById(R.id.imageViewPreview);
		selectorMapPreview = new SelectorMapPreview(this, game, null);
		//selectorMapPreview.setBackgroundResource(R.drawable.border_white);
		
		layoutPreview.addView(selectorMapPreview);
		
		Tileset[] tilesets = game.tilesets;
		for (int i = 0; i < tilesets.length; i++) {
			final int fi = i;
			Tileset t = tilesets[i];
			final RadioButton button = new RadioButton(this);
			button.setText(t.name);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					game.getSelectedMap().tilesetId = fi;
					updatePreview();
				}
			});
			
			if (i == game.getSelectedMap().tilesetId) {
				button.post(new Runnable() {
					@Override
					public void run() {
						button.setChecked(true);
					}
				});
			}
			
			group.addView(button);
		}
		
		updatePreview();
	}

	private void updatePreview() {
		Bitmap bmp = Data.loadTileset(game.
				tilesets[game.getSelectedMap().tilesetId].bitmapName);
		imageViewPreivew.setImageBitmap(bmp);
		
		synchronized (game) {
			selectorMapPreview.refreshTileset();
		}
	}
}
