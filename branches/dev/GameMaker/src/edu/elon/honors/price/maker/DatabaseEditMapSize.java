package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Game;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

public class DatabaseEditMapSize extends DatabaseActivity {
	
	private TextView textViewSize;
	private SelectorMapPreview preview;
	
	private Map map;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		map = game.getSelectedMap();
		
		setContentView(R.layout.database_edit_map_size);
		
		preview = new SelectorMapPreview(this, game, null);
		LinearLayout layoutMap = (LinearLayout)findViewById(R.id.linearLayoutMap);
		layoutMap.addView(preview);
		
		textViewSize = (TextView)findViewById(R.id.textViewSize);

		final int[] dRows = new int[] {-1, 1, 0, 0, 0, 0, -1, 1};
		final int[] dCols = new int[] {0, 0, -1, 1, -1, 1, 0, 0};
		final boolean[] anchors = new boolean [] {
				true, true, true, true,
				false, false, false, false
		};
		
		View[] resizers = new View[] {
			findViewById(R.id.buttonTopMinus),
			findViewById(R.id.buttonTopPlus),
			findViewById(R.id.buttonLeftMinus),
			findViewById(R.id.buttonLeftPlus),
			findViewById(R.id.buttonRightMinus),
			findViewById(R.id.buttonRightPlus),
			findViewById(R.id.buttonBottomMinus),
			findViewById(R.id.buttonBottomPlus),
		};
		
		for (int i = 0; i < resizers.length; i++) {
			final int fi = i;
			((Button)resizers[i]).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					resize(dRows[fi], dCols[fi], anchors[fi]);
				}
			});
		}
		
		setDefaultButtonActions();
		
		setMapSizeText();
	}
	
	private void resize(int dx, int dy, boolean anchorTL) {
		Tileset tileset = game.tilesets[map.tilesetId];
		
		synchronized (game) {
			map.resize(dx, dy, anchorTL, anchorTL, 
					tileset.tileWidth, tileset.tileHeight);
		}
		
		setMapSizeText();
	}
	
	private void setMapSizeText() {
		textViewSize.setText(String.format("%d x %d", map.rows, map.columns));
	}
}
