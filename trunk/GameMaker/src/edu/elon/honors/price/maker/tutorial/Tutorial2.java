package edu.elon.honors.price.maker.tutorial;

import android.content.Context;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.data.tutorial.Tutorial;

public class Tutorial2 extends Tutorial {
	private static final long serialVersionUID = 1L;

	private static int VERSION = 2;
	private final int myVersion = VERSION; 
	
	@Override
	public String getName() {
		return "Objects and Physics";
	}

	@Override
	public Tutorial getResetCopy(Context context) {
		return new Tutorial2(context);
	}

	@Override
	public boolean isUpToDate() {
		return myVersion == VERSION;
	}
	
	public Tutorial2(Context context) {
		super("tutorial2.txt", "tutorial2.game", context);
		
		addAction()
		.setDialog("Objects");
		
		addAction()
		.setDialog("Object Layer")
		.setDialogImageId(R.drawable.layerobject)
		.addHighlight(EditorButton.MapEditorLayer)
		.addHighlight(EditorButton.MapEditorLayerObjects);
		
		addAction()
		.setDialog("Placing Objects")
		.setCondition(new Condition(EditorButton.MapEditorLayerObjects));
		
		addAction()
		.setCondition(new Condition(EditorAction.MapEditorPlaceObject));
		
		addAction()
		.setDialog("Test")
		.setCondition(new Condition(EditorAction.MapEditorPlaceObject))
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setDialog("Object Selection")
		.setCondition(new Condition(EditorAction.MapEditorFinishTest))
		.addHighlight(EditorButton.MapEditorSelection);
		
		addAction()
		.setDialog("Object Selection")
		.setCondition(new Condition(EditorAction.MapEditorStartObjectSelection));
		
		addAction()
		.setDialog("Placing Objects")
		.setCondition(new Condition(EditorAction.MapEditorFinishSelection))
		.addHighlight(EditorButton.MapEditorMenu);
		
		addAction()
		.setDialog("Selection Tool")
		.setDialogImageId(R.drawable.select)
		.setCondition(new Condition(EditorAction.MapEditorFinishTest))
		.addHighlight(EditorButton.MapEditorSelection)
		.addHighlight(EditorButton.MapEditorDrawModeSelect);
		
		addAction()
		.setDialog("The End")
		.setCondition(new Condition(EditorButton.MapEditorDrawModeSelect));
	}

}
