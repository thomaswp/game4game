package edu.elon.honors.price.maker;

import java.io.InputStream;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.action.EventContext;
import edu.elon.honors.price.maker.action.ActionParser;
import edu.elon.honors.price.maker.action.Element;

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

public class DatabaseEditAction extends DatabaseActivity {

	private int id;
	private int nextId = 100;
	private Element rootElement;
	private Parameters originalParameters;

	private LinearLayout linearLayoutHost;
	
	private EventContext eventContext;
	
	public EventContext getEventContext() {
		return eventContext;
	}

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

		ActionParser parser = new ActionParser(this);

		try {
			InputStream is = Data.loadAction(id, this);
			Xml.parse(is, Xml.Encoding.UTF_8, parser);
		} catch (Exception e) {
			Game.debug(e);
		}

		rootElement = parser.getLayout();

		linearLayoutHost.addView(rootElement.getView());
		
		if (savedInstanceState != null) {
			Parameters params =
				(Parameters)savedInstanceState.getSerializable("params");
			rootElement.setParameters(params);
			originalParameters = 
				(Parameters)getIntent().getExtras().getSerializable("params");
		} else if (getIntent().getExtras().containsKey("params")) {
			originalParameters = 
				(Parameters)getIntent().getExtras().getSerializable("params");
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("params", rootElement.getParameters());
	}

	@Override
	protected void putExtras(Intent intent) {
		super.putExtras(intent);
		Parameters params = rootElement.getParameters();
		Action action = new Action(id, params);
		action.description = rootElement.getDescription(game);
		intent.putExtra("action", action);
		Game.debug(params);
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
		populate();
		if (resultCode == RESULT_OK) {
			View v = findViewById(requestCode); 
			if (v != null && v instanceof IPopulatable) {
				((IPopulatable)v).onActivityResult(requestCode, data);
			}
		}
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
		setIds(linearLayoutHost);
	}

	private void setIds(View v) {
		if (v instanceof IPopulatable) {
			v.setId(nextId);
			nextId++;
		}
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				setIds(vg.getChildAt(i));
			}
		}
	}

	private void populate() {
		populate(linearLayoutHost);
	}

	private void populate(View v) {
		if (v instanceof IPopulatable) {
			((IPopulatable)v).populate(game);
		}
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				populate(vg.getChildAt(i));
			}
		}
	}
}
