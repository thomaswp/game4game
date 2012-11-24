package edu.elon.honors.price.maker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Screen {
	public static int dipToPx(float dip, Context context) {
		Resources res = context.getResources();
		return (int)TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 
				dip, res.getDisplayMetrics());
	}
}
