package com.twp.platform;


import java.util.List;

import android.graphics.Bitmap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class ObjectBody extends PlatformBody {

	private static final float SCALE = PlatformLogic.SCALE;

	private ObjectClass object;

	@Override
	protected MapClass getMapClass() {
		return object;
	}
	
	public ObjectClass getObject() {
		return object;
	}
	
	@Override
	public List<BehaviorInstance> getBehaviorInstances() {
		return object.behaviors;
	}

	public ObjectBody(Viewport viewport, PhysicsHandler physics, ObjectClass object, int id, 
			float startX, float startY) {
		super(viewport, physics, id, startX, startY);

		this.object = object;
		
		behaviorRuntimes = new BehaviorRuntime[object.behaviors.size()];
		for (int i = 0; i < behaviorRuntimes.length; i++) {
			behaviorRuntimes[i] = new BehaviorRuntime(
					object.behaviors.get(i), physics.getGame());
		}

		Bitmap bitmap = Data.loadObject(object.imageName);
		this.sprite = new Sprite(viewport, bitmap);
		this.sprite.setX(startX); this.sprite.setY(startY);
		this.sprite.centerOrigin();
		this.sprite.setZoom(object.zoom);

		BodyDef actorDef = new BodyDef();
		actorDef.position.set(spriteToVect(sprite, null));
		actorDef.type = BodyType.DynamicBody;
		actorDef.fixedRotation = object.fixedRotation;
		body = physics.getWorld().createBody(actorDef);

		float[] xs = new float[8]; float[] ys = new float[8];
		int pts = sprite.convexHull(xs, ys);
		sprite.centerOrigin();

//		Path path = new Path();
//		boolean first = true;
//		for (int i = 0; i < xs.length; i++) {
//			if (xs[i] >= 0) {
//				if (first)
//					path.moveTo(xs[i], ys[i]);
//				else
//					path.lineTo(xs[i], ys[i]);
//				first = false;
//			}
//			//Debug.write("(%f,%f)", xs[i], ys[i]);
//		}
//		path.close();
//		Paint paint = new Paint();
//		paint.setColor(Color.argb(150, 255, 255, 255));
//		paint.setStyle(Style.FILL);
//		sprite.setMutable(true);
//		sprite.getBitmapCanvas().drawPath(path, paint);

		Vector2[] points = new Vector2[pts];
		int pi = points.length - 1;
		for (int i = 0; i < 8; i++) {
			if (xs[i] >= 0) {
				points[pi] = new Vector2(
						(xs[i] - bitmap.getWidth() / 2) / SCALE * object.zoom, 
						(ys[i] - bitmap.getHeight() / 2) / SCALE * object.zoom
				);
				//Debug.write(points[pi]);
				pi--;
			}
		}
		PolygonShape poly = new PolygonShape();
		poly.set(points);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = poly;
		fixture.density = getDensity(object.density);
		fixture.friction = 1;
		fixture.restitution = 0.5f;
		body.createFixture(fixture);
		//body.setFixedRotation(true);


	}

	@Override
	public void update(long timeElapsed, Vector offset) {
		collidedBodies.clear();
		collidedWall = false;

		updateSprite(offset);
		lastVelocity.set(getVelocity());
	}

	@Override
	public void updateSprite(Vector offset) {
		setSpritePosition(sprite, body, offset);
		if (!object.fixedRotation) {
			sprite.setRotation(body.getAngle() * 180 / (float)Math.PI);
		}
	}
}
