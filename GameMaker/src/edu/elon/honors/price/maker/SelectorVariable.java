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

public class SelectorVariable extends Button {

	private int variableId;
	private PlatformGame game;
	
	public int getVariableId() {
		return variableId;
	}
	
	public void setVariableId(int variableId) {
		this.variableId = variableId;
		
		if (game != null) {
			setText(String.format("%03d: %s", variableId, game.variableNames[variableId]));
		}
	}
	
	public SelectorVariable(Context context) {
		super(context);
	}

	public SelectorVariable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void populate(PlatformGame game) {
		this.game = game;
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlatformGame iGame = SelectorVariable.this.game;
				if (iGame != null) {
					Intent intent = new Intent(getContext(), SelectorActivityVariable.class);
					intent.putExtra("game", iGame);
					intent.putExtra("id", variableId);
					((Activity)getContext()).startActivityForResult(intent, getId());
				}
			}
		});
		setVariableId(variableId);
	}

	public void onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			setVariableId(data.getExtras().getInt("id"));
		}
	}

}
