package edu.elon.honors.price.maker;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import edu.elon.honors.price.data.PlatformGame;

public class SelectorMapObjectSize extends SelectorMapBase {

	@Override
	protected MapView getMapView(PlatformGame game, Bundle savedInstanceState) {
		return new ObjectView(this, game, savedInstanceState);
	}
	
	protected static class ObjectView extends SelectorMapView {
		
		float zoom = 1;
		
		public ObjectView(Context context, PlatformGame game,
				Bundle savedInstanceState) {
			super(context, game, savedInstanceState);
			
		} 
		
		@Override
		protected boolean showLeftButton() {
			return true;
		}
		
		@Override
		protected boolean doSelection() {
			return true;
		}
		
		@Override
		protected void updateSelection() {
			
		}
		
		@Override
		protected void drawContent(Canvas c) {
			super.drawContent(c);
			
			
		}
		
	}
}
