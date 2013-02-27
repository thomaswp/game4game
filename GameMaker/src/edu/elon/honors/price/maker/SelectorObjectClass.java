package edu.elon.honors.price.maker;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Behavior.ParameterType;
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
	protected void addLabelsAndImages(List<String> labels, List<Bitmap> images) {
		for (int i = 0; i < game.objects.length; i++) {
			labels.add(game.objects[i].name);
			String name = game.objects[i].imageName;
			Bitmap bitmap = Data.loadObject(name); 
			float zoom = game.objects[i].zoom;
			int width = (int)(bitmap.getWidth() * zoom);
			if (width > MAX_IMAGE_SIZE) {
				zoom *= (float)MAX_IMAGE_SIZE / width;
			}
			int height = (int)(bitmap.getHeight() * zoom);
			if (height > MAX_IMAGE_SIZE) {
				zoom *= (float)MAX_IMAGE_SIZE / width;
			}
			width = (int)(bitmap.getWidth() * zoom);
			height = (int)(bitmap.getHeight() * zoom);
			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			images.add(bitmap);
		}
	}

}
