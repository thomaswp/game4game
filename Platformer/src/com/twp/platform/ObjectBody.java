package com.twp.platform;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.twp.platform.PlatformBody.DisposeCallback;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Vector;

public class ObjectBody extends PlatformBody {

	private static final float SCALE = PlatformLogic.SCALE;

	private ObjectClass object;

	public ObjectClass getObject() {
		return object;
	}

	public ObjectBody(Viewport viewport, World world, ObjectClass object, int id, 
			float startX, float startY, DisposeCallback onDisposeCallback) {
		super(viewport, world, id, startX, startY, onDisposeCallback);

		this.object = object;
		//object.zoom = 1f;

		Bitmap bitmap = Data.loadObject(object.imageName);
		this.sprite = new Sprite(viewport, bitmap);
		this.sprite.setX(startX); this.sprite.setY(startY);
		this.sprite.centerOrigin();
		this.sprite.setZoom(object.zoom);

		BodyDef actorDef = new BodyDef();
		actorDef.position.set(spriteToVect(sprite, null));
		actorDef.type = BodyType.DynamicBody;
		actorDef.fixedRotation = object.fixedRotation;
		body = world.createBody(actorDef);

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
//			//Game.debug("(%f,%f)", xs[i], ys[i]);
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
				//Game.debug(points[pi]);
				pi--;
			}
		}
		PolygonShape poly = new PolygonShape();
		poly.set(points);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = poly;
		fixture.density = 1;
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

	public void updateSprite(Vector offset) {
		setSpritePosition(sprite, body, offset);
		if (!object.fixedRotation) {
			sprite.setRotation(body.getAngle() * 180 / (float)Math.PI);
		}
	}

	@Override
	protected boolean collidesWith(PlatformBody body) {
		return true;
	}
}
