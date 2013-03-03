package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.types.ActorClassPointer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class SelectorActorClass extends SelectorMapClass<ActorClassPointer> {
	
	public SelectorActorClass(Context context) {
		super(context);
	}

	@Override
	protected ActorClassPointer getNewPointer() {
		return new ActorClassPointer();
	}

	@Override
	protected ParameterType getParameterType() {
		return ParameterType.ActorClass;
	}

	@Override
	protected MapClass[] getMapClasses() {
		return game.actors;
	}

	@Override
	protected Bitmap getBitmap(String imageName) {
		return Data.loadActorIcon(imageName);
	}

}
