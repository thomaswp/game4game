package com.twp.platform;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

public class Platformer extends Game {

	@Override
	protected Logic getNewLogic() {
		String map = getIntent().getExtras().getString("map");
		return new PlatformLogic(map);
	}

}