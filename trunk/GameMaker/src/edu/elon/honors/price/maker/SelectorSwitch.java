package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class SelectorSwitch extends Button {

	private int switchId;
	private PlatformGame game;
	
	public int getSwitchId() {
		return switchId;
	}
	
	public void setSwitchId(int switchId) {
		this.switchId = switchId;
		
		if (game != null) {
			setText(String.format("%03d: %s", switchId, game.switchNames[switchId]));
		}
	}
	
	public SelectorSwitch(Context context) {
		super(context);
	}

	public SelectorSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void populate(PlatformGame game) {
		this.game = game;
	}

}
