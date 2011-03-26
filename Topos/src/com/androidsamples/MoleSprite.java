package com.androidsamples;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class MoleSprite extends View{

	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private int x = 0;
	private int y = 0;


	private Bitmap bmp;
	private int width;
	private int height;	
	
	public MoleSprite(ToposGameView gameView, int x, int y, int direction) {
		super(gameView.getContext()); //TODO
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bad1);
		this.width = bmp.getWidth() / BMP_COLUMNS;		
		this.height = bmp.getHeight() / BMP_ROWS;
		
		this.x=x;
		this.y= y;
	}


	public void onDraw(Canvas canvas) {
        Rect src = new Rect(width, height, 2*width, 2*height);
		Rect dst = new Rect(x, y, x + width, y+height);
        canvas.drawBitmap(bmp, src, dst, null);   
	}

}
