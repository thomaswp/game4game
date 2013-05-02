package edu.elon.honors.price.maker;

import java.util.Arrays;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
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

	@Override
	public void addEditorButtons() {
		Debug.write("Button Edit: %s", buttonEdit);
		TutorialUtils.addHighlightable(buttonEdit, 
				EditorButton.DatabaseActorsEdit, parent);
	}
}
