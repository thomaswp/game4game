package edu.elon.honors.price.data.tutorial;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;

public abstract class Tutorial implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Change to reload all tutorials and maps.
	public static final int VERSION = 4;
	private static final String DIR = "tutorials/";
	
	public enum EditorButton {
		MapEditorMoveMode,
		MapEditorSelection,
		MapEditorDrawModePencil,
		MapEditorDrawModePaint,
		MapEditorDrawModeSelect,
		MapEditorLayer,
		MapEditorLayerTerrain1,
		MapEditorLayerTerrain2,
		MapEditorLayerTerrain3,
		MapEditorLayerActors,
		MapEditorLayerObjects,
		MapEditorUndo,
		MapEditorRedo,
		MapEditorMenu,
		MapEditorHelpButton,
		MapEditorMenuPlay,
		MapEditorMenuDatabase,
		TextureSelectorOk, 
		DatabaseOk,
		DatabaseCancel, 
		DatabaseActors, 
		DatabaseActorsEdit,
		DatabaseObjects,
		DatabaseObjectsEdit,
		DatabaseMaps,
		DatabaseEvents,
		DatabaseBehaviors, 
		DatabaseHelp, 
		EditActorImage, 
		EditActorOk,
		EditObjectIsPlatform, 
		EditObjectIsMovable, 
		EditObjectOk,
	}
	
	public enum EditorButtonAction {
		ButtonUp,
		ButtonDown
	}
	
	public enum EditorAction {
		MapEditorPlaceHero,
		MapEditorPlaceActor,
		MapEditorPlaceObject,
		MapEditorPlaceTile,
		MapEditorPlaceMidground,
		MapEditorPlaceForeground,
		MapEditorPlaceBackground, 
		MapEditorFinishTest, 
		MapEditorFinishSelection,
		MapEditorStartActorSelection,
		MapEditorStartObjectSelection,
		MapEditorStartTextureSelection,
		MapEditorReturnedDatabase,
		DatabaseSelected,
	}
	
	
	private List<TutorialAction> tutorialActions =
			new LinkedList<Tutorial.TutorialAction>();
	private int actionIndex;
	
	public String gameFile;
	
	private LinkedList<String> messages = new LinkedList<String>();
	
	public abstract String getName();
	public abstract Tutorial getResetCopy(Context context);
	/** Change to reload just the tutorial information **/
	public abstract boolean isUpToDate();
	
	private String nextMessage() {
		return messages.remove(0);
	}
	
	public Tutorial(String textfile, String gameFile, Context context) {
		this.gameFile = gameFile;
		try {
			InputStream is = context.getAssets().open(DIR + textfile);
			Scanner sc = new Scanner(is);
			while (sc.hasNext()) {
				String line = sc.nextLine();
				if (line.length() > 0) {
					messages.add(line);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PlatformGame loadGameFromAssets(Context context) {
		try {
			InputStream is = context.getAssets().open(DIR + gameFile);
			ObjectInputStream ois = new ObjectInputStream(is);
			PlatformGame game = (PlatformGame)ois.readObject();
			game.stripEditorData();
			game.tutorial = getResetCopy(context);
			is.close();
			return game;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean hasNext() {
		return actionIndex < tutorialActions.size();
	}
	
	public TutorialAction peek() {
		return tutorialActions.get(actionIndex);
	}
	
	public TutorialAction next() {
		return tutorialActions.get(actionIndex++);
	}
	
	public boolean hasPrevious() {
		return actionIndex > 0;
	}
	
	public TutorialAction previous() {
		return tutorialActions.get(--actionIndex);
	}
	
	public TutorialAction peekPrevious() {
		return tutorialActions.get(actionIndex - 1);
	}
	
	protected TutorialAction addAction() {
		TutorialAction action = new TutorialAction();
		tutorialActions.add(action);
		return action;
	}

	public int getActionIndex() {
		return actionIndex;
	}
	
	public class TutorialAction implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Condition condition;
		public String dialogMessage;
		public String dialogTitle;
		public LinkedList<EditorButton> highlights
		 	= new LinkedList<Tutorial.EditorButton>();
		public int dialogDelay;
		public int dialogImageId;
		
		public TutorialAction setDialogImageId(int dialogImageId) {
			this.dialogImageId = dialogImageId;
			return this;
		}
		
		public TutorialAction setCondition(Condition condition) {
			this.condition = condition;
			return this;
		}

		public TutorialAction setCondition(EditorAction action) {
			return setCondition(new Condition(action));
		}
		
		public TutorialAction setCondition(EditorButton button) {
			return setCondition(new Condition(button));
		}
		
		public TutorialAction setDialog(String title) {
			return setDialog(title, nextMessage());
		}
		
		public TutorialAction setDialog(String title, String message) {
			this.dialogTitle = title;
			this.dialogMessage = message;
			return this;
		}
		
		public TutorialAction addHighlight(EditorButton button) {
			this.highlights.add(button);
			return this;
		}
		
		public TutorialAction setDialogDelay(int delay) {
			this.dialogDelay = delay;
			return this;
		}

		public boolean hasDialog() {
			return dialogMessage != null && dialogTitle != null;
		}
	}
	
	public static class Condition implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private EditorButton trigger;
		private EditorButtonAction buttonAction;
		private EditorAction action;
		
		public Condition(EditorButton trigger) {
			this.trigger = trigger;
		}
		
		public Condition(EditorButton trigger, EditorButtonAction action) {
			this.trigger = trigger;
			this.buttonAction = action;
		}
		
		public Condition(EditorAction action) {
			this.action = action;
		}
		
		public boolean isTriggered(EditorButton trigger, EditorButtonAction action) {
			if (trigger == null) return false;
			return this.trigger == trigger && 
					(this.buttonAction == null || this.buttonAction == action);
		}
		
		public boolean isTriggered(EditorAction action) {
			return action != null && this.action == action;
		}
	}
}
