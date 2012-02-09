package edu.elon.honors.price.maker;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class PageUI extends Page {

	public PageUI(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.page_ui;
	}

	@Override
	public String getName() {
		return "UI";
	}

	@Override
	public void onCreate() {
		Button buttonEditUI = (Button)findViewById(R.id.buttonUI);
		buttonEditUI.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, DatabaseEditUI.class);
				intent.putExtra("game", getGame());
				parent.startActivityForResult(intent, 
						DatabaseActivity.REQUEST_RETURN_GAME);
			}
		});
	}

	@Override
	public void onResume() {
		
	}

}
