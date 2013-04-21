package edu.elon.honors.price.data.tutorial;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;

public class Tutorial implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum EditorButton {
		MapEditorMoveMode,
		MapEditorDrawMode,
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
		TextureSelectorOk
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
		MapEditorPlaceBackground
	}
	
	
	public List<TutorialAction> tutorialActions =
			new LinkedList<Tutorial.TutorialAction>();
	
	private LinkedList<String> messages = new LinkedList<String>();
	
	private String nextMessage() {
		return messages.remove(0);
	}
	
	public Tutorial(String textfile, Context context) {
		try {
			InputStream is = context.getAssets().open("tutorials/" + textfile);
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
	
	public boolean hasNext() {
		return tutorialActions.size() > 0;
	}
	
	public TutorialAction peek() {
		return tutorialActions.get(0);
	}
	
	public TutorialAction next() {
		return tutorialActions.remove(0);
	}
	
	protected TutorialAction addAction() {
		TutorialAction action = new TutorialAction();
		tutorialActions.add(action);
		return action;
	}
	
	public class TutorialAction implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Condition condition;
		public String dialogMessage;
		public String dialogTitle;
		public LinkedList<EditorButton> highlights
		 	= new LinkedList<Tutorial.EditorButton>();
		public int dialogDelay;
		
		public TutorialAction setCondition(Condition condition) {
			this.condition = condition;
			return this;
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
