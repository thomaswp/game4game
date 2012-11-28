package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformLogic;

import edu.elon.honors.price.maker.action.ActionDrawToScreen.ActionDrawAData;

import android.graphics.Paint;
import android.graphics.Paint.Style;

public class InterpreterDrawToScreen extends ActionInterpreter<ActionDrawToScreen> {

	Paint paint;
	
	@Override
	protected void interperate(ActionDrawToScreen action,
			PlatformGameState gameState) throws ParameterException {
		PlatformLogic logic = gameState.getLogic();
		if (action.actionClearTheScreen) {
			logic.clearDrawScreen();
		} else if (action.actionDrawA) {
			ActionDrawAData data = action.actionDrawAData;
			paint.reset();
			paint.setColor(data.color);
			if (data.styleFilledIn) {
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(3);
			} else if (data.styleHollow) {
				paint.setStyle(Style.FILL);
			}
			boolean world = data.useWorldCoordinates;
			
			if (data.shapeBox) {
				Point point = data.shapeBoxData.readCorner1(gameState);
				int x1 = point.x, y1 = point.y;
				point = data.shapeBoxData.readCorner2(gameState);
				int x2 = point.x, y2 = point.y;
				logic.drawBox(paint, x1, y1, x2, y2, world);
			} else if (data.shapeLine) {
				Point point = data.shapeLineData.readFrom(gameState);
				int x1 = point.x, y1 = point.y;
				point = data.shapeLineData.readTo(gameState);
				int x2 = point.x, y2 = point.y;
				logic.drawLine(paint, x1, y1, x2, y2, world);
			} else if (data.shapeLine) {
				Point center = data.shapeCircleData.readCenter(gameState);
				int radius = data.shapeCircleData.readRadius(gameState);
				logic.drawCircle(paint, center.x, center.y, radius, world);
			} else {
				throw new UnsupportedException();
			}
			
		} else {
			throw new UnsupportedException();
		}
	}

}
