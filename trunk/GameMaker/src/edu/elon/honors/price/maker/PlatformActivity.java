package edu.elon.honors.price.maker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

public class PlatformActivity extends Activity {
	
	protected PlatformGame game;
	
	public static final int REQUEST_RETURN_GAME = 10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RETURN_GAME) {
			if (resultCode == RESULT_OK) {
				game = (PlatformGame)data.getExtras().getSerializable("game");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
