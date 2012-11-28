package edu.elon.honors.price.maker;

import android.content.Intent;
import edu.elon.honors.price.data.PlatformGame;

public interface IPopulatable {
	public void populate(PlatformGame game);
	public boolean onActivityResult(int requestCode, Intent data);
}
