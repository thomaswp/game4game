package com.twp.platform;

import java.util.List;

import edu.elon.honors.price.data.BehaviorInstance;

public interface IBehaving {
	public List<BehaviorInstance> getBehaviorInstances();
	public BehaviorRuntime[] getBehaviorRuntimes();
	public int getBehaviorCount();
}
