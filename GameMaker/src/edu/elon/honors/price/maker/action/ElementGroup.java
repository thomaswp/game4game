package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;

public class ElementGroup extends Element {

	public ElementGroup(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		Parameters childPs = new Parameters();
		super.addParameters(childPs);
		params.addParam(childPs);
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		Parameters childPs = params.getParameters(index);
		super.readParameters(childPs, 0);
		return index + 1;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < children.size(); i++) {
			if (i != 0) {
				sb.append(" ");
			}
			sb.append(children.get(i).getDescription(game));
		}
		
		return sb.toString();
	}
}
