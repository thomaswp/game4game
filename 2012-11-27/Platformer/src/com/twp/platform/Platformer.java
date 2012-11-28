package com.twp.platform;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;

public class Platformer extends Game {

	@Override
	protected Logic getNewLogic() {
		String map = null;
		if (getIntent() != null && getIntent().getExtras() != null)
			map = getIntent().getExtras().getString("map");
		else
			map = "final-Map_1";
		PlatformLogic logic = new PlatformLogic(map, this);
		return logic;
	}

}