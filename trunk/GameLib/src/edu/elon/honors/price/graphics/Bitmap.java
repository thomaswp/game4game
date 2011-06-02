package edu.elon.honors.price.graphics;

public class Bitmap {
	private android.graphics.Bitmap androidBitmap;
	
	private int width, height, realWidth, realHeight;
	
	public Bitmap(int width, int height) {
		realWidth = 1;
		while (realWidth < width) realWidth *= 2;
		realHeight = 1;
		while (realHeight < height) realHeight *= 2;
		
		this.width = width;
		this.height = height;
		
		//androidBitmap = new 
	}
}
