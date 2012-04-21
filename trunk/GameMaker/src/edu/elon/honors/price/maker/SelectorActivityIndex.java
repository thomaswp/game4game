package edu.elon.honors.price.maker;

import edu.elon.honors.price.maker.SelectorListSize.SizeChangedListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class SelectorActivityIndex extends DatabaseActivity {

	protected int id, originalId;
	protected Button buttonOk, buttonCancel;
	protected RadioGroup radioGroupItems;
	protected EditText editTextItemName;
	protected SelectorListSize selectorListSize;
	protected TextView textViewId;

	protected abstract String getType();
	protected abstract void setName(String name);
	protected abstract String makeRadioButtonText(int id);
	protected abstract void setupSelectors();
	protected abstract int getItemsSize();
	protected abstract void resizeItems(int newSize);
	
	protected void setId(int id) {
		this.id = id;
	}
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		id = getIntent().getExtras().getInt("id");
		originalId = id;
 
		setContentView(R.layout.selector_index);
		
		((TextView)findViewById(R.id.textViewIdPrompt)).setText(getType() + " Id:");
		((TextView)findViewById(R.id.textViewNamePrompt)).setText(getType() + " Name:");

		buttonOk = (Button)findViewById(R.id.buttonOk);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);

		radioGroupItems = (RadioGroup)findViewById(R.id.radioGroupSwitches);
		
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
				((RadioButton)radioGroupItems.getChildAt(id)).setText(makeRadioButtonText(id));
				return false;
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();		
			}
		});
		
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishOk();
			}
		});
	}
	
	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("id", id);
	}
	
	@Override
	protected boolean hasChanged() {
		return super.hasChanged() || id != originalId;
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
			
			if (i == id || (id >= game.switchNames.length && 
					i == game.switchNames.length - 1)) {
				id = i;
				radioGroupItems.check(rb.getId());
				final ScrollView sv = (ScrollView)findViewById(R.id.scrollView);
				sv.post(new Runnable() {
					@Override
					public void run() {
						sv.scrollTo(0, rb.getBottom() - rb.getHeight() / 2 
								- sv.getHeight() / 2);
					}
				});
			}
		}
	}
}
