package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.physics.Vector;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;

@AutoAssign
public class DatabaseEditMapPhysics extends DatabaseActivity {
	
	private static int GRAVITY_GRADATIONS = 10;
	
	private SelectorVector selectorVectorGravity;
	private SeekBar seekBarGravity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.database_edit_map_physics);
		autoAssign();
		setDefaultButtonActions();
		
		selectorVectorGravity.populate(game);
		
		Vector v = game.getSelectedMap().gravity.copy();
		seekBarGravity.setMax(Map.MAX_GRAVITY * GRAVITY_GRADATIONS);
		seekBarGravity.setProgress(Math.round(v.magnitude() * GRAVITY_GRADATIONS));
		v.makeUnitVector();
		if (v.magnitude() == 0) v.set(0, 1);
		selectorVectorGravity.setVector(v.getX(), v.getY());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			selectorVectorGravity.onActivityResult(requestCode, data);
		}
	}
	
	@Override
	public void onFinishing() {
		float magnitude = (float)seekBarGravity.getProgress() / seekBarGravity.getMax();
		magnitude *= Map.MAX_GRAVITY;
		Vector v = new Vector(selectorVectorGravity.getVectorX(),
				selectorVectorGravity.getVectorY());
		v.multiply(magnitude);
		Debug.write(v);
		game.getSelectedMap().gravity = v;
	}
}
