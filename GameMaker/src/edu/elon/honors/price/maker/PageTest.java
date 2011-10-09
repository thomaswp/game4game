package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import android.content.Intent;

public class PageTest extends Page{

	private SelectorActorInstance sai, sai2;
	private SelectorRegion sr;
	private SelectorSwitch ss;
	
	public PageTest(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.testlayout;
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public void onCreate() {
		((SelectorActorClass)findViewById(R.id.selectorActorClass)).populate(getGame());
		sai = (SelectorActorInstance)findViewById(R.id.selectorActorInstance1); 
		sai.populate(getGame());
		sai2 = (SelectorActorInstance)findViewById(R.id.selectorActorInstance2); 
		sai2.populate(getGame());
		sr = (SelectorRegion)findViewById(R.id.selectorRegion1);
		sr.populate(getGame());
		ss = (SelectorSwitch)findViewById(R.id.selectorSwitch1);
		ss.populate(getGame());
		ss.setSwitchId(1);
	}

	@Override
	public void onResume() {
		
	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		sai.onActivityResult(requestCode, data);
		sai2.onActivityResult(requestCode, data);
		sr.onActivityResult(requestCode, data);
	}

}
