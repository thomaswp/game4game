package com.twp.platform;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class PlatformBody {

	private static final int FRAME = 150;
	
	private Body body;
	private AnimatedSprite sprite;
	private PlatformActor actor;
	private Tilemap[] layers;
	private PlatformMap map;
	private RectF rect = new RectF();
	private Vector temp = new Vector();
	private boolean grounded;

	public Body getBody() {
		return body;
	}
	
	public Vector getVelocity() {
		return body.getVelocity();
	}
	
	public Vector getPosition() {
		return body.getPosition();
	}
	
	public AnimatedSprite getSprite() {
		return sprite;
	}

	public PlatformBody(Viewport viewport, Physics physics, PlatformActor actor, float startX, float startY, Tilemap[] layers, PlatformMap map) {
		this.actor = actor;
		Bitmap[] frames = Tilemap.createTiles(Data.loadBitmap(actor.imageId), 32, 48, 0); 
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.body = new Body(physics, sprite);
		this.layers = layers;
		this.map = map;
		
		this.body.getVelocity().setX(actor.speed);
	}

	public void update(long timeElapsed) {
		
		Vector velocity = body.getVelocity();
		rect.set(body.getSprite().getRect());
		
		int frame = sprite.getFrame();
		if (velocity.getX() > 0 && (frame / 4 != 2 || !sprite.isAnimated())) {
			sprite.Animate(FRAME, 8, 4);
		} else if (velocity.getX() < 0 && (frame / 4 != 1 || !sprite.isAnimated())) {
			sprite.Animate(FRAME, 4, 4);
		}
		if (sprite.isAnimated()) {
			if (velocity.getX() == 0) {
				sprite.setFrame(frame - frame % 4);
			}
		}

		velocity.addY(PlatformLogic.GRAVITY);

		boolean collideX = false, collideY = false, collideBoth = false;
		temp.set(velocity);

		for (int k = 0; k < layers.length; k++) {

			if (!map.layers[k].active)
				continue;

			Sprite[][] sprites = layers[k].getSprites();

			if (velocity.getX() != 0) {
				rect.offset(velocity.getX() * timeElapsed, 0);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (s.getRect().intersect(rect)) {
								rect.offset(-velocity.getX() * timeElapsed, 0);
								velocity.setX(0);
								collideX = true;
								break;

							}

						}
					}
				}
				rect.offset(-velocity.getX() * timeElapsed, 0);
			}
			
			boolean overEdge = false;
			if (velocity.getY() != 0) {
				rect.offset(0, velocity.getY() * timeElapsed);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (s.getRect().intersect(rect)) {
								rect.offset(0, -velocity.getY() * timeElapsed);
								velocity.setY(0);
								collideY = true;
								break;
							}
						}
					}
				}
				rect.offset(0, -velocity.getY() * timeElapsed);
			}

			if (velocity.getX() != 0 && velocity.getY() != 0) {
				rect.offset(velocity.getX() * timeElapsed, velocity.getY() * timeElapsed);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (s.getRect().intersect(rect)) {
								rect.offset(-velocity.getX() * timeElapsed, -velocity.getY() * timeElapsed);
								velocity.clear();
								collideBoth = true;
								break;

							}

						}
					}
				}
				rect.offset(-velocity.getX() * timeElapsed, -velocity.getY() * timeElapsed);
			}
		}
		
		grounded = velocity.getY() == 0;
		body.updatePhysics(timeElapsed);
		body.updateSprite();
		
		if (collideX) {
			velocity.setX(-Math.signum(temp.getX()) * actor.speed);
		}
	}

	public void updateSprite() {
		body.updateSprite();
	}
}
