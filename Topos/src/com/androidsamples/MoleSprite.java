package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class MoleSprite extends View{

	public static final int FRONT = 0;
	public static final int LEFT = 1;
	public static final int BACK = 3;
	public static final int RIGHT = 2;

	private static final int BMP_ROWS = 1;
	private static final int BMP_COLUMNS = 1;
	private int x = 0;
	private int y = 0;


	private Bitmap bmp;
	private int width;
	private int height;	
	private int direction;

	private static final String tag = "TAG";

	public MoleSprite(ToposGameView gameView, int x, int y, int direction) {
		super(gameView.getContext());
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.png100x120);
		this.width = bmp.getWidth() / BMP_COLUMNS;		
		this.height = bmp.getHeight() / BMP_ROWS;

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
		this.direction = direction;
	}

}
