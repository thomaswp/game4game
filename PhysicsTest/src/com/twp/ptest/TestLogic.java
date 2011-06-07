package com.twp.ptest;

import android.app.Activity;
import android.graphics.Color;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class TestLogic implements Logic {

	public int targetFPS = 60;
    public float timeStep = (1.0f / targetFPS);
    public int iterations = 5;
 
    private Body[] bodies = new Body[100];
    private Sprite[] sprites = new Sprite[100];
    private int count = 0;
   
    private AABB worldAABB;
    private World world;
    private BodyDef groundBodyDef;
    private PolygonDef groundShapeDef;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initialize() {
		
		// Step 1: Create Physics World Boundaries
        worldAABB = new AABB();
        worldAABB.lowerBound.set(new Vec2((float) 0, (float) 0));
        worldAABB.upperBound.set(new Vec2((float) 100.0, (float) 100.0));
       
        // Step 2: Create Physics World with Gravity
        Vec2 gravity = new Vec2((float) 0.0, (float) -10.0);
        boolean doSleep = true;
        world = new World(worldAABB, gravity, doSleep);
       
        // Step 3: Create Ground Box
        groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vec2((float) 0.0, (float) 0.0));
        Body groundBody = world.createBody(groundBodyDef);
        groundShapeDef = new PolygonDef();
        groundShapeDef.setAsBox((float) 50.0, (float) 10.0);
        groundBody.createShape(groundShapeDef);
        
//        for (int i = 0; i < 5; i++) {
//        	addBall();
//        }
	}
	
	public void addBall() {
        // Create Dynamic Body
        BodyDef bodyDef = new BodyDef();
        float x = Input.getLastTouchX() / 10;
        float y = Input.getLastTouchY() / 10;
        bodyDef.position.set(x, y);
        bodies[count] = world.createBody(bodyDef);
       
        sprites[count] = new Sprite(Viewport.DefaultViewport, 0, 0, 36, 36);
        sprites[count].getBitmapCanvas().drawCircle(18, 18, 18, Sprite.paintFromColor(Color.BLUE));
        sprites[count].centerOrigin();
        
        // Create Shape with Properties
        CircleDef circle = new CircleDef();
        circle.radius = (float) 1.8;
        circle.density = (float) 1.0;
        circle.restitution = 0.3f;
       
        // Assign shape to Body
        bodies[count].createShape(circle);
        bodies[count].setMassFromShapes();
 
        // Increase Counter
        count += 1;        
    }

	@Override
	public void update(long timeElapsed) {
		if (Input.isTapped()) {
			addBall();
		}
		
		// Update Physics World
        world.step(timeElapsed / 1000.0f, 1);
        
        for (int i = 0; i < count; i++) {
        	Vec2 position = bodies[i].getPosition();
        	sprites[i].setX(position.x * 10);
        	sprites[i].setY(position.y * 10);
        }
	}

	@Override
	public void save(Activity parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(Activity parent) {
		// TODO Auto-generated method stub
		
	}

}
