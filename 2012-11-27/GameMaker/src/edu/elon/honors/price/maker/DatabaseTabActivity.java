package edu.elon.honors.price.maker;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public abstract class DatabaseTabActivity extends DatabaseActivity {
	protected TabHost tabHost;
	private boolean loaded[];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_tab_activity);
		createTabs();
		setDefaultButtonActions();
	}
	
	protected abstract Tab[] getTabs();
	
	protected void onTabChanged(int newTab) {
		if (!loaded[newTab]) {
			onTabLoaded(newTab);
		}
	}
	protected void onTabLoaded(int tab) {
		loaded[tab] = true;
	}
	
	private void createTabs() {
		LinearLayout main = 
			(LinearLayout)findViewById(R.id.linearLayoutHost);
		
		Tab[] tabs = getTabs();
		loaded = new boolean[tabs.length];
		
		if (tabs.length == 0) {
			return;
		} else if (tabs.length == 1) { 
			getLayoutInflater().inflate(tabs[0].layoutId, 
					main);
			onTabChanged(0);
			return;
		}
		
		
		tabHost = new TabHost(this);
		tabHost.setId(1);
				
		LinearLayout.LayoutParams lps = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		main.addView(tabHost, lps);
		
		LinearLayout content = new LinearLayout(this);
		content.setOrientation(LinearLayout.VERTICAL);
		tabHost.addView(content);

		TabWidget tabWidget = new TabWidget(this);
		tabWidget.setId(android.R.id.tabs);
		content.addView(tabWidget);

		FrameLayout frames = new FrameLayout(this);
		frames.setId(android.R.id.tabcontent);
		content.addView(frames);
		
		tabHost.setup();
		
		for (int i = 0; i < tabs.length; i++) {
			Tab tab = tabs[i];
			TabSpec ts = tabHost.newTabSpec(tab.title);
			CharSequence title = tab.title;
			if (!tab.enabled) {
				title = Html.fromHtml(TextUtils.getColoredText(
						"" + title, Color.DKGRAY));
			}
			ts.setIndicator(title);
			
			final View view = getLayoutInflater().inflate(
					tab.layoutId, null);
			
			ts.setContent(new TabHost.TabContentFactory() {
				@Override
				public View createTabContent(String tag) {
					return view;
				}
			});
			tabHost.addTab(ts);
			tabHost.getTabWidget().getChildAt(i).setEnabled(tab.enabled);
			
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int index = tabHost.getCurrentTab();
				DatabaseTabActivity.this.onTabChanged(index);
			}
		});
		
		onTabChanged(0);
	}
	
	protected static class Tab {
		public int layoutId;
		public String title;
		public boolean enabled = true;
		
		public Tab(int layoutId, String title) {
			this.layoutId = layoutId;
			this.title = title;
		}
	}
}
