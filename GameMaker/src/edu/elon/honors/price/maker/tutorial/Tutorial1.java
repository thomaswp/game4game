package edu.elon.honors.price.maker.tutorial;

import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.data.tutorial.Tutorial.Condition;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButtonAction;
import edu.elon.honors.price.maker.R;
import android.content.Context;

public class Tutorial1 extends Tutorial {
	private static final long serialVersionUID = 1L;
	
	private static int VERSION = 2;
	private final int myVersion = VERSION;
	
	@Override
	public boolean isUpToDate() {
		return VERSION == myVersion;	
	}
	
	@Override
	public String getName() {
		return "Map Editor Basics";
	}

	@Override
	public Tutorial getResetCopy(Context context) {
		return new Tutorial1(context);
	}
	
	public Tutorial1(Context context) {
		super("tutorial1.txt", "tutorial1.game", context);
		addAction()
		.setDialog("Welcome!");
		
		addAction()
		.setDialog("Tutorials")
		.addHighlight(EditorButton.MapEditorHelpButton);
		
		addAction()
		.setDialog("Move Mode")
		.addHighlight(EditorButton.MapEditorMoveMode);
		
		addAction()
		.setDialog("Placement")
		.setCondition(new Condition(EditorButton.MapEditorMoveMode, 
				EditorButtonAction.ButtonUp))
		.addHighlight(EditorButton.MapEditorSelection);
		
		addAction()
		.setDialog("Select")
		.setCondition(new Condition(EditorAction.MapEditorStartTextureSelection))
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
		
		addAction()
		.setCondition(new Condition(EditorButton.MapEditorUndo))
		.addHighlight(EditorButton.MapEditorRedo);
		
		addAction()
		.setCondition(new Condition(EditorButton.MapEditorUndo))
		.addHighlight(EditorButton.MapEditorRedo);
		
		addAction()
		.setDialog("Platforms")
		.setCondition(new Condition(EditorButton.MapEditorRedo));
		
		addAction()
		.setDialog("Layers")
		.addHighlight(EditorButton.MapEditorLayer);
		
		addAction()
		.setDialog("Layers")
		.setCondition(new Condition(EditorButton.MapEditorLayer))
		.addHighlight(EditorButton.MapEditorLayer)
		.addHighlight(EditorButton.MapEditorLayerActors)
		.setDialogImageId(R.drawable.layeractor);
		
		addAction()
		.setDialog("Actor Layer")
		.setCondition(new Condition(EditorButton.MapEditorLayerActors));
		
		addAction()
		.setDialog("Test")
		.setCondition(new Condition(EditorAction.MapEditorPlaceActor))
		.addHighlight(EditorButton.MapEditorMenu);
		
		addAction()
		.setDialog("Actors")
		.setCondition(new Condition(EditorAction.MapEditorFinishTest))
		.addHighlight(EditorButton.MapEditorSelection);
		
		addAction()
		.setDialog("Actors")
		.setCondition(new Condition(EditorAction.MapEditorStartActorSelection));
		
		addAction()
		.setDialog("Place Actors")
		.setCondition(new Condition(EditorAction.MapEditorFinishSelection));
		
		addAction()
		.setCondition(new Condition(EditorAction.MapEditorPlaceActor));
		
		addAction()
		.setCondition(new Condition(EditorAction.MapEditorPlaceActor));
		
		addAction()
		.setDialog("Decorations")
		.setCondition(new Condition(EditorAction.MapEditorPlaceActor));
		
		addAction()
		.setDialog("Decorations")
		.addHighlight(EditorButton.MapEditorLayer)
		.addHighlight(EditorButton.MapEditorLayerTerrain3)
		.setDialogImageId(R.drawable.layer3);
		
		addAction()
		.setDialog("Foreground")
		.setCondition(new Condition(EditorButton.MapEditorLayerTerrain3))
		.addHighlight(EditorButton.MapEditorLayer)
		.addHighlight(EditorButton.MapEditorLayerTerrain1)
		.setDialogImageId(R.drawable.layer1);
		
		addAction()
		.setCondition(new Condition(EditorButton.MapEditorLayerTerrain1));
		
		addAction()
		.setDialog("Tools")
		.setCondition(new Condition(EditorAction.MapEditorPlaceTile))
		.addHighlight(EditorButton.MapEditorSelection)
		.addHighlight(EditorButton.MapEditorDrawModePaint)
		.setDialogImageId(R.drawable.paint);
		
		addAction()
		.setDialog("Paint")
		.setCondition(new Condition(EditorButton.MapEditorDrawModePaint))
		.addHighlight(EditorButton.MapEditorSelection)
		.addHighlight(EditorButton.MapEditorDrawModeSelect)
		.setDialogImageId(R.drawable.select);
		
		addAction()
		.setDialog("Selection")
		.setCondition(new Condition(EditorButton.MapEditorDrawModeSelect))
		.addHighlight(EditorButton.MapEditorLayerActors)
		.addHighlight(EditorButton.MapEditorLayer);
		
		addAction()
		.setDialog("Selection")
		.setCondition(new Condition(EditorButton.MapEditorLayerActors));
		
		addAction()
		.setDialog("The End");
	}

}
