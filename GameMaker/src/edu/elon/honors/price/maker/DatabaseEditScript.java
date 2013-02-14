package edu.elon.honors.price.maker;

import java.io.IOException;
import java.io.InputStream;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.EventContext;
import edu.elon.honors.price.maker.action.ScriptParser;
import edu.elon.honors.price.maker.action.Element;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public abstract class DatabaseEditScript extends DatabaseActivity implements IEventContextual {

	protected int id;
	protected Element rootElement;
	protected EventContext eventContext;
	
	private Parameters originalParameters;
	private LinearLayout linearLayoutHost;
	
	public EventContext getEventContext() {
		return eventContext;
	}

	protected abstract InputStream getXmlStream(int id)
		throws IOException;
	protected abstract void putExtras(Intent intent, Parameters params);
	/**
	 * Returns the parameters to edit, or null if there are none
	 */
	protected abstract Parameters getOriginalParameters();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_action);
		setDefaultButtonActions();

		id = getIntent().getExtras().getInt("id");
		eventContext = (EventContext)getIntent().getExtras()
		.getSerializable("eventContext");

		linearLayoutHost = (LinearLayout)findViewById(R.id.linearLayoutHost);
		ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
		scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				removeFocus();
				return false;
			}
		});

		ScriptParser parser = new ScriptParser(this);

		try {
			InputStream is = getXmlStream(id);
			Xml.parse(is, Xml.Encoding.UTF_8, parser);
		} catch (Exception e) {
			Debug.write(e);
		}

		rootElement = parser.getLayout();

		linearLayoutHost.addView(rootElement.getView());
		
		
		if (savedInstanceState != null) {
			Parameters params =
				(Parameters)savedInstanceState.getSerializable("params");
			rootElement.setParameters(params);
			originalParameters = 
				(Parameters)getIntent().getExtras().getSerializable("params");
		} else if ((originalParameters = getOriginalParameters()) != null) {
			rootElement.setParameters(originalParameters);
		} else {
			rootElement.getView().post(new Runnable() {
				@Override
				public void run() {
					rootElement.getView().post(new Runnable() {
						@Override
						public void run() {
							originalParameters = rootElement.getParameters().copy();
						}
					});
				}
			});
		}		

		setIds();
		populate();
	}
	
	@Override
	protected boolean onSaving() {
		String warning = rootElement.getWarning(); 
		if (warning != null) {
			new AlertDialog.Builder(this)
			.setTitle("Warning")
			.setMessage(warning)
			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finishOk();
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("params", rootElement.getParameters());
	}

	@Override
	protected boolean hasChanged() {
		if (originalParameters == null)
			return false;
		
		return super.hasChanged() || 
		!rootElement.getParameters().equals(originalParameters);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			populate();
			View v = findViewById(requestCode); 
			if (v != null && v instanceof IPopulatable) {
				((IPopulatable)v).onActivityResult(requestCode, data);
			}
		}
	}
	
	@Override
	protected void putExtras(Intent intent) {
		super.putExtras(intent);
		Parameters params = rootElement.getParameters();
		putExtras(intent, params);
	}

	private void removeFocus() {
		removeFocus(linearLayoutHost);
	}

	private void removeFocus(View v) {
		if (v instanceof EditText) {
			if (v.hasFocus()) {
				v.clearFocus();
			}
		}
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				removeFocus(vg.getChildAt(i));
			}
		}
	}

	private void setIds() {
		setPopulatableViewIds(linearLayoutHost, 100);
	}

	private void populate() {
		populateViews(linearLayoutHost);
	}
}
