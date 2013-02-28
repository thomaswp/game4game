package edu.elon.honors.price.maker;

import android.content.Context;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.types.ObjectClassPointer;

public class SelectorObjectClass extends SelectorMapClass<ObjectClassPointer> {

	public SelectorObjectClass(Context context) {
		super(context);
	}

	@Override
	protected ObjectClassPointer getNewPointer() {
		return new ObjectClassPointer();
	}

	@Override
	protected ParameterType getParameterType() {
		return ParameterType.ObjectClass;
	}

	@Override
	protected MapClass[] getMapClasses() {
		return game.objects;
	}

}
