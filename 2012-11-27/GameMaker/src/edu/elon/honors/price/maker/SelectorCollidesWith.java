package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.MapClass;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class SelectorCollidesWith extends LinearLayout {

	private CheckBox[] checkBoxes;
	private MapClass mapClass;
	
	public void setMapClass(MapClass mapClass) {
		this.mapClass = mapClass;
		
		for (int i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i].setChecked(mapClass.collidesWith[i]);
		}
	}
	
	public SelectorCollidesWith(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public SelectorCollidesWith(Context context, MapClass mapClass) {
		super(context);
		setup();
		setMapClass(mapClass);
	}

	private void setup() {
		LayoutInflater.from(getContext())
		.inflate(R.layout.selector_collides_with, this);
		
		if (isInEditMode()) return;
		
		int[] ids = new int[] {
				R.id.checkBoxHero,
				R.id.checkBoxActors,
				R.id.checkBoxObjects,
				R.id.checkBoxGround
		};
		checkBoxes = new CheckBox[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			CheckBox checkBox = (CheckBox)findViewById(ids[i]);
			checkBoxes[i] = checkBox;
			final int fi = i;
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mapClass != null) {
						mapClass.collidesWith[fi] = isChecked;
					}
				}
			});
		}
	}
	
}
