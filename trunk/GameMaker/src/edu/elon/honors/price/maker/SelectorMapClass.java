package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.ScopedData;
import edu.elon.honors.price.maker.action.EventContext;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public abstract class SelectorMapClass<T extends ScopedData<?>> extends Spinner implements IPopulatable {

	protected final static int MAX_IMAGE_SIZE = 100;
	
	protected  PlatformGame game;
	private T object = getNewPointer();
	private EventContext eventContext;
	private int paramObjects;
	
	protected abstract T getNewPointer();
	protected abstract ParameterType getParameterType();
	protected abstract void addLabelsAndImages(List<String> labels, List<Bitmap> images);
	
	public SelectorMapClass(Context context) {
		super(context);
	}
	
	public SelectorMapClass(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public T getSelectedClass() {
		return object;
	}
	
	public void setSelectedClass(T mapClass) {
		this.object = mapClass;
		if (object.scope == DataScope.Global) {
			setSelection(object.id + paramObjects);
		} else {
			int index = 0; 
			Behavior behavior = getBehavior();
			if (behavior != null) {
				for (int i = 0; i < object.id; i++) {
					if (behavior.parameters.get(i).type == 
							ParameterType.ObjectClass) index++;
				}
			}
			setSelection(index);
		}
	}
	
	private Behavior getBehavior() {
		return eventContext == null ? null :
			eventContext.getBehavior();
	}
	
	public void setEventContext(EventContext eventContext) {
		this.eventContext = eventContext;
	}
	
	@Override
	public void populate(PlatformGame game) {
		Context context = getContext();
		
		//figure out how to maintain this, but
		//change as necessary if the game changes...
		
		this.game = game;
		
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		
		paramObjects = 0;
		if (eventContext != null && eventContext.hasBehavior()) {
			for (Parameter param : eventContext.getBehavior().getParamters(
					ParameterType.ObjectClass)) {
				labels.add(param.name);
				images.add(null);
				paramObjects++;
			}
		}
		
		addLabelsAndImages(labels, images);
		
		ImageAdapter imageAdapter = new ImageAdapter(
				context, 
				android.R.layout.simple_spinner_dropdown_item, 
				labels,
				images);
		setAdapter(imageAdapter);
		
		setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
//				if (onObjectClassChangedListener != null) {
//					onObjectClassChangedListener.onObjectClassChanged(position);
//				}
				if (position < paramObjects) {
					object.scope = DataScope.Param;
					Behavior behavior = getBehavior();
					if (behavior != null) {
						int objects = 0;
						for (int i = 0; i < behavior.parameters.size(); i++) {
							if (behavior.parameters.get(i).type == ParameterType.ObjectClass) {
								if (objects == position) {
									object.id = i; break;
								} else {
									objects++;
								}
							}
						}
					} else {
						object = getNewPointer();
					}
				} else {
					object.scope = DataScope.Global;
					object.id = position - paramObjects;
				}
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		setSelectedClass(object);
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}
}
