package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.SurfaceHolder;
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
	
	public void doSelection(int id) {
		Intent intent = new Intent();
		intent.putExtra("id", id);
		setResult(RESULT_OK, intent);
		finish();
	}

	public class ObjectSelectorView extends ScrollingImageSelectorView {
		
		private PlatformGame game;

		public ObjectSelectorView(Context context, PlatformGame game, int id) {
			super(context, id);
			this.game = game;
		}

		@Override
		protected Bitmap[] getBitmaps() {
			Bitmap[] objects = new Bitmap[game.objects.length];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = Data.loadObject(game.objects[i].imageName);
				objects[i] = Bitmap.createScaledBitmap(objects[i], 
						(int)(objects[i].getWidth() * game.objects[i].zoom), 
						(int)(objects[i].getHeight() * game.objects[i].zoom), true);
			}
			return objects;
		}

		@Override
		protected String getDescription(int id) {
			return game.objects[id].name;
		}

		@Override
		protected void onSelection(int id) {
			((MapEditorObjectSelector)getContext()).doSelection(id);
		}
	}
}
