package com.twp.platform;

import java.util.ArrayList;

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
	private boolean isHero;
	private ArrayList<PlatformBody> actors;
	private int lastDir;

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

	public PlatformBody(Viewport viewport, Physics physics, PlatformActor actor, float startX, float startY, 
			Tilemap[] layers, PlatformMap map, boolean isHero, ArrayList<PlatformBody> actors) {
		this.actor = actor;
		Bitmap[] frames = Tilemap.createTiles(Data.loadBitmap(actor.imageId), 32, 48, 0); 
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.body = new Body(physics, sprite);
		this.layers = layers;
		this.map = map;
		this.isHero = isHero;
		this.actors = actors;

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

		boolean collideX = false, collideY = false, collideBoth = false, edge = false;

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

			if (velocity.getY() != 0) {
				int edgeCount = 0;
				boolean thisEdge = false;
				rect.offset(0, velocity.getY() * timeElapsed);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (s.getRect().intersect(rect)) {
								if (edgeCount > 0) {
									thisEdge = false;
									break;
								}

								if (rect.left < s.getRect().left && velocity.getX() < 0 ||
										rect.right > s.getRect().right && velocity.getX() > 0) {
									thisEdge = true;
								}
								edgeCount++;

								collideY = true;
							}
						}
					}
				}
				rect.offset(0, -velocity.getY() * timeElapsed);
				if (collideY) {
					velocity.setY(0);
				}
				edge |= thisEdge;
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

		RectF myRect = sprite.getRect();
		for (int i = 0; i < actors.size(); i++) {
			PlatformBody thatActor = actors.get(i);
			if (this != thatActor) {
				RectF sRect = thatActor.getSprite().getRect();
				if (myRect.intersect(sRect)) {
					int[] behaviors = thatActor.isHero ? actor.heroContactBehaviors : actor.actorContactBehaviors;
					if (myRect.right > sRect.left && myRect.left < sRect.left) {
						doBehavior(behaviors[PlatformActor.RIGHT], temp);
						break;
					} else if (myRect.left < sRect.right && myRect.right > sRect.right) {
						doBehavior(behaviors[PlatformActor.LEFT], temp);
						break;
					} else if (myRect.bottom > sRect.top && myRect.top < sRect.top) {
						doBehavior(behaviors[PlatformActor.BELOW], temp);
						break;
					} else if (myRect.top < sRect.bottom && myRect.bottom > sRect.bottom) {
						doBehavior(behaviors[PlatformActor.LEFT], temp);
						break;
					}
				}
			}
		}

		if (collideX) {
			doBehavior(actor.wallBehavior, temp);
		} else if (edge) {
			doBehavior(actor.edgeBehavior, temp);
		}
	}

	public void updateSprite() {
		body.updateSprite();
	}

	private void doBehavior(int behavior, Vector oldVelocity) {		
		Vector velocity = body.getVelocity();
		switch (behavior) {
		case PlatformActor.BEHAVIOR_STOP:
			velocity.setX(0);
			break;
		case PlatformActor.BEHAVIOR_JUMP_TURN:
			velocity.setY(-actor.jumpVelocity);
		case PlatformActor.BEHAVIOR_TURN:
			velocity.setX(-Math.signum(oldVelocity.getX()) * actor.speed);
			break;
		case PlatformActor.BEHAVIOR_JUMP:
			velocity.setY(-actor.jumpVelocity);
			break;
		case PlatformActor.BEHAVIOR_START_STOP:
			//if (lastDir == 0)) //FINISH
			break;
		}	
	}
}
