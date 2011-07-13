package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.maker.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PageActors extends Page{
	private ListView actorsView;
	private EditText editSize;

	@Override
	public int getViewId() {
		return R.layout.platformactorselector;
	}

	@Override
	public String getName() {
		return "Actors";
	}
	
	public PageActors(Database parent) {
		super(parent);
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
					Intent intent = new Intent(parent, DatabaseEditActor.class);
					intent.putExtra("id", getSelectedId());
					intent.putExtra("game", getGame());
					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
				}
			}
		});

		Button reset = (Button)parent.findViewById(R.id.buttonResetActor);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = getSelectedId();
				getGame().actors[id] = new ActorClass();
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
						ActorClass[] newActors = new ActorClass[newSize];
						for (int i = 0; i < newActors.length; i++) {
							if (i < getGame().actors.length) {
								newActors[i] = getGame().actors[i];
							} else {
								newActors[i] = new ActorClass();
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
		ActorClass[] actors = new ActorClass[getGame().actors.length - 1];
		for (int i = 0; i < actors.length; i++) actors[i] = getGame().actors[i+1];
		actorsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//actorsView.setAdapter(new ArrayAdapter<PlatformActor>(parent, android.R.layout.simple_list_item_1, actors));
		actorsView.setAdapter(new ImageAdapter(parent, R.layout.imageadapterrow, actors));
	}
	
	private static void setRow(View row, ActorClass actor, Context context) {
		TextView label=(TextView)row.findViewById(R.id.weekofday);
		label.setText(actor.name);
		label.setTextSize(20);
		ImageView icon=(ImageView)row.findViewById(R.id.icon);
		Bitmap bmp = Data.loadActor(actor.imageName, context);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		icon.setImageBitmap(bmp);
	}
	
	private static class ImageAdapter extends ArrayAdapter<ActorClass> {
		
		public ImageAdapter(Context context, int textViewResourceId, ActorClass[] actors) {
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
