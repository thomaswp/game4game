package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectClass;
import edu.elon.honors.price.maker.SelectorObjectInstance;
import edu.elon.honors.price.maker.SelectorVector;
import edu.elon.honors.price.maker.TextUtils;

public class ElementVector extends ElementMulti {
	
	public ElementVector(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		final SelectorVector selectorVector = new SelectorVector(context);
		Option optionExact = new Option(selectorVector, "the exact Vector") {
			@Override
			public void writeDescription(StringBuilder sb, PlatformGame game) {
				sb.append("[");
				TextUtils.addColoredText(sb, selectorVector.getVectorX(), 
						DatabaseEditEvent.COLOR_VALUE);
				sb.append(", ");
				TextUtils.addColoredText(sb, selectorVector.getVectorX(), 
						DatabaseEditEvent.COLOR_VALUE);
				sb.append("]");
			}
			
			@Override
			public void readParams(Parameters params, int index) {
				final int x = params.getInt(index);
				final int y = params.getInt(index + 1);
				selectorVector.post(new Runnable() {
					@Override
					public void run() {
						selectorVector.setVector(x, y);
					}
				});
			}
			
			@Override
			public void addParams(Parameters params) {
				params.addParam(selectorVector.getVectorX());
				params.addParam(selectorVector.getVectorY());
			}
		};
		
		return new Option[] {
				optionExact,
				new OptionEmpty(context, "the triggering vector", 
						"the triggering vector"),
				new OptionElement("a joystick's vector",
						new ElementJoystick(attributes, context))
		};
	}

}

