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
	
	public static final int ABOVE = 0;
	public static final int BELOW = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public int imageId = R.drawable.ghost;
	public float speed = 0.1f;
	public float jumpVelocity = 0.2f;
	public int edgeBehavior = BEHAVIOR_TURN;
	public int wallBehavior = BEHAVIOR_TURN;
	public int[] actorContactBehaviors = new int[] {BEHAVIOR_NONE, BEHAVIOR_NONE, BEHAVIOR_TURN, BEHAVIOR_JUMP};
	public int[] heroContactBehaviors = new int[] {BEHAVIOR_NONE, BEHAVIOR_START_STOP, BEHAVIOR_NONE, BEHAVIOR_NONE};
}
