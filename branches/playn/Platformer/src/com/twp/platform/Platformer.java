package com.twp.platform;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

public class Platformer extends Game {

	@Override
	protected Logic getNewLogic() {
		String map = getIntent().getExtras().getString("map");
		PlatformLogic logic = new PlatformLogic(map, this);
		return logic;
	}

}