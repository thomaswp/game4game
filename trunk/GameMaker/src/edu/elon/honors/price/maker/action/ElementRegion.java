package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.graphics.Rect;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorRegion;
import edu.elon.honors.price.maker.TextUtils;
import edu.elon.honors.price.maker.action.EventContext.Scope;

public class ElementRegion extends Element {
	
	private SelectorRegion selectorRegion;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}
	
	public ElementRegion(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		Parameters ps = new Parameters();
		Rect rect = selectorRegion.getRect();
		ps.addParam(rect.left);
		ps.addParam(rect.top);
		ps.addParam(rect.right);
		ps.addParam(rect.bottom);
		params.addParam(ps);
	}
	
	@Override
	protected void readParameters(Iterator params) {
		Rect rect = new Rect();
		Parameters ps = params.getParameters();
		rect.left = ps.getInt();
		rect.top = ps.getInt();
		rect.right = ps.getInt();
		rect.bottom = ps.getInt();
		selectorRegion.setRect(rect);
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setFocusableInTouchMode(true);
		selectorRegion = new SelectorRegion(context);
		selectorRegion.setHasMap(
				eventContext.getScope() == Scope.MapEvent);
		layout.addView(selectorRegion);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		Rect rect = selectorRegion.getRect();
		sb.append("(");
		TextUtils.addColoredText(sb, rect.left, color);
		sb.append(", ");
		TextUtils.addColoredText(sb, rect.top, color);
		sb.append(", ");
		TextUtils.addColoredText(sb, rect.right, color);
		sb.append(", ");
		TextUtils.addColoredText(sb, rect.bottom, color);
		sb.append(")");
		return sb.toString();
	}
}
