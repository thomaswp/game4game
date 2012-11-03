package edu.elon.honors.price.maker;

import edu.elon.honors.price.maker.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Database extends DatabaseActivity {

	private Page[] pages;
	private int selectedPage = 0;
	
	private String LAST_PAGE = "lastPage";
	
	private Button next, prev;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pages = new Page[] {
				new PageEvents(this),
				new PageActors(this),
				new PageObjects(this),
				new PageMap(this),
				new PageBehaviors(this),
				//new PageTest(this)
			};
		
		
		setContentView(R.layout.database); 
		next = (Button)findViewById(R.id.buttonNext);
		prev = (Button)findViewById(R.id.buttonPrevious);
		
		setButtonEvents();
		
		selectedPage = -1;
		if (savedInstanceState != null && 
				savedInstanceState.containsKey("page")) {
			int page = savedInstanceState.getInt("page");
			selectPage(page);
		} else {
			selectPage(getIntPreference(LAST_PAGE, 0));
		}
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
		pages[selectedPage].onResume();
	}
	
	@Override
	public void onPause() {
		if (selectedPage >= 0) {
			pages[selectedPage].onPause();
		}
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("page", selectedPage);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (selectedPage >= 0) {
			if (resultCode == RESULT_OK) {
				pages[selectedPage].onActivityResult(requestCode, data);
			}
		}
	}

	@Override
	protected void onFinishing() {
		if (selectedPage >= 0) {
			pages[selectedPage].onPause();
		}
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
		if (page >= pages.length) {
			page = pages.length - 1;
		}
		if (page < 0) {
			page = 0;
		}
		if (selectedPage >= 0) {
			pages[selectedPage].onPause();
			pages[selectedPage].setVisibility(View.GONE);
		}
		selectedPage = page;
		putPreference(LAST_PAGE, selectedPage);

		if (!pages[page].isCreated()) {
			pages[page].onCreate((ViewGroup)findViewById(
					R.id.relativeLayoutHost));
		}
		
		pages[page].setVisibility(View.VISIBLE);
		pages[page].onResume();
		
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
