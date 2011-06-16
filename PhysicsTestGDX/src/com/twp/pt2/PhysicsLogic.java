package com.twp.pt2;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;

public class PhysicsLogic implements Logic {

	float SCALE = 10;
	
	World world;
	ArrayList<Body> balls = new ArrayList<Body>();
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	Body pBody;
	Sprite pSprite;
	Sprite bmpSprite;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		world = new World(new Vector2(0, -10), true);
		BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2((float) 0.0, (float) -10.0));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundShapeDef = new PolygonShape();
        groundShapeDef.setAsBox((float) 50.0, (float) 10.0);
        groundBody.createFixture(groundShapeDef, 1);
        
        bmpSprite = new Sprite(Viewport.DefaultViewport, Data.loadBitmap(R.drawable.rock));
        float[] xs = new float[8]; float[] ys = new float[8];
        int pts = bmpSprite.convexHull(xs, ys);
        bmpSprite.setOriginY(bmpSprite.getHeight());
        
        Vector2[] points = new Vector2[pts];
        int pi = points.length - 1;
        for (int i = 0; i < 8; i++) {
        	if (xs[i] >= 0) {
        		points[pi] = new Vector2(xs[i] / SCALE, ys[i] / SCALE);
        		pi--;
        	}
        }
        PolygonShape poly = new PolygonShape();
        poly.set(points);
        BodyDef polyDef = new BodyDef();
        polyDef.type = BodyType.DynamicBody;
        polyDef.position.set(new Vector2(10, 50));
        //polyDef.fixedRotation = true;
        pBody = world.createBody(polyDef);
        pBody.createFixture(poly, 1);
        pBody.applyAngularImpulse(100);
        
        pSprite = new Sprite(Viewport.DefaultViewport, 100, 0, 
        		bmpSprite.getBitmap().getWidth(), bmpSprite.getBitmap().getHeight());
        Path path = new Path();
        for (int i = 0; i < points.length; i++) {
        	if (i == 0)
        		path.moveTo(points[i].x * SCALE, points[i].y * SCALE);
        	else
        		path.lineTo(points[i].x * SCALE, points[i].y * SCALE);
        }
        path.close();
        Paint paint = new Paint();
        paint.setColor(Color.argb(150, 255, 255, 255));
        paint.setStyle(Style.FILL);
        pSprite.getBitmapCanvas().drawPath(path, paint);
        pSprite.setOriginY(pSprite.getWidth());
	}
	
	private void createBall() {
		BodyDef balldef = new BodyDef();
        balldef.type = BodyType.StaticBody;
        float x = Input.getLastTouchX() / 10;
        float y = Input.getLastTouchY() / 10;
        balldef.position.set(x, y);
        Body ball = world.createBody(balldef);
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(1.8f);
        FixtureDef ballFix = new FixtureDef();
        ballFix.shape = ballShape;
        ballFix.density = 3;
        ballFix.friction = 0.3f;
        ballFix.restitution = 0.3f;
        ball.createFixture(ballFix);
        
        balls.add(ball);
        Sprite s = new Sprite(Viewport.DefaultViewport, 0, 0, 36, 36);
        s.getBitmapCanvas().drawCircle(18, 18, 18, Sprite.paintFromColor(Color.BLUE));
        s.centerOrigin();
        sprites.add(s);
	}

	@Override
	public void update(long timeElapsed) {
		if (Input.isTapped()) {
			createBall();
		}
		world.step(timeElapsed / 1000.0f, 6, 2);
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).setX(balls.get(i).getPosition().x * SCALE);
			sprites.get(i).setY(balls.get(i).getPosition().y * SCALE);
		}
		pSprite.setX(pBody.getPosition().x * SCALE);
		pSprite.setY(pBody.getPosition().y * SCALE + pSprite.getWidth());
		pSprite.setRotation(pBody.getAngle() * 180 / 3.1415f);
		
		bmpSprite.setX(pSprite.getX());
		bmpSprite.setY(pSprite.getY());
		bmpSprite.setRotation(pSprite.getRotation());
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
