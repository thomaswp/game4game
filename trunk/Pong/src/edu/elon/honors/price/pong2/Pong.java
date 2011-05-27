package edu.elon.honors.price.pong2;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

import android.view.Menu;
import android.view.MenuItem;

public class Pong extends Game {

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
        	//Get the Logic and start a new game
        	//This has been causing problems and I don't know why...
        	PongLogic logic = (PongLogic)view.getLogic();
        	debug("Getting logic");
        	synchronized (logic) {
        		debug("Got logic");
        		logic.newGame();
        	}
        	debug("Done with logic");
        	break;
        }

        return false;
    }
	
	@Override
	protected Logic getNewLogic() {
		return new PongLogic();
	}
}