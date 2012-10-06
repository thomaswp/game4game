package edu.elon.honors.price.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Actor Class, defining characteristics
 * such as look, speed, jump height and behaviors.
 *
 */
public class ActorClass extends GameData implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public final static float MAX_SPEED = 5f;
	public final static float MAX_JUMP = 8f;
	
	public final static int BEHAVIOR_NONE = 0;
	public final static int BEHAVIOR_STOP = 1;
	public final static int BEHAVIOR_TURN = 2;
	public final static int BEHAVIOR_JUMP = 3;
	public final static int BEHAVIOR_JUMP_TURN = 4;
	public final static int BEHAVIOR_START_STOP = 5;
	public final static int BEHAVIOR_STUN = 6;
	public final static int BEHAVIOR_DIE = 7;
	
	public final static String[] BEHAVIORS = new String[] {
		"Nothing",
		"Stop",
		"Turn around",
		"Jump",
		"Jump and turn",
		"Toggle Start/Stop",
		"Become stunned",
		"Die"
	};
	
	public static final int ABOVE = 0;
	public static final int BELOW = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public final static String[] DIRECTIONS = new String[] {
		"Above this actor",
		"Below this actor",
		"Left of this actor",
		"Right of this actor",
	};

	public String name = "";
	public String imageName = "ghost.png";
	public float speed;
	public float jumpVelocity;
	public int edgeBehavior;
	public int wallBehavior;
	public int stunDuration;
	public int[] actorContactBehaviors = new int[4];
	public int[] heroContactBehaviors = new int[4];
	public boolean animated = true;
	public boolean fixedRotation = true;
	public float zoom = 1;
	public boolean collidesWithActors = true;
	public boolean collidesWithHero = true;
	public boolean collidesWithObjects = true;
	public List<BehaviorInstance> behaviors = 
		new LinkedList<BehaviorInstance>();
	
	@Override
	public ActorClass clone() {
		try {
			ActorClass a = (ActorClass)super.clone();
			a.actorContactBehaviors = actorContactBehaviors.clone();
			a.heroContactBehaviors = heroContactBehaviors.clone();
			return a;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
