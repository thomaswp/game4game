package edu.elon.honors.price.graphics;

import java.util.LinkedList;

import android.graphics.RectF;

public class Graphics {

	final static int FRAME_BLOCK = 60;

	private static LinkedList<Viewport> viewports = new LinkedList<Viewport>();
	private static int width, height;

	private static int frameCountDraw = 0;
	private static long lastSystemTimeDraw = System.currentTimeMillis();
	private static int fpsDraw;
	private static int frameCountGame = 0;
	private static long lastSystemTimeGame = System.currentTimeMillis();
	private static int fpsGame;
	private static boolean showFPS;
	private static boolean fpsBitmapRefresh;
	
	public static boolean getFPSBitmapRefresh() {
		return fpsBitmapRefresh;
	}
	
	public static void setFPSBitmapRefresh(boolean refresh) {
		Graphics.fpsBitmapRefresh = refresh;
	}

	/**
	 * Returns the width of the current drawable area.
	 * @return The width
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the current drawable area.
	 * @return
	 */
	public static int getHeight() {
		return height;
	}
	
	public static RectF getRect() {
		return new RectF(0, 0, width, height);
	}
	
	public static boolean isShowingFPS() {
		return showFPS;
	}

	public static void setShowingFPS(boolean showFPS) {
		Graphics.showFPS = showFPS;
	}

	public static int getFpsDraw() {
		return fpsDraw;
	}

	public static int getFpsGame() {
		return fpsGame;
	}
	
	public static LinkedList<Viewport> getViewports() {
		return viewports;
	}

	/**
	 * Adds a viewport to the list of drawn viewports.
	 * @param viewport The viewport.
	 */
	public static void addViewport(Viewport viewport) {
		viewports.add(viewport);
	}

	public static void resize(int width, int height) {
		Graphics.width = width;
		Graphics.height = height;
	}
	
	/**
	 * Updates the Graphics class, which updates all of its Viewports.
	 */
	public static void update(long timeElapsed) {
		for (Viewport viewport : viewports) {
			if (viewport != null) {
				viewport.upadte(timeElapsed);
			}
		}
		updateFPSGame();
	}
	
	public static int powerOfTwoSize(int size) {
		int s = 1;
		while (s < size) s *= 2;
		return s;
	}
	
	public static void reset() {
		for (Viewport v : viewports) {
			for (Sprite s : v.getSprites()) {
				s.dispose();
			}
		}
		viewports.clear();
		viewports.add(Viewport.DefaultViewport);
	}

	public static void updateFPSDraw() {
		frameCountDraw = (frameCountDraw + 1) % FRAME_BLOCK;
		if (frameCountDraw == 0) {
			long currentTime = System.currentTimeMillis();
			fpsDraw = (int)(FRAME_BLOCK * 1000 / (currentTime - lastSystemTimeDraw));
			lastSystemTimeDraw = currentTime;
			fpsBitmapRefresh = true;
		}
	}

	private static void updateFPSGame() {
		frameCountGame = (frameCountGame + 1) % FRAME_BLOCK;
		if (frameCountGame == 0) {
			long currentTime = System.currentTimeMillis();
			fpsGame = (int)(FRAME_BLOCK * 1000 / (currentTime - lastSystemTimeGame));
			lastSystemTimeGame = currentTime;
			fpsBitmapRefresh = true;
		}
	}
	
}
