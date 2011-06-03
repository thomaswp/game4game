package com.test;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

public class Test extends Game{

	@Override
	protected Logic getNewLogic() {
		return new ThisLogic();
	}

}
