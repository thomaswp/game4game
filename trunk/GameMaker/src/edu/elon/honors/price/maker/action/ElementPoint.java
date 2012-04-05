package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorPoint;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.SelectorVariable;
import edu.elon.honors.price.maker.TextUtils;

public class ElementPoint extends ElementMulti {

	public ElementPoint(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionElement("the exact point", 
					new ElementExactPoint(attributes, context)),
			new OptionElement("the variable point",
					new ElementVariablePoint(attributes, context))
		};
	}
}

