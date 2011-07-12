package com.twp.platform;

/*
 * TODO: 
 * Asterisk
 * Delayed load on Edit Hero
 * AnimatedSprite Update
 */

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

public class Platformer extends Game {

	@Override
	protected Logic getNewLogic() {
		String map = null;
		if (getIntent() != null && getIntent().getExtras() != null)
			map = getIntent().getExtras().getString("map");
		else
			map = "final-Map_1";
		return new PlatformLogic(map, this);
	}

}