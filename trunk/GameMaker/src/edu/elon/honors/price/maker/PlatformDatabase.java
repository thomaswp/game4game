package edu.elon.honors.price.maker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class PlatformDatabase extends PlatformDatabaseActivity {

	private PlatformDatabasePage[] pages;
	private int selectedPage = -1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pages = new PlatformDatabasePage[] { 
				new PlatformActorsPage(this, R.layout.platformactorselector) 
			};
		
		setContentView(R.layout.platformdatabase);
		selectPage(0);
	}

	@Override
	public void onResume() {
		super.onResume();
		pages[selectedPage].onResume();
	}
	
	@Override
	public void onPause() {
		pages[selectedPage].onPause();
		super.onPause();
	}
	
	private void selectPage(int page) {
		if (selectedPage > 0) {
			pages[selectedPage].onPause();
		}
		selectedPage = page;

		RelativeLayout host = (RelativeLayout)findViewById(R.id.relativeLayoutHost);
		host.removeAllViews();
		LayoutInflater inflator = getLayoutInflater();
		inflator.inflate(pages[page].viewId, host);
		
		pages[page].onCreate();
	}
}
