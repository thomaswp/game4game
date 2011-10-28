package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;

public class ImageAdapter extends ArrayAdapter<String> {
	private ArrayList<Bitmap> images;
	
	public ImageAdapter(Context context, int textViewResourceId,
			ArrayList<String> labels, ArrayList<Bitmap> images) {
		super(context, textViewResourceId, labels);
		this.images = images;
	}

	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		View row=inflater.inflate(R.layout.image_adapter_row, parent, false);
		TextView label=(TextView)row.findViewById(R.id.weekofday);
		label.setText(getItem(position));
		label.setTextSize(20);
		label.setTextColor(Color.DKGRAY);
		ImageView icon=(ImageView)row.findViewById(R.id.icon);
		//Bitmap bmp = Data.loadActor(getItem(position), getContext());
		//bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		icon.setImageBitmap(images.get(position));
		return row;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = getDropDownView(position, convertView, parent);
		v.findViewById(R.id.checkedTextView1).setVisibility(View.GONE);
		return v;
	}
	
	public static abstract class ImageSelector {
		public abstract Bitmap getImage(String item);
	}
}
