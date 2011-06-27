package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlatformActorsPage extends PlatformDatabasePage{
	private ListView actorsView;
	private EditText editSize;
	
	public PlatformActorsPage(PlatformDatabase parent, int viewId, String name) {
		super(parent, viewId, name);
	}
	
	@Override
	public void onCreate() {
		actorsView = (ListView)findViewById(R.id.listViewActors);
		editSize = (EditText)findViewById(R.id.editTextResize);

		createButtonEvents();
	}
	
	@Override
	public void onResume() {
		createRadioButtons();
	}
	
	private void createButtonEvents() {

		Button editButton = (Button)findViewById(R.id.buttonEditActor);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getSelectedId() >= 0) {
					Intent intent = new Intent(parent, PlatformEditActor.class);
					intent.putExtra("id", getSelectedId());
					intent.putExtra("game", parent.gameName);
					parent.startActivity(intent);
				}
			}
		});

		Button reset = (Button)parent.findViewById(R.id.buttonResetActor);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = getSelectedId();
				getGame().actors[id] = new PlatformActor();
				View row = actorsView.getChildAt(id - 1);
				setRow(row, getGame().actors[id], parent);
			}
		});

		Button resize = (Button)findViewById(R.id.buttonResizeActors);
		resize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button me = (Button)v;
				if (editSize.getVisibility() == View.VISIBLE) {
					editSize.setVisibility(View.GONE);
					me.setText(R.string.resize);
					int newSize = Integer.parseInt(editSize.getText().toString()) + 1;
					if (newSize != getGame().actors.length) {
						PlatformActor[] newActors = new PlatformActor[newSize];
						for (int i = 0; i < newActors.length; i++) {
							if (i < getGame().actors.length) {
								newActors[i] = getGame().actors[i];
							} else {
								newActors[i] = new PlatformActor();
							}
						}
						getGame().actors = newActors;
						createRadioButtons();
					}
				} else {
					editSize.setVisibility(View.VISIBLE);
					editSize.setText("" + (getGame().actors.length - 1));
					me.setText("Set new size");
				}
			}
		});
	}

	private void createRadioButtons() {		
		PlatformActor[] actors = new PlatformActor[getGame().actors.length - 1];
		for (int i = 0; i < actors.length; i++) actors[i] = getGame().actors[i+1];
		actorsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//actorsView.setAdapter(new ArrayAdapter<PlatformActor>(parent, android.R.layout.simple_list_item_1, actors));
		actorsView.setAdapter(new ImageAdapter(parent, R.layout.imageadapterrow, actors));
	}
	
	private static void setRow(View row, PlatformActor actor, Context context) {
		TextView label=(TextView)row.findViewById(R.id.weekofday);
		label.setText(actor.name);
		label.setTextSize(20);
		ImageView icon=(ImageView)row.findViewById(R.id.icon);
		Bitmap bmp = Data.loadActor(actor.imageName, context);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		icon.setImageBitmap(bmp);
	}
	
	private static class ImageAdapter extends ArrayAdapter<PlatformActor> {
		
		public ImageAdapter(Context context, int textViewResourceId, PlatformActor[] actors) {
			super(context, textViewResourceId, actors);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			View row=inflater.inflate(R.layout.imageadapterrow, parent, false);
			setRow(row, getItem(position), getContext());
			return row;
		}		
	}
	
	private int getSelectedId() {
		for (int i = 0; i < actorsView.getChildCount(); i++) {
			if (((Checkable)actorsView.getChildAt(i)).isChecked()) {
				return i + 1;
			}
		}
		return -1;
	}
}
