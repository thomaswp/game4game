package com.twp.pong2;

import com.twp.graphics.GraphicsView;

import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {
	
	PongLogic logic;
	GraphicsView view;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        logic = new PongLogic();
        view = (GraphicsView) findViewById(R.id.graphics);
        view.setGame(logic);
    }
}