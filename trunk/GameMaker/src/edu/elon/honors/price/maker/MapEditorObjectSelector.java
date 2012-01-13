package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MapEditorObjectSelector extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		PlatformGame game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		int id = getIntent().getExtras().getInt("id");
		
		setContentView(new ObjectSelectorView(this, game, id));
	}
	
	public class ObjectSelectorView extends BasicCanvasView {

		private PlatformGame game;
		private int selectedId;
		
		public ObjectSelectorView(Context context, PlatformGame game, int id) {
			super(context);
			this.game = game;
			selectedId = id;
		}

		@Override
		protected void onDraw(Canvas c) {
			
		}
		
		@Override
		protected void update(long timeElapsed) {
			// TODO Auto-generated method stub
			
		}
	}
}
