package com.twp.asteroids;

import android.view.Menu;
import android.view.MenuItem;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.GraphicsView;

public class Asteroids extends Game {
  
	@Override
	protected Logic getNewLogic() {
		return new AsteroidsLogic();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
        	AsteroidsLogic logic = (AsteroidsLogic)view.getLogic();
        	view.setLogic(null);
        	synchronized (logic) {
        		Graphics.reset();
        		logic.setNewGame(true);
        		logic.initialize();
        	}
        	view.setLogic(logic);
        	break;
        }

        return false;
    }
}