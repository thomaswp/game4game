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

public class DatabaseEditAction extends DatabaseEditScript implements IEventContextual {

	@Override
	protected InputStream getXmlStream(int id) throws IOException {
		return Data.loadAction(id, this);
	}

	@Override
	protected void putExtras(Intent intent, Parameters params) {
		Action action = new Action(id, params);
		action.description = rootElement.getDescription(game);
		intent.putExtra("action", action);
		Debug.write(params);
	}

	@Override
	protected Parameters getOriginalParameters() {
		return (Parameters)getIntent().getExtras().getSerializable("params");
	}
}
