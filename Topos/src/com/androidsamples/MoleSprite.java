package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class MoleSprite extends View{

	public static final int HOLE = 0;
	public static final int DIGUP1 = 1;
	public static final int DIGUP2 = 2;
	public static final int BEATEN = 3;

	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private int x = 0;
	private int y = 0;


	private Bitmap bmp;
	private int width;
	private int height;	
	private int direction;

	private static final String tag = "TAG";

	public MoleSprite(ToposGameView gameView, int x, int y, int direction) {
		super(gameView.getContext());
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pruebanumeros);
		this.width = bmp.getWidth() / BMP_COLUMNS;		
		this.height = bmp.getHeight() / BMP_ROWS;
		
		this.direction=direction;

		this.x=x;
		this.y= y;

	}


	public int getX() {
		return x;
	}


	public int getY() {
		return y;
	}


	public int getMoleWidth() {
		return width;
	}


	public int getMoleHeight() {
		return height;
	}


	public void onDraw(Canvas canvas) {
		int newheight = direction * height;
		Rect src = new Rect(width, newheight, 2*width, height+newheight);
		Rect dst = new Rect(x, y, x + width, y+height);
		canvas.drawBitmap(bmp, src, dst, null);   
	}

	public void turnMole(int direction){

		try {
			
			
			this.direction = direction;
			//lock.wait(10000);//TODO no podemos dormir el hilo.
		
			
			} catch (Exception e) {
			 throw new RuntimeException("Error turnMole");
			}
		
	}

}
