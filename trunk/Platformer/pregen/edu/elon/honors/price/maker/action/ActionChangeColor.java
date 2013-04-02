package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.maker.action.*;
import edu.elon.honors.price.data.*;
import edu.elon.honors.price.data.types.*;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.Event.Parameters;
import com.twp.platform.*;
import edu.elon.honors.price.physics.*;
import edu.elon.honors.price.input.*;

@SuppressWarnings("unused")
public class ActionChangeColor extends ScriptableInstance {
	public static final String NAME = "Change Color";
	public static final int ID = 24;
	public static final String CATEGORY = "Actor|Object";
	
	public boolean colorTheActor;
	public ColorTheActorData colorTheActorData;
	public class ColorTheActorData extends ScriptableFragment {
		/** Type: <b>&lt;actorInstance&gt;</b> */
		public Parameters actorInstance;
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {
			return gameState.readActorInstance(actorInstance);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			actorInstance = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean colorTheObject;
	public ColorTheObjectData colorTheObjectData;
	public class ColorTheObjectData extends ScriptableFragment {
		/** Type: <b>&lt;objectInstance&gt;</b> */
		public Parameters objectInstance;
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {
			return gameState.readObjectInstance(objectInstance);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			objectInstance = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	/** Type: <b>&lt;color&gt;</b> */
	public int newColor;
	public boolean turnPermanently;
	public boolean turnFlashingFor;
	public TurnFlashingForData turnFlashingForData;
	public class TurnFlashingForData extends ScriptableFragment {
		/** Type: <b>&lt;number&gt;</b> */
		public Parameters number;
		public int readNumber(GameState gameState) throws ParameterException {
			return gameState.readNumber(number);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			number = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;number&gt;</b> number</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionChangeColor() {
		colorTheActorData = new ColorTheActorData();
		colorTheObjectData = new ColorTheObjectData();
		turnFlashingForData = new TurnFlashingForData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int color = iterator.getInt();
		colorTheActor = color == 0;
		if (colorTheActor) colorTheActorData.readParams(iterator);
		colorTheObject = color == 1;
		if (colorTheObject) colorTheObjectData.readParams(iterator);
		
		newColor = iterator.getInt();
		int turn = iterator.getInt();
		turnPermanently = turn == 0;
		turnFlashingFor = turn == 1;
		if (turnFlashingFor) turnFlashingForData.readParams(iterator);
		
	}
	/**
	 * 024 <b><i>Change Color</i></b> (Actor|Object)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> color</i>:</li><ul>
	 * <li>colorTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>colorTheObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;color&gt;</b> newColor</li>
	 * <li><b>&lt;radio&gt;</b> turn</i>:</li><ul>
	 * <li>turnPermanently:</li>
	 * <li>turnFlashingFor:</li>
	 * <ul>
	 * <li><b>&lt;number&gt;</b> number</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
