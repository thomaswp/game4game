package edu.elon.honors.price.maker;

import java.util.Arrays;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PageActors extends PageList<ActorClass> {

	@Override
	public String getName() {
		return "Actors";
	}
	
	@Override
	public String getItemCategory() {
		return "Actor";
	}

	private int getActorId(int index) {
		return index;
	}

	private ActorClass[] getActors() {
		return getGame().actors;
	}

	public PageActors(Database parent) {
		super(parent);
	}

	@Override
	protected void editItem(int index) {
		Intent intent = new Intent(parent, DatabaseEditActorClass.class);
		intent.putExtra("id", getActorId(index));
		intent.putExtra("game", getGame());
		parent.startActivityForResult(intent, REQUEST_EDIT_ITEM);
	}

	@Override
	protected void resetItem(int index) {
		getGame().actors[getActorId(index)] = new ActorClass();
	}

	@Override
	protected void addItem() {
		ActorClass[] actors = getActors();
		actors = getGame().actors = Arrays.copyOf(actors, actors.length + 1);
		actors[actors.length - 1] = new ActorClass();
	}

	@Override
	protected CheckableArrayAdapter<ActorClass> getAdapter() {
		return new ImageAdapter(parent, getActors());
	}

	@Override
	protected ActorClass getItem(int index) {
		return getActors()[getActorId(index)];
	}

	private static class ImageAdapter extends CheckableArrayAdapter<ActorClass> {

		public ImageAdapter(Context context, ActorClass[] actors) {
			super(context, R.layout.array_adapter_row_image, actors);
		}

		@Override
		protected void setRow(int position, ActorClass actor, View row) {
			TextView label=(TextView)row.findViewById(R.id.textViewTitle);
			label.setText(actor.name);
			ImageView icon=(ImageView)row.findViewById(R.id.imageViewIcon);
			Bitmap bmp = Data.loadActorIcon(actor.imageName, getContext());
			if (actor.zoom != 1) {
				bmp = Bitmap.createScaledBitmap(bmp, 
						(int)(bmp.getWidth() * actor.zoom), 
						(int)(bmp.getHeight() * actor.zoom), true);
			}
			icon.setImageBitmap(bmp);
		}		
	}
	
	

	//	private ListView actorsView;
	//	private EditText editSize;
	//
	//	private static final String SELECTED_ACTOR = "selectedActor";
	//	
	//	protected ImageAdapter getAdapter() {
	//		return ((ImageAdapter)actorsView.getAdapter());
	//	}
	//	
	//	@Override
	//	public int getViewId() {
	//		return R.layout.page_actors;
	//	}
	//
	//	@Override
	//	public String getName() {
	//		return "Actors";
	//	}
	//	
	//	public PageActors(Database parent) {
	//		super(parent);
	//	}
	//	
	//	@Override
	//	public void onCreate() {
	//		actorsView = (ListView)findViewById(R.id.listViewActors);
	//		editSize = (EditText)findViewById(R.id.editTextResize);
	//
	//		createButtonEvents();
	//	}
	//	
	//	@Override
	//	public void onResume() {
	//		createActorsView();
	//		int index = getIntPreference(SELECTED_ACTOR, 0);
	//		int count = getAdapter().getCount();
	//		if (index >= count) index = count - 1;
	//		actorsView.setItemChecked(index, true);
	//		actorsView.setSelectionFromTop(index, actorsView.getHeight() / 2);		
	//	}
	//	
	//	@Override
	//	public void onPause() {
	//		super.onPause();
	//		Game.debug("write %d", getSelectedIndex());
	//		putPreference(SELECTED_ACTOR, getSelectedIndex());
	//	}
	//	
	//	private void createButtonEvents() {
	//
	//		Button editButton = (Button)findViewById(R.id.buttonEditActor);
	//		editButton.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				if (getSelectedId() >= 0) {
	//					Intent intent = new Intent(parent, DatabaseEditActor.class);
	//					intent.putExtra("id", getSelectedId());
	//					intent.putExtra("game", getGame());
	//					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
	//				}
	//			}
	//		});
	//
	//		Button reset = (Button)parent.findViewById(R.id.buttonResetActor);
	//		reset.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				int id = getSelectedId();
	//				getGame().actors[id] = new ActorClass();
	//				getAdapter().replaceItem(id - 1, getGame().actors[id]);
	//			}
	//		});
	//
	//		Button resize = (Button)findViewById(R.id.buttonResizeActors);
	//		resize.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Button me = (Button)v;
	//				if (editSize.getVisibility() == View.VISIBLE) {
	//					editSize.setVisibility(View.GONE);
	//					me.setText(R.string.resize);
	//					int newSize = Integer.parseInt(editSize.getText().toString()) + 1;
	//					if (newSize != getGame().actors.length) {
	//						ActorClass[] newActors = new ActorClass[newSize];
	//						for (int i = 0; i < newActors.length; i++) {
	//							if (i < getGame().actors.length) {
	//								newActors[i] = getGame().actors[i];
	//							} else {
	//								newActors[i] = new ActorClass();
	//							}
	//						}
	//						getGame().actors = newActors;
	//						createActorsView();
	//					}
	//				} else {
	//					editSize.setVisibility(View.VISIBLE);
	//					editSize.setText("" + (getGame().actors.length - 1));
	//					me.setText("Set new size");
	//				}
	//			}
	//		});
	//	}
	//
	//	private void createActorsView() {		
	//		ActorClass[] actors = new ActorClass[getGame().actors.length - 1];
	//		for (int i = 0; i < actors.length; i++) actors[i] = getGame().actors[i+1];
	//		actorsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	//		actorsView.setAdapter(new ImageAdapter(parent, R.layout.array_adapter_row_image, actors));
	//	}
	//	

	//	
	//	private int getSelectedId() {
	//		int id = getSelectedIndex();
	//		if (id >= 0) id++;
	//		return id;
	//	}
	//	
	//	private int getSelectedIndex() {
	//		return actorsView.getCheckedItemPosition();
	//	}
}
