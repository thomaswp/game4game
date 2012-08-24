package edu.elon.honors.price.maker;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class TestActivity extends DatabaseActivity {
	public SelectorSwitch ss;
	public SelectorVariable sv;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ss = new SelectorSwitch(this);
		ss.populate(game);
		sv = new SelectorVariable(this);
		sv.populate(game);
		LinearLayout layout = new LinearLayout(this);
		layout.addView(ss);
		layout.addView(sv);
		setContentView(layout);
	}
	
	public void onActivityResult(int requestCode, 
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			ss.onActivityResult(requestCode, data);
			sv.onActivityResult(requestCode, data);
		}
	}
}
