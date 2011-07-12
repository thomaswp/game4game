package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.R.id;
import edu.elon.honors.price.maker.R.layout;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Database extends DatabaseActivity {

	private Page[] pages;
	private int selectedPage = -1;
	
	private Button next, prev;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pages = new Page[] { 
				new PageActors(this),
				new PageHero(this) 
			};
		
		
		setContentView(R.layout.platformdatabase);
		next = (Button)findViewById(R.id.buttonNext);
		prev = (Button)findViewById(R.id.buttonPrevious);
		
		setButtonEvents();
		selectPage(0);
	}

	@Override
	public void onResume() {
		super.onResume();
		pages[selectedPage].onResume();
	}
	
	@Override
	public void onPause() {
		if (selectedPage > 0) {
			pages[selectedPage].onPause();
		}
		super.onPause();
	}
	
	protected void finishOk() {
		if (selectedPage > 0) {
			pages[selectedPage].onPause();
		}
		super.finishOk();
	}
	
	private void setButtonEvents() {
		Button ok = (Button)findViewById(R.id.buttonOk);
		Button cancel = (Button)findViewById(R.id.buttonCancel);
				
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishOk();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectPage(selectedPage + 1);
				pages[selectedPage].onResume();
			}
		});
		
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectPage(selectedPage - 1);
				pages[selectedPage].onResume();
			}
		});
	}
	
	private void selectPage(int page) {
		if (selectedPage > 0) {
			pages[selectedPage].onPause();
		}
		selectedPage = page;

		RelativeLayout host = (RelativeLayout)findViewById(R.id.relativeLayoutHost);
		host.removeAllViews();
		LayoutInflater inflator = getLayoutInflater();
		inflator.inflate(pages[page].getViewId(), host);
		
		pages[page].onCreate();
		
		if (selectedPage > 0) {
			prev.setVisibility(View.VISIBLE);
			prev.setText(pages[selectedPage - 1].getName());
		} else {
			prev.setVisibility(View.INVISIBLE);
		}
		
		if (selectedPage < pages.length - 1) {
			next.setVisibility(View.VISIBLE);
			next.setText(pages[selectedPage + 1].getName());
		} else {
			next.setVisibility(View.INVISIBLE);
		}
	}
}
