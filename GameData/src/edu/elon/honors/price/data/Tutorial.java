package edu.elon.honors.price.data;

import java.util.LinkedList;
import java.util.List;

public class Tutorial {

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
	
	public boolean hasNext() {
		return tutorialActions.size() > 0;
	}
	
	public TutorialAction peek() {
		return tutorialActions.get(0);
	}
	
	public TutorialAction next() {
		return tutorialActions.remove(0);
	}
	
	public Tutorial() {
		addAction().setDialog("Welcome!", "Welcome to your first tutorial");
	}
	
	private TutorialAction addAction() {
		TutorialAction action = new TutorialAction();
		tutorialActions.add(action);
		return action;
	}
	
	public static class TutorialAction {
		public Condition condidtion;
		public String dialogMessage;
		public String dialogTitle;
		public LinkedList<EditorButton> highlights
		 	= new LinkedList<Tutorial.EditorButton>();
		
		public TutorialAction setCondition(Condition condition) {
			this.condidtion = condition;
			return this;
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
	}
	
	public static class Condition {
		public EditorButton trigger;
		
		public Condition(EditorButton trigger) {
			this.trigger = trigger;
		}
	}
}
