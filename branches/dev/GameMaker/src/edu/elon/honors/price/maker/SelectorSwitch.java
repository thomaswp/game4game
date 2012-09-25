package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.maker.action.EventContext;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorSwitch extends Button implements IPopulatable{

	private Switch _switch = new Switch();
	private PlatformGame game;
	private EventContext eventContext;
	private String allowedScopes;
	
	public String getAllowedScopes() {
		return allowedScopes;
	}
	
	public void setAllowedScopes(String allowedScopes) {
		this.allowedScopes = allowedScopes;
	}
	
	public void setEventContext(EventContext eventContext) {
		this.eventContext = eventContext;
	}
	
	public int getSwitchId() {
		return _switch.id;
	}
	
	public DataScope getScope() {
		return _switch.scope;
	}
	
	public void setScope(DataScope scope, int switchId) {
		_switch.scope = scope;
		setSwitchId(switchId);
	}
	
	public void setSwitchId(int switchId) {
		_switch.id = switchId;
		setSwitch(_switch);
		
	}
	
	public Switch getSwitch() {
		return _switch;
	}
	
	private void setSwitch() {
		setSwitch(_switch);
	}
	
	public void setSwitch(Switch aSwitch) {
		_switch = aSwitch;
		Behavior behavior = eventContext == null ?
				null : eventContext.getBehavior();
		validateSwitch();
		setText(_switch.getName(game, behavior));
	}
	
	private void validateSwitch() {
		Behavior behavior = eventContext == null ?
				null : eventContext.getBehavior();
		if (_switch.scope != DataScope.Global) {
			if (behavior == null) _switch = new Switch();
		}
	}
		
	public SelectorSwitch(Context context) {
		super(context);
	}

	public SelectorSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlatformGame iGame = SelectorSwitch.this.game;
				if (iGame != null) {
					Intent intent = new Intent(getContext(), SelectorActivitySwitch.class);
					intent.putExtra("game", iGame);
					intent.putExtra("id", _switch.id);
					intent.putExtra("scope", _switch.scope.toInt());
					intent.putExtra("eventContext", eventContext);
					intent.putExtra("allowedScopes", allowedScopes);
					((Activity)getContext()).startActivityForResult(intent, getId());
				}
			}
		});
		
		setSwitch();
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			int scope = data.getExtras().getInt("scope");
			int id = data.getExtras().getInt("id");
			setScope(DataScope.fromInt(scope), id);
			return true;
		}
		return false;
	}

}
