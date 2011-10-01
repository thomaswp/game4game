package edu.elon.honors.price.maker;

public class PageTest extends Page{

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
		SelectorActorInstance sai = (SelectorActorInstance)findViewById(R.id.selectorActorInstance1); 
		sai.populate(getGame());
		sai.setSelectedInstance(2);
	}

	@Override
	public void onResume() {
		
	}

}
