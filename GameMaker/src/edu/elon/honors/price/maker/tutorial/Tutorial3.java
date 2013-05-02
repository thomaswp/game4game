package edu.elon.honors.price.maker.tutorial;

import android.content.Context;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.data.tutorial.Tutorial.Condition;

public class Tutorial3 extends Tutorial {
	private static final long serialVersionUID = 1L;

	private static int VERSION = 1;
	private final int myVersion = VERSION; 
	
	@Override
	public String getName() {
		return "Actor and Object Properties";
	}

	@Override
	public Tutorial getResetCopy(Context context) {
		return new Tutorial3(context);
	}

	@Override
	public boolean isUpToDate() {
		return myVersion == VERSION;
	}
	
	public Tutorial3(Context context) {
		super("tutorial3.txt", "tutorial3.game", context);
		
		addAction()
		.setDialog("The Database");
		
		addAction()
		.setDialog("Accessing the Database")
		.setDialogImageId(R.drawable.database)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase);
		
		addAction()
		.setDialog("The Database")
		.setCondition(new Condition(EditorAction.DatabaseSelected))
		.addHighlight(EditorButton.DatabaseHelp);
		
		addAction()
		.setDialog("Pages")
		.setDialogImageId(R.drawable.layerobject)
		.addHighlight(EditorButton.DatabaseObjects);
		
		addAction()
		.setDialog("Pages")
		.setDialogImageId(R.drawable.layeractor)
		.setCondition(new Condition(EditorButton.DatabaseObjects))
		.addHighlight(EditorButton.DatabaseActors);
		
		addAction()
		.setDialog("Editing an Actor")
		.setCondition(new Condition(EditorButton.DatabaseActors))
		.addHighlight(EditorButton.DatabaseActorsEdit);
		
		addAction()
		.setDialog("Editing an Actor")
		.setCondition(new Condition(EditorButton.DatabaseActorsEdit))
		.addHighlight(EditorButton.EditActorImage);
		
		addAction()
		.setDialog("Accepting Changes")
		.setCondition(new Condition(EditorButton.EditActorImage))
		.addHighlight(EditorButton.EditActorOk);
		
		addAction()
		.setDialog("Accepting All Changes")
		.setCondition(new Condition(EditorButton.EditActorOk))
		.addHighlight(EditorButton.DatabaseOk);
		
		addAction()
		.setDialog("Testing")
		.setCondition(new Condition(EditorButton.DatabaseOk))
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setDialog("More with Actors")
		.setCondition(EditorAction.MapEditorFinishTest)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase);
		
		addAction()
		.setDialog("Other Actors")
		.setCondition(EditorAction.DatabaseSelected)
		.addHighlight(EditorButton.DatabaseActorsEdit);

		addAction()
		.setDialog("Actor Properties")
		.setCondition(EditorButton.DatabaseActorsEdit);
		
		addAction()
		.setDialog("Actor Properties")
		.addHighlight(EditorButton.EditActorOk);
		
		addAction()
		.setDialog("Adding Actors")
		.setCondition(EditorButton.EditActorOk)
		.addHighlight(EditorButton.DatabaseOk);
		
		addAction()
		.setDialog("Testing")
		.setCondition(EditorButton.DatabaseOk)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setCondition(EditorAction.MapEditorFinishTest)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase)
		.addHighlight(EditorButton.DatabaseObjects);
		
		addAction()
		.setDialog("Editing Objects")
		.setCondition(EditorButton.DatabaseObjects)
		.addHighlight(EditorButton.DatabaseObjectsEdit);
		
		addAction()
		.setDialog("Object Properties")
		.setCondition(EditorButton.DatabaseObjectsEdit);
		
		addAction()
		.setDialog("Object Properties")
		.addHighlight(EditorButton.EditObjectOk)
		.addHighlight(EditorButton.DatabaseOk)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setCondition(EditorAction.MapEditorFinishTest)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase)
		.addHighlight(EditorButton.DatabaseObjects);
		
		addAction()
		.setDialog("Brick")
		.setCondition(EditorButton.DatabaseObjects)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase)
		.addHighlight(EditorButton.DatabaseObjectsEdit);
		
		addAction()
		.setDialog("Is Platform")
		.setCondition(EditorButton.DatabaseObjectsEdit)
		.addHighlight(EditorButton.EditObjectIsPlatform);
		
		addAction()
		.setDialog("Immobile")
		.setCondition(EditorButton.EditObjectIsPlatform)
		.addHighlight(EditorButton.EditObjectIsMovable);
		
		addAction()
		.setDialog("Placing Platforms")
		.setCondition(EditorButton.EditObjectIsMovable)
		.addHighlight(EditorButton.EditObjectOk)
		.addHighlight(EditorButton.DatabaseOk);
		
		addAction()
		.setDialog("Select the Brick")
		.setCondition(EditorAction.MapEditorReturnedDatabase)
		.addHighlight(EditorButton.MapEditorLayerObjects);
		
		addAction()
		.setDialog("Aligning Platforms")
		.setCondition(EditorAction.MapEditorPlaceObject);
		
		addAction()
		.setDialog("That's All")
		.setCondition(EditorAction.MapEditorPlaceObject);
		
	}

}
