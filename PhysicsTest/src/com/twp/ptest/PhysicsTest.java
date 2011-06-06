package com.twp.ptest;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import android.app.Activity;
import android.os.Bundle;

public class PhysicsTest extends Game {


	@Override
	protected Logic getNewLogic() {
		return new TestLogic();
	}
}