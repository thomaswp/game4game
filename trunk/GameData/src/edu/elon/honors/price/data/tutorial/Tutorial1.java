package edu.elon.honors.price.data.tutorial;

import android.content.Context;

public class Tutorial1 extends Tutorial {
	private static final long serialVersionUID = 1L;

	public Tutorial1(Context context) {
		super("tutorial1.txt", context);
		addAction()
		.setDialog("Welcome!");
		
		addAction()
		.setDialog("Move Mode")
		.addHighlight(EditorButton.MapEditorMoveMode);
		
		addAction()
		.setDialog("Placement")
		.setCondition(new Condition(EditorButton.MapEditorMoveMode, 
				EditorButtonAction.ButtonUp))
		.addHighlight(EditorButton.MapEditorDrawMode);
		
		addAction()
		.setDialog("Select")
		.setCondition(new Condition(EditorButton.MapEditorDrawMode, 
				EditorButtonAction.ButtonUp))
		.addHighlight(EditorButton.TextureSelectorOk);
		
		addAction()
		.setDialog("Placement")
		.setCondition(new Condition(EditorButton.TextureSelectorOk,
				EditorButtonAction.ButtonUp));
		
		addAction()
		.setDialog("Placement")
		.setCondition(new Condition(EditorAction.MapEditorPlaceTile));
		
		addAction()
		.setCondition(new Condition(EditorAction.MapEditorPlaceTile));
		
		addAction()
		.setDialog("Undo/Redo")
		.setCondition(new Condition(EditorAction.MapEditorPlaceTile))
		.addHighlight(EditorButton.MapEditorUndo)
		.addHighlight(EditorButton.MapEditorRedo);
	}

}
