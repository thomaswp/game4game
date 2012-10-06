package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.PlatformGame;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MapEditorActorSelector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		PlatformGame game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		int id = getIntent().getExtras().getInt("id");
			
		setContentView(new ASView(this, id + 1, game));
	}
	
	private void doSelection(int id) {
		Intent intent = new Intent();
		intent.putExtra("id", id - 1);
		setResult(RESULT_OK, intent);
		finish();
	}

	private static class ASView extends ScrollingImageSelectorView {

		private PlatformGame game;
		
		public ASView(Context context, int id, PlatformGame game) {
			super(context, id);
			this.game = game;
		}

		@Override
		protected Bitmap[] getBitmaps() {
			Bitmap[] bitmaps = new Bitmap[game.actors.length + 1];
			bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.no);
			for (int i = 0; i < game.actors.length; i++) {
				ActorClass actor = i == 0 ? game.hero : game.actors[i];
				bitmaps[i+1] = Data.loadActorIcon(actor.imageName);
				bitmaps[i+1] = Bitmap.createScaledBitmap(bitmaps[i+1], bitmaps[i+1].getWidth() * 2, bitmaps[i+1].getHeight() * 2, false);
			}
			return bitmaps;
		}

		@Override
		protected String getDescription(int id) {
			if (id == 0) {
				return "Remove Actor";
			} else if (id == 1) {
				return game.hero.name;
			} else {
				return game.actors[id - 1].name;
			}
		}

		@Override
		protected void onSelection(int id) {
			((MapEditorActorSelector)getContext()).doSelection(id);
		}
	}
}