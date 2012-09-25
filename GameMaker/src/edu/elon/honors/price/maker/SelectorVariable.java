package edu.elon.honors.price.maker;

import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.maker.action.EventContext;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorVariable extends Button implements IPopulatable {

	private Variable variable = new Variable();
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
	
	public DataScope getScope() {
		return variable.scope;
	}
	
	public void setScope(DataScope scope, int variableId) {
		variable.scope = scope;
		setVariableId(variableId);
	}
	
	public int getVariableId() {
		return variable.id;
	}
	
	public void setVariableId(int variableId) {
		variable.id = variableId;
		setVariable();
	}
	
	public Variable getVariable() {
		return variable;
	}
	
	public void setVariable() {
		setVariable(variable);
	}
	
	public void setVariable(Variable variable) {
		this.variable = variable;
		Behavior behavior = eventContext == null ?
				null : eventContext.getBehavior();
		validateVariable();
		setText(variable.getName(game, behavior));
	}
	
	private void validateVariable() {
		Behavior behavior = eventContext == null ?
				null : eventContext.getBehavior();
		if (variable.scope != DataScope.Global) {
			if (behavior == null) variable = new Variable();
		}
	}
	
	public SelectorVariable(Context context) {
		super(context);
	}

	public SelectorVariable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlatformGame iGame = SelectorVariable.this.game;
				if (iGame != null) {
					Intent intent = new Intent(getContext(), SelectorActivityVariable.class);
					intent.putExtra("game", iGame);
					intent.putExtra("id", variable.id);
					intent.putExtra("scope", variable.scope.toInt());
					intent.putExtra("eventContext", eventContext);
					intent.putExtra("allowedScopes", allowedScopes);
					((Activity)getContext()).startActivityForResult(intent, getId());
				}
			}
		});
		setVariable();
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			int scope = data.getExtras().getInt("scope");
			int id = data.getExtras().getInt("id");
			DataScope dataScope = DataScope.fromInt(scope);
			setScope(dataScope, id);
			return true;
		}
		return false;
	}

}
