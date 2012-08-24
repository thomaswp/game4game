package edu.elon.honors.price.maker;

import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorListSize.SizeChangedListener;
import edu.elon.honors.price.maker.action.EventContext;
import edu.elon.honors.price.maker.action.EventContext.Scope;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class SelectorActivityIndex extends DatabaseTabActivity {

	public final static int SCOPE_GLOBAL = 0, SCOPE_LOCAL = 1, SCOPE_PARAM = 2;
	
	protected int id, scope, originalId, originalScope;
	protected RadioGroup radioGroupItems;
	protected EditText editTextItemName;
	protected SelectorListSize selectorListSize;
	protected TextView textViewId;
	protected EventContext eventContext;
	
	protected TextView textViewLocalName, textViewLocalDefault;
	protected RadioGroup radioGroupLocal;
	
	protected TextView textViewParamName;
	protected RadioGroup radioGroupParam;

	protected abstract String getType();
	protected abstract void setName(String name);
	protected abstract String makeRadioButtonText(int id);
	protected abstract void setupSelectors();
	protected abstract int getItemsSize();
	protected abstract void resizeItems(int newSize);
	
	protected abstract String getLocalName(int id);
	protected abstract String getLocalDefault(int id);
	protected abstract int getLocalSize();
	
	protected abstract String getParamName(int id);
	protected abstract int getParamSize();
 
	protected void setId(int id) {
		this.id = id;
	}
	
	@Override
	protected Tab[] getTabs() {
		if (extras.containsKey("eventContext")) {
			eventContext = (EventContext)extras.
			getSerializable("eventContext");
		}
		
		LinkedList<Tab> tabs = new LinkedList<DatabaseTabActivity.Tab>();
		tabs.add(new Tab(R.layout.selector_activity_index, 
				"Global " + getType()));
		if (eventContext != null && eventContext.hasBehavior()) {
			Tab tab = new Tab(R.layout.selector_activity_local_index,
					"Local " + getType());
			tab.enabled = getLocalSize() > 0;
			tabs.add(tab);
			
			tab = new Tab(R.layout.selector_activity_parameter_index,
					"Parameter " + getType());
			tab.enabled = getParamSize() > 0;
			tabs.add(tab);
		}
		return tabs.toArray(new Tab[tabs.size()]);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		id = extras.getInt("id");
		scope = extras.getInt("scope");
		originalId = id;
		originalScope = scope;

		if (tabHost != null) {
			tabHost.setCurrentTab(scope);
		}
			
		final RadioButton rb; final ScrollView scroll;
		if (scope == SCOPE_GLOBAL) {
			int index = id >= getItemsSize() ?
					getItemsSize() - 1 : id;
			rb = (RadioButton)radioGroupItems.getChildAt(index);
			scroll = (ScrollView)findViewById(R.id.scrollView);
		} else if (scope == SCOPE_LOCAL) {
			int index = id >= getLocalSize() ?
					getLocalSize() - 1 : id;
			rb = (RadioButton)radioGroupLocal.getChildAt(index);
			scroll = (ScrollView)findViewById(R.id.scrollViewLocal);
		} else {
			int index = id >= getParamSize() ?
					getParamSize() - 1 : id;
			rb = (RadioButton)radioGroupParam.getChildAt(index);
			scroll = (ScrollView)findViewById(R.id.scrollViewParam);
		}
		rb.setChecked(true);
		scroll.post(new Runnable() {
			@Override
			public void run() {
				scroll.scrollTo(0, rb.getBottom() - rb.getHeight() / 2 
						- scroll.getHeight() / 2);
			}
		});
	}
	
	@Override
	protected void onTabChanged(int newTab) {
		super.onTabChanged(newTab);
		scope = newTab;
	}
	
	@Override
	public void onFinishing() {
		RadioGroup radioGroup;
		if (scope == SCOPE_GLOBAL) {
			radioGroup = radioGroupItems;
		} else if (scope == SCOPE_LOCAL) {
			radioGroup = radioGroupLocal;
		} else {
			radioGroup = radioGroupParam;
		}
		if (radioGroup != null) {
			id = radioGroup.indexOfChild(radioGroup.findViewById(
					radioGroup.getCheckedRadioButtonId()));
		}
	}
	
	@Override
	protected void onTabLoaded(int index) {
		super.onTabLoaded(index);
		
		if (index == SCOPE_GLOBAL) {
			((TextView)findViewById(R.id.textViewIdPrompt)).setText(getType() + " Id:");
			((TextView)findViewById(R.id.textViewNamePrompt)).setText(getType() + " Name:");

			radioGroupItems = (RadioGroup)findViewById(R.id.radioGroupSwitches);
			RadioButton button = (RadioButton)radioGroupItems.getChildAt(0);
			if (button != null) button.setChecked(true);
			
			editTextItemName = (EditText)findViewById(R.id.editTextSwitchName);
			
			textViewId = (TextView)findViewById(R.id.textViewId);
			
			setupSelectors();
			
			selectorListSize = (SelectorListSize)findViewById(R.id.selectorListSize);
			selectorListSize.setMaxSize(999);
			selectorListSize.setSize(getItemsSize());
			selectorListSize.setOnSizeChangedListener(new SizeChangedListener() {
				@Override
				public void onSizeChanged(int newSize) {
					resize(newSize);
				}
			});
			
			addRadios();
			
			editTextItemName.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					setName(v.getText().toString());
					int id = radioGroupItems.getCheckedRadioButtonId();
					((RadioButton)radioGroupItems.findViewById(id))
					.setText(makeRadioButtonText(id));
					return false;
				}
			});
		}
		
		if (index == SCOPE_LOCAL) {
			radioGroupLocal = 
				(RadioGroup)findViewById(R.id.radioGroupLocal);
			textViewLocalName =
				(TextView)findViewById(R.id.textViewLocalName);
			textViewLocalDefault =
				(TextView)findViewById(R.id.textViewLocalDefault);
			
			RadioButton btn = (RadioButton)radioGroupLocal.getChildAt(0);
			if (btn != null) btn.setChecked(true);
			
			int size = getLocalSize();
			for (int i = 0; i < size; i++) {
				final int fi = i;
				RadioButton button = new RadioButton(this);
				button.setText(getLocalName(i));
				button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							textViewLocalName.setText(
									getLocalName(fi));
							textViewLocalDefault.setText(
									getLocalDefault(fi));
						}
					}
				});
				radioGroupLocal.addView(button);
				if (i == 0) button.setChecked(true);
			}
		}
		if (index == SCOPE_PARAM) {
			radioGroupParam = 
				(RadioGroup)findViewById(R.id.radioGroupParam);
			textViewParamName =
				(TextView)findViewById(R.id.textViewParamName);
					
			int size = getParamSize();
			for (int i = 0; i < size; i++) {
				final int fi = i;
				RadioButton button = new RadioButton(this);
				button.setText(getParamName(i));
				button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							textViewParamName.setText(
									getParamName(fi));
						}
					}
				});
				radioGroupParam.addView(button);
				if (i == 0) button.setChecked(true);
			}
		}
	}
	
	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("id", id);
		intent.putExtra("scope", scope);
	}
	
	@Override
	protected boolean hasChanged() {
		return super.hasChanged() || id != originalId ||
		scope != originalScope;
	}
	
	private void resize(int newSize) {
		resizeItems(newSize);
		addRadios();
	}
	
	private void addRadios() {
		radioGroupItems.removeAllViews();
		for (int i = 0; i < getItemsSize(); i++) {
			final RadioButton rb = new RadioButton(this);
			rb.setText(makeRadioButtonText(i));
			rb.setTextSize(18);
			radioGroupItems.addView(rb);
			
			final int fi = i;
			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						setId(fi);
					}
				}
			});
			if (i == 0) rb.setChecked(true);
		}
	}
}
