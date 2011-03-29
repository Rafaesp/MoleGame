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

	private static final int BMP_ROWS = 1;
	private static final int BMP_COLUMNS = 1;
	private int x = 0;
	private int y = 0;

	private ToposGameView gameView;
	private Bitmap bmp;
	private int width;
	private int height;	
	private int status; //Each row, each frame of the animation
	private int animation = 0; //Each column, for example entering hole or being hit

	private static final String tag = "TAG";

	public MoleSprite(ToposGameView gameView, int x, int y, int status) {
		super(gameView.getContext());
		this.gameView = gameView;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.png100x120);
		this.width = bmp.getWidth() / BMP_COLUMNS;		
		this.height = bmp.getHeight() / BMP_ROWS;
		
		this.status=status;

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
		int srcy = status * height;
		int srcx = animation * width;
		Rect src = new Rect(srcx, srcy, srcx+width, srcy+height);
		Rect dst = new Rect(x, y, x + gameView.getWidth()/3, y+gameView.getHeight()/4);
		canvas.drawBitmap(bmp, src, dst, null);   
	}

	public void turnMole(int status){

		try {
			
			
			this.status = status;
			//lock.wait(10000);//TODO no podemos dormir el hilo.
		
			
			} catch (Exception e) {
			 throw new RuntimeException("Error turnMole");
			}
		
	}
	
	public boolean isClicked(float eventx, float eventy){
		boolean coordx = getX() <= eventx && getX()+getMoleWidth() >= eventx;
		boolean coordy = getY() <=eventy && getY()+getMoleHeight() >= eventy;

		if(coordx && coordy){
			return true;
		}
		
		return false;
	}
	
	public String toString(){
		return "Mole x: "+x+", y: "+y+", width: "+width+", \nheight: "+height+" ,dstWidth: "+gameView.getWidth()/3+", dstHeight: "+gameView.getHeight()/4;
	}

}
