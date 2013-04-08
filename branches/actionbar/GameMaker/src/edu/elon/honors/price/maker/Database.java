package edu.elon.honors.price.maker;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.R;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Database extends DatabaseActivity implements ActionBar.TabListener {

	private final static String LAST_PAGE = "lastPage";
	
	private Page[] pages;
	
	private Page getSelectedPage() {
		return (Page)getSupportActionBar().getSelectedTab().getTag();
	}
	
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

		ActionBar actionBar = getSupportActionBar();
		
//		actionBar.setIcon(null);
//		actionBar.setTitle("");
				
		int page = getIntPreference(LAST_PAGE, 0);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
        for (int i = 0; i < pages.length; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(pages[i].getName());
            tab.setTabListener(this);
            tab.setTag(pages[i]);
            tab.setIcon(R.drawable.icon);
            getSupportActionBar().addTab(tab);
        }

		actionBar.getTabAt(page).select();
	}
	
	@Override
	protected boolean hasActionBar() {
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getSelectedPage().onResume();
	}
	
	@Override
	public void onPause() {
		getSelectedPage().onPause();
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getSelectedPage().onActivityResult(requestCode, data);
	}

	@Override
	protected void onFinishing() {
		getSelectedPage().onPause();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Page page = (Page)tab.getTag();
		if (!page.isCreated()) {
			page.onCreate((ViewGroup)findViewById(
					R.id.relativeLayoutHost));
		}
		page.setVisibility(View.VISIBLE);
		page.onResume();
		putPreference(LAST_PAGE, tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Page page = (Page)tab.getTag();
		page.onPause();
		page.setVisibility(View.GONE);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}
