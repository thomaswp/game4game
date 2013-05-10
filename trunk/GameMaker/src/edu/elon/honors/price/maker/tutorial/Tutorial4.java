package edu.elon.honors.price.maker.tutorial;

import android.content.Context;
import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.maker.R;

public class Tutorial4 extends Tutorial {
	private static final long serialVersionUID = 1L;
	
	private static int VERSION = 1;
	private final int myVersion = VERSION; 
	
	@Override
	public String getName() {
		return "Map Properties";
	}

	@Override
	public Tutorial getResetCopy(Context context) {
		return new Tutorial4(context);
	}

	@Override
	public boolean isUpToDate() {
		return false; //myVersion == VERSION; TODO: CHANGE
	}

	public Tutorial4(Context context) {
		super("tutorial4.txt", "tutorial4.game", context);
		
		addAction()
		.setDialog("Map Properties");
		
		addAction()
		.setDialog("Maps")
		.setDialogImageId(R.drawable.maps)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase)
		.addHighlight(EditorButton.DatabaseMaps);
		
		addAction()
		.setDialog("Selecting Maps")
		.setCondition(EditorButton.DatabaseMaps)
		.addHighlight(EditorButton.DatabaseMapsSelectMap);
		
		addAction()
		.setDialog("Map Editor")
		.setCondition(EditorButton.DatabaseMapsSelectMap)
		.addHighlight(EditorButton.DatabaseOk);
		
		addAction()
		.setDialog("A New Map")
		.setCondition(EditorButton.DatabaseOk)
		.addHighlight(EditorButton.MapEditorLayerTerrain2)
		.addHighlight(EditorButton.MapEditorSelection);
		
		addAction()
		.setDialog("New Tileset")
		.setCondition(EditorAction.MapEditorStartTextureSelection)
		.addHighlight(EditorButton.TextureSelectorOk);
		
		addAction()
		.setCondition(EditorButton.TextureSelectorOk)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setDialog("Map Details")
		.setCondition(EditorAction.MapEditorFinishTest)
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuDatabase)
		.addHighlight(EditorButton.DatabaseMaps)
		.addHighlight(EditorButton.DatabaseMapsEdit);
		
		addAction()
		.setDialog("Map Details")
		.setCondition(EditorButton.DatabaseMapsEdit)
		.addHighlight(EditorButton.EditMapBackground);
		
		addAction()
		.setDialog("Backgrounds")
		.setCondition()
		.addHighlight(EditorButton.EditMapBackgroundOk);
		
		addAction()
		.setDialog("Midgrounds")
		.setCondition()
		.addHighlight(EditorButton.EditMapMidground);
		
		addAction()
		.setDialog("Midgrounds")
		.setCondition()
		.addHighlight(EditorButton.EditMapMidgroundOk);
		
		addAction()
		.setDialog("Tileset")
		.setCondition()
		.addHighlight(EditorButton.EditMapTileset);
		
		addAction()
		.setDialog("Tileset")
		.setCondition()
		.addHighlight(EditorButton.EditMapTilesetOk);
		
		addAction()
		.setDialog("Horizon")
		.setCondition()
		.addHighlight(EditorButton.EditMapHorizon);
		
		addAction()
		.setDialog("Horizon")
		.setCondition()
		.addHighlight(EditorButton.EditMapHorizonOk);
		
		addAction()
		.setDialog("Size")
		.setCondition()
		.addHighlight(EditorButton.EditMapSize);
		
		addAction()
		.setDialog("Size")
		.setCondition()
		.addHighlight(EditorButton.EditMapSizeOk);
		
		addAction()
		.setDialog("Physics")
		.setCondition()
		.addHighlight(EditorButton.EditMapPhysics);
		
		addAction()
		.setDialog("Gravity Vector")
		.setCondition()
		.addHighlight(EditorButton.EditMapPhysicsGravityVector);
		
		addAction()
		.setDialog("Gravity Vector")
		.setCondition()
		.addHighlight(EditorButton.SelectVectorOk);
		
		addAction()
		.setDialog("Gravity Magnitude")
		.setCondition()
		.addHighlight(EditorButton.EditMapPhysicsOk);
		
		addAction()
		.setDialog("Test")
		.setCondition()
		.addHighlight(EditorButton.EditMapOk)
		.addHighlight(EditorButton.DatabaseOk);
		
		addAction()
		.setDialog("Platforms")
		.setCondition()
		.addHighlight(EditorButton.MapEditorMenu)
		.addHighlight(EditorButton.MapEditorMenuPlay);
		
		addAction()
		.setDialog("The End")
		.setCondition(EditorAction.MapEditorFinishTest);
		
	}
}