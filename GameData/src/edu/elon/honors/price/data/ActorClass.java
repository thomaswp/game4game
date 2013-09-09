package edu.elon.honors.price.data;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

/**
 * Represents an Actor Class, defining characteristics
 * such as look, speed, jump height and behaviors.
 *
 */
public class ActorClass extends MapClass {
	private static final long serialVersionUID = 1L;
	
	public final static float MAX_SPEED = 7f;
	public final static float MAX_JUMP = 14f;
	
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

	public float speed;
	public float jumpVelocity;
	public int edgeBehavior;
	public int wallBehavior;
	public int stunDuration;
	public int[] actorContactBehaviors = new int[4];
	public int[] heroContactBehaviors = new int[4];
	public boolean animated = true;
	public boolean doubleJump = false;

	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		super.addFields(fields);
		speed = fields.add(speed);
		jumpVelocity = fields.add(jumpVelocity);
		edgeBehavior = fields.add(edgeBehavior);
		wallBehavior = fields.add(wallBehavior);
		stunDuration = fields.add(stunDuration);
		animated = fields.add(animated);
		doubleJump = fields.add(doubleJump);
		fields.addArray(actorContactBehaviors);
		fields.addArray(heroContactBehaviors);
	}
	
	public ActorClass clone() {
		try {
			ActorClass a = new ActorClass();
			a.speed = speed;
			a.jumpVelocity = jumpVelocity;
			a.edgeBehavior = edgeBehavior;
			a.wallBehavior = wallBehavior;
			a.stunDuration = stunDuration;
			a.actorContactBehaviors = Copy.copyOf(actorContactBehaviors, actorContactBehaviors.length);
			a.heroContactBehaviors = Copy.copyOf(heroContactBehaviors, heroContactBehaviors.length);
			a.animated = animated;
			a.doubleJump = doubleJump;
			return a;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	protected String getDefaultImageName() {
		return "a2/zombie.png";
	}

}
