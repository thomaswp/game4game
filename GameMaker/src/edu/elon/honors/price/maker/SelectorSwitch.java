package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorSwitch extends Button implements IPopulatable{

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
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlatformGame iGame = SelectorSwitch.this.game;
				if (iGame != null) {
					Intent intent = new Intent(getContext(), SelectorActivitySwitch.class);
					intent.putExtra("game", iGame);
					intent.putExtra("id", switchId);
					((Activity)getContext()).startActivityForResult(intent, getId());
				}
			}
		});
		setSwitchId(switchId);
	}

	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			setSwitchId(data.getExtras().getInt("id"));
			return true;
		}
		return false;
	}

}
