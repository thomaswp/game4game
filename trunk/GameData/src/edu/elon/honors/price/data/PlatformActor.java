package edu.elon.honors.price.data;

import java.io.Serializable;

public class PlatformActor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final static int BEHAVIOR_NONE = 0;
	public final static int BEHAVIOR_STOP = 1;
	public final static int BEHAVIOR_TURN = 2;
	public final static int BEHAVIOR_JUMP = 3;
	public final static int BEHAVIOR_JUMP_TURN = 4;
	public final static int BEHAVIOR_START_STOP = 5;
	public final static int BEHAVIOR_STUN = 6;
	public final static int BEHAVIOR_DIE = 7;
	
	public static final int ABOVE = 0;
	public static final int BELOW = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	public String name = "";
	public int imageId;
	public float speed;
	public float jumpVelocity;
	public int edgeBehavior;
	public int wallBehavior;
	public int stunDuration;
	public int[] actorContactBehaviors = new int[4];
	public int[] heroContactBehaviors = new int[4];
}
