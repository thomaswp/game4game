package edu.elon.honors.price.maker;

import android.content.Intent;

public class PageTest extends Page {

	private SelectorActorInstance sai, sai2;
	private SelectorRegion sr;
	private SelectorSwitch ss, ss2;
	private SelectorVariable sv1, sv2;
	private SelectorPoint sp;
	private SelectorObjectInstance so;
	private SelectorObjectClass soc;
	
	public PageTest(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.tab_test;
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public void onCreate() {
//		((SelectorActorClass)findViewById(R.id.selectorActorClass)).populate(getGame());
//		sai = (SelectorActorInstance)findViewById(R.id.selectorActorInstance1);
//		sai2 = (SelectorActorInstance)findViewById(R.id.selectorActorInstance2);
//		sr = (SelectorRegion)findViewById(R.id.selectorRegion1);
//		ss = (SelectorSwitch)findViewById(R.id.selectorSwitch1);
//		ss2 = (SelectorSwitch)findViewById(R.id.selectorSwitch2);
//		sv1 = (SelectorVariable)findViewById(R.id.selectorVariable1);
//		sv2 = (SelectorVariable)findViewById(R.id.selectorVariable2);
//		sp = (SelectorPoint)findViewById(R.id.selectorPoint1);
//		so = (SelectorObjectInstance)findViewById(R.id.selectorObjectInstance1);
//		soc = (SelectorObjectClass)findViewById(R.id.selectorObjectClass1);
//		
//		populate();
//		
//		ss.setSwitchId(20);
//		ss2.setSwitchId(10);
//		sv1.setVariableId(15);
//		sv2.setVariableId(25);
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		
//		populate();
//		
//		sai.onActivityResult(requestCode, data);
//		sai2.onActivityResult(requestCode, data);
//		sr.onActivityResult(requestCode, data);
//		ss.onActivityResult(requestCode, data);
//		ss2.onActivityResult(requestCode, data);
//		sv1.onActivityResult(requestCode, data);
//		sv2.onActivityResult(requestCode, data);
//		sp.onActivityResult(requestCode, data);
//		so.onActivityResult(requestCode, data);
//		soc.onActivityResult(requestCode, data);
	}

	private void populate() {
		sai.populate(getGame()); 
		sai2.populate(getGame());
		sr.populate(getGame());
		ss.populate(getGame());
		ss2.populate(getGame());
		sv1.populate(getGame());
		sv2.populate(getGame());
		sp.populate(getGame());
		so.populate(getGame());
		soc.populate(getGame());
	}
}
