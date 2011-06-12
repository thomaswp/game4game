package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GameMaker extends Game {

	@Override
	protected Logic getNewLogic() {
		return new PlatformMaker();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save");
		menu.add("Load");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	
}