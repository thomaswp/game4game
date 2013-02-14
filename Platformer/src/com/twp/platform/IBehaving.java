package com.twp.platform;

import java.util.List;

import edu.elon.honors.price.data.BehaviorInstance;

/**
 * Represents a thing (map/object/actor) that can have a behavior. It makes that
 * thing responsible for enumerating its BehaviorInstances (shared between all instances
 * of the type) and its behavior runtimes (specific to this object)  
 */
public interface IBehaving {
	public List<BehaviorInstance> getBehaviorInstances();
	public BehaviorRuntime[] getBehaviorRuntimes();
	public int getBehaviorCount();
}
