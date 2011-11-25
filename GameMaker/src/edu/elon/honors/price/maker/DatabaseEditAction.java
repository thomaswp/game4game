package edu.elon.honors.price.maker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xml.sax.SAXException;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.action.ActionParser;
import edu.elon.honors.price.maker.action.Element;

import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DatabaseEditAction extends DatabaseActivity {
	
	private int id;
	private int nextId = 100;
	private Element rootElement;
	
	private LinearLayout linearLayoutHost;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_action);
		setDefaultButtonActions();
		
		id = getIntent().getExtras().getInt("id");
		
		linearLayoutHost = (LinearLayout)findViewById(R.id.linearLayoutHost);
		
		ActionParser parser = new ActionParser(this);
		
		try {
			InputStream is = Data.loadAction(id, this);
			Xml.parse(is, Xml.Encoding.UTF_8, parser);
		} catch (Exception e) {
			Game.debug(e);
		}
		
		rootElement = parser.getLayout();

		if (getIntent().getExtras().containsKey("params")) {
			Parameters params = 
				(Parameters)getIntent().getExtras().getSerializable("params");
			rootElement.setParameters(params);
		}
		
		linearLayoutHost.addView(rootElement.getView());
		
		setIds();
		populate();
	}
	
	@Override
	protected void putExtras(Intent intent) {
		super.putExtras(intent);
		Parameters params = rootElement.getParameters();
		Action action = new Action(id, params);
		action.description = rootElement.getDescription();
		intent.putExtra("action", action);
		Game.debug(params);
	}
	
	@Override
	protected boolean hasChanged() {
		if (getIntent().getExtras().containsKey("params")) {
			Parameters params = 
				(Parameters)getIntent().getExtras().getSerializable("params");
			return super.hasChanged() || 
				!rootElement.getParameters().equals(params);
		}
		return true;
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
