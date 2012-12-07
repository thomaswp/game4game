import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.management.RuntimeErrorException;

import edu.elon.honors.price.data.ActorAnimator;
import edu.elon.honors.price.data.ActorAnimator2;
import edu.elon.honors.price.data.ActorAnimator5;
import edu.elon.honors.price.data.ActorAnimator7;
import edu.elon.honors.price.data.ActorAnimator.Action;


public class Hero implements KeyListener  {
	private float x, y, vx, vy;
	private Image image;
	private Image[][] frames;
	
	
	int frame, action;
	int frameWidth, frameHeight;

	public ActorAnimator animator = new ActorAnimator5();
	
	private boolean grounded, climbing;
	private boolean left, right;
	
	
	public Hero(Image image) {
		this.image = image;
	}
	
	
	private void loadFrames(int width, int height) {
		BufferedImage bi = new BufferedImage(
				image.getWidth(null), image.getHeight(null), 
				BufferedImage.TYPE_4BYTE_ABGR_PRE);
		bi.getGraphics().drawImage(image, 0, 0, null);
		Action[] actions = Action.values();
		frames = new Image[actions.length][];
		frameWidth = image.getWidth(null) / animator.getTotalCols();
		frameHeight = image.getHeight(null) / animator.getTotalRows();
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			ActorAnimator.ActionParams actionParams =
					animator.getActionParams(action);
			frames[i] = new Image[actionParams.frames];
			for (int j = 0; j < actionParams.frames; j++) {
				frames[i][j] = bi.getSubimage(
						(actionParams.column + j) * frameWidth, 
						actionParams.row * frameHeight, 
						frameWidth, frameHeight);
			}
		}
	}
	
	private int getDir() {
		int dir = 0;
		if (left) dir--; if (right) dir++;
		return dir;
	}
	
	private void updateAnimation(long timeElapsed) {
		animator.update(timeElapsed, getDir(), grounded, climbing);
		
		frame = animator.getFrame();
		action = animator.getAction();
	}
	
	public void update(long timeElapsed, int width, int height) {
		if (frames == null) {
			loadFrames(width, height);
		} 
		
		if (frames != null) {
			float dt = timeElapsed / 1000f;
			vy += 1000f * dt;
			if (climbing) {
				vy = -300;
			}
			y += vy * dt;
			
			if (!climbing) {
				if (left) {
					x -= 200 * dt;
				}
				if (right) {
					x += 200 * dt;
				}
			}
			
			if (x < 0) {
				x = 0;
				vx = 0;
			}
			if (x > width - frameWidth) {
				x = width - frameWidth;
				vx = 0;
			}
			grounded = false;
			if (y > height - frameHeight) {
				y = height - frameHeight;
				vy = 0;
				grounded = true;
			}
			
			updateAnimation(timeElapsed);
		}
	}
	
	public void draw(Graphics g, int width, int height) {
		if (frames != null) {
			AffineTransform a = new AffineTransform();
			a.translate(x, y);
			if (animator.isFlipped()) {
				a.translate(frameWidth, 0);
				a.scale(-1, 1);
			}
			((Graphics2D)g).drawImage(frames[action][frame], a, null);
			//g.drawImage(frames[action][frame], (int)x, (int)y, null);
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (grounded) {
				vy -= 600;
				animator.jump();
				climbing = false;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			left = true;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			animator.action();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			climbing = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A) {
			left = false;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			climbing = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
