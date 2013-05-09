package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

@AutoAssign
public class DatabaseEditMap extends DatabaseActivity {
	private EditText editTextName;
	private RelativeLayout relativeLayoutHost;
	private Button buttonBackground, buttonMidground, buttonSize, 
		buttonTileset, buttonHorizon, buttonPhysics;
	private ScrollView scrollView;
	@AutoAssignIgnore
	private SelectorMapPreview selectorMapPreview;
	
	private Map map;
	private int formerIndex;
	
	public static void startForResult(DatabaseActivity activity, 
			int mapIndex, int requestCode) {
		Intent intent = activity.getNewGameIntent(DatabaseEditMap.class);
		intent.putExtra("index", mapIndex);
		activity.startActivityForResult(intent, requestCode);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.database_edit_map);
		autoAssign();
		setDefaultButtonActions();
		
		formerIndex = game.selectedMapId;
		int index = getIntent().getExtras().getInt("index");
		game.selectedMapId = index;
		
		map = game.getSelectedMap();
		
		editTextName.setText(map.name);
		selectorMapPreview = 
				new SelectorMapPreview(this, game, savedInstanceState);
		relativeLayoutHost.addView(selectorMapPreview);
		
		final Class<?>[] editors = new Class<?>[] {
			DatabaseEditMapBackground.class,
			DatabaseEditMapMidground.class,
			DatabaseEditMapSize.class,
			DatabaseEditMapTileset.class,
			DatabaseEditMapHorizon.class,
			DatabaseEditMapPhysics.class
		};
		final Button[] buttons = new Button[] {
				buttonBackground,
				buttonMidground,
				buttonSize,
				buttonTileset,
				buttonHorizon,
				buttonPhysics
		};
		for (int i = 0; i < buttons.length; i++) {
			final int fi = i;
			buttons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(DatabaseEditMap.this, editors[fi]);
					intent.putExtra("game", game);
					startActivityForResult(intent, REQUEST_RETURN_GAME);
				}
			});
		}
		
		scrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editTextName.isSelected()) {
					editTextName.setSelected(false);
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onFinishing() {
		map.name = editTextName.getText().toString();
	}
	
	@Override
	protected void putExtras(Intent intent) {
		//TODO: Make this cleaner... and stop it from detecting as an unsaved change
		selectorMapPreview.setVisibility(View.INVISIBLE);
		game.selectedMapId = formerIndex;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, 
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectorMapPreview.populate(game);
		}
	}
}
