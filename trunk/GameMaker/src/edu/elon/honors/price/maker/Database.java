package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.maker.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

@AutoAssign
public class Database extends DatabaseActivity {

	private Page[] pages;
	private int selectedPage = 0;
	
	private String LAST_PAGE = "lastPage";
	
	//private Button buttonNext, buttonPrevious;
	
	private final static EditorButton[] pageButtons = new EditorButton[] {
			EditorButton.DatabaseActors,
			EditorButton.DatabaseObjects,
			EditorButton.DatabaseMaps,
			EditorButton.DatabaseEvents,
			EditorButton.DatabaseBehaviors
	};

	private ImageButton imageButtonEvents, imageButtonActors,
		imageButtonObjects, imageButtonMaps, imageButtonBehaviors;
	ImageButton[] tabButtons;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pages = new Page[] {
				new PageActors(this),
				new PageObjects(this),
				new PageMap(this),
				new PageEvents(this),
				new PageBehaviors(this),
				//new PageTest(this)
		};
		
		
		setContentView(R.layout.database);
		AutoAssignUtils.autoAssign(this);
		
		tabButtons = new ImageButton[] {
				imageButtonActors,
				imageButtonObjects,
				imageButtonMaps,
				imageButtonEvents,
				imageButtonBehaviors
		};
		
		for (int i = 0; i < tabButtons.length; i++) {
			final int fi = i;
			tabButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectPage(fi);
					TutorialUtils.fireCondition(pageButtons[fi], Database.this);
				}
			});
		}
		
		setDefaultButtonActions();
		
		selectedPage = -1;
		if (savedInstanceState != null && 
				savedInstanceState.containsKey("page")) {
			int page = savedInstanceState.getInt("page");
			selectPage(page);
		} else {
			selectPage(getIntPreference(LAST_PAGE, 0));
		}
		
		TutorialUtils.fireCondition(EditorAction.DatabaseSelected, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		for (int i = 0; i < tabButtons.length; i++) {
			TutorialUtils.addHighlightable(tabButtons[i], pageButtons[i], this);
		}
		for (Page page : pages) { 
			page.addEditorButtons();
		}
		TutorialUtils.addHighlightable(findViewById(R.id.buttonOk), 
				EditorButton.DatabaseOk, this);
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
			tabButtons[selectedPage].setSelected(false);
		}
		
		selectedPage = page;
		putPreference(LAST_PAGE, selectedPage);
		
		tabButtons[selectedPage].setSelected(true);

		if (!pages[page].isCreated()) {
			pages[page].onCreate((ViewGroup)findViewById(
					R.id.relativeLayoutHost));
			pages[page].addEditorButtons();
		}
		
		pages[page].setVisibility(View.VISIBLE);
		pages[page].onResume();
		
//		if (selectedPage > 0) {
//			buttonPrevious.setVisibility(View.VISIBLE);
//			buttonPrevious.setText(pages[selectedPage - 1].getName());
//		} else {
//			buttonPrevious.setVisibility(View.INVISIBLE);
//		}
//		
//		if (selectedPage < pages.length - 1) {
//			buttonNext.setVisibility(View.VISIBLE);
//			buttonNext.setText(pages[selectedPage + 1].getName());
//		} else {
//			buttonNext.setVisibility(View.INVISIBLE);
//		}
	}
}
