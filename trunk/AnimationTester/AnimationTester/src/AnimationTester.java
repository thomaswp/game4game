import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.Timer;

import java.io.File;
import java.io.IOException;


public class AnimationTester extends JApplet {
	private static final long serialVersionUID = 1L;
	
	private Image spritesheet;
	private Hero hero;
	private long lastUpdate;
	private AnimCanvas canvas;
	
	@Override
	public void init() {
		resize(800, 500);
		canvas = new AnimCanvas(); 
		loadImage();
		add(canvas);
		lastUpdate = System.currentTimeMillis();
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long timeElapsed = System.currentTimeMillis() - lastUpdate;
				hero.update(timeElapsed, canvas.getWidth(), canvas.getHeight());
				lastUpdate += timeElapsed;
				canvas.repaint();
			}
		});
		canvas.requestFocus();
		timer.start();
	}
	
	@SuppressWarnings("deprecation")
	private void loadImage() {
		String file = "C:/StickMan.png";
		FileDialog dialog = new FileDialog(findParentFrame(), "Load Image", FileDialog.LOAD);
		dialog.show();
		file = dialog.getDirectory() + dialog.getFile();
		//System.out.println(file);
		try {
			//spritesheet = getImage(new URL("file:///" + file));
			spritesheet = ImageIO.read(new File(file));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(spritesheet, 0);
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		hero = new Hero(spritesheet);
		canvas.addKeyListener(hero);
	}
	
	public Frame findParentFrame(){
	    java.awt.Component c = this;

	    while(true) {
	        if(c instanceof Frame)
	            return (Frame)c;
	        c = c.getParent();
	    }
	}
	
	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		super.update(g);
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.resize(arg0, arg1);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}

	public class AnimCanvas extends Canvas {
		private static final long serialVersionUID = 1L;
		
		Dimension offDimension;
		Image offImage;
		Graphics offGraphics;
		
		@Override 
		public void update(Graphics g) {
			Dimension d = getSize();
			if ( (offGraphics == null)
					|| (d.width != offDimension.width)
					|| (d.height != offDimension.height) ) {
				offDimension = d;
				offImage = createImage(d.width, d.height);
				offGraphics = offImage.getGraphics();
			}
			offGraphics.clearRect(0, 0, d.width, d.height);
			hero.draw(offGraphics, getWidth(), getHeight());
			Font f = new Font("Arial", Font.PLAIN, 16);
			offGraphics.setFont(f);
			drawText(offGraphics, hero.animator.toString(), getWidth() - 150, 0);
			drawText(offGraphics, "Controls:\nW: Move Left\n" +
					"D: Move Right\nSpace: Jump\nEnter: Action\n" +
					"Q: Climb Ladder", 0, 0);
			
			g.drawImage(offImage, 0, 0, null);
		}
	}
	
	private void drawText(Graphics g, String s, int x, int y) {
		String[] lines = s.split("\n");
		for (String line : lines) {
			y += g.getFont().getSize();
			g.drawString(line, x, y);
		}
	}
}
