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
public class ActionSetVariable extends ScriptableInstance {
	public static final String NAME = "Set Variable";
	public static final int ID = 1;
	public static final String CATEGORY = "Variables";
	
	public boolean setOneVariable;
	public SetOneVariableData setOneVariableData;
	public class SetOneVariableData extends ScriptableFragment {
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable variable;
		public int readVariable(GameState gameState) throws ParameterException {
			return gameState.readVariable(variable);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			variable = iterator.getVariable();
		}
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean setAllVariablesFrom;
	public SetAllVariablesFromData setAllVariablesFromData;
	public class SetAllVariablesFromData extends ScriptableFragment {
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable from;
		public int readFrom(GameState gameState) throws ParameterException {
			return gameState.readVariable(from);
		}
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable to;
		public int readTo(GameState gameState) throws ParameterException {
			return gameState.readVariable(to);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			from = iterator.getVariable();
			to = iterator.getVariable();
		}
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> from</li>
		 * <li><b>&lt;variable&gt;</b> to</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean operationSetItTo;
	public boolean operationAdd;
	public boolean operationSubtract;
	public boolean operationMultiply;
	public boolean operationDivideBy;
	public boolean operationModBy;
	public boolean withTheValue;
	public WithTheValueData withTheValueData;
	public class WithTheValueData extends ScriptableFragment {
		/** Type: <b>&lt;exactNumber&gt;</b> */
		public int exactNumber;
		
		@Override
		public void readParams(Iterator iterator) {
			exactNumber = iterator.getInt();
		}
		/**
		 * <ul>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withAVariable;
	public WithAVariableData withAVariableData;
	public class WithAVariableData extends ScriptableFragment {
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable variable;
		public int readVariable(GameState gameState) throws ParameterException {
			return gameState.readVariable(variable);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			variable = iterator.getVariable();
		}
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withARandomNumber;
	public WithARandomNumberData withARandomNumberData;
	public class WithARandomNumberData extends ScriptableFragment {
		public Group group;
		public class Group extends ScriptableFragment {
			/** Type: <b>&lt;exactNumber&gt;</b> */
			public int exactNumber;
			/** Type: <b>&lt;exactNumber&gt;</b> */
			public int exactNumber2;
			
			@Override
			public void readParams(Iterator iterator) {
				exactNumber = iterator.getInt();
				exactNumber2 = iterator.getInt();
			}
			/**
			 * <ul>
			 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
			 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		
		public WithARandomNumberData() {
			group = new Group();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			Iterator it2 = iterator.getParameters().iterator(true);
			group.readParams(it2);
			it2.dispose();
		}
		/**
		 * <ul>
		 * <li><b>&lt;group&gt;</b> group:</li>
		 * <ul>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withAnActorProperty;
	public WithAnActorPropertyData withAnActorPropertyData;
	public class WithAnActorPropertyData extends ScriptableFragment {
		/** Type: <b>&lt;actorInstance&gt;</b> */
		public Parameters actorInstance;
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {
			return gameState.readActorInstance(actorInstance);
		}
		public boolean coordinateX;
		public boolean coordinateY;
		
		@Override
		public void readParams(Iterator iterator) {
			actorInstance = iterator.getParameters();
			int coordinate = iterator.getInt();
			coordinateX = coordinate == 0;
			coordinateY = coordinate == 1;
			
		}
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * <li><b>&lt;radio&gt;</b> coordinate</i>:</li><ul>
		 * <li>coordinateX:</li>
		 * <li>coordinateY:</li>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionSetVariable() {
		setOneVariableData = new SetOneVariableData();
		setAllVariablesFromData = new SetAllVariablesFromData();
		withTheValueData = new WithTheValueData();
		withAVariableData = new WithAVariableData();
		withARandomNumberData = new WithARandomNumberData();
		withAnActorPropertyData = new WithAnActorPropertyData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int set = iterator.getInt();
		setOneVariable = set == 0;
		if (setOneVariable) setOneVariableData.readParams(iterator);
		setAllVariablesFrom = set == 1;
		if (setAllVariablesFrom) setAllVariablesFromData.readParams(iterator);
		
		int operation = iterator.getInt();
		operationSetItTo = operation == 0;
		operationAdd = operation == 1;
		operationSubtract = operation == 2;
		operationMultiply = operation == 3;
		operationDivideBy = operation == 4;
		operationModBy = operation == 5;
		
		int with = iterator.getInt();
		withTheValue = with == 0;
		if (withTheValue) withTheValueData.readParams(iterator);
		withAVariable = with == 1;
		if (withAVariable) withAVariableData.readParams(iterator);
		withARandomNumber = with == 2;
		if (withARandomNumber) withARandomNumberData.readParams(iterator);
		withAnActorProperty = with == 3;
		if (withAnActorProperty) withAnActorPropertyData.readParams(iterator);
		
	}
	/**
	 * 001 <b><i>Set Variable</i></b> (Variables)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> set</i>:</li><ul>
	 * <li>setOneVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * <li>setAllVariablesFrom:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> from</li>
	 * <li><b>&lt;variable&gt;</b> to</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> operation</i>:</li><ul>
	 * <li>operationSetItTo:</li>
	 * <li>operationAdd:</li>
	 * <li>operationSubtract:</li>
	 * <li>operationMultiply:</li>
	 * <li>operationDivideBy:</li>
	 * <li>operationModBy:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withTheValue:</li>
	 * <ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * </ul>
	 * <li>withAVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * <li>withARandomNumber:</li>
	 * <ul>
	 * <li><b>&lt;group&gt;</b> group:</li>
	 * <ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
	 * </ul>
	 * </ul>
	 * <li>withAnActorProperty:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;radio&gt;</b> coordinate</i>:</li><ul>
	 * <li>coordinateX:</li>
	 * <li>coordinateY:</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
