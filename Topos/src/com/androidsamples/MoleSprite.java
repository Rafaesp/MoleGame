package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;

public class MoleSprite extends View{

	public static final int HOLE = 0;
	public static final int DIGUP1 = 1;
	public static final int DIGUP2 = 2;
	public static final int HIT = 3;

	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private int x = 0;
	private int y = 0;

	private ToposGameView gameView;
	private Bitmap bmp;
	private int width;
	private int height;	
	private int status; //Each row, each frame of the animation
	private int animation = 1; //Each column, for example entering hole or being hit
	private boolean isHit = false;
	private long hitStartTime;
	private final long ANIMATION_TIME = 500;
	
	private static final String tag = "TAG";

	public MoleSprite(ToposGameView gameView, int x, int y, int status) {
		super(gameView.getContext());
		this.gameView = gameView;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pruebanumeros);
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
		isHit();
		int srcy = status * height;
		int srcx = animation * width;
		Rect src = new Rect(srcx, srcy, srcx+width, srcy+height);
		Rect dst = new Rect(x, y, x + gameView.getWidth()/3, y+gameView.getHeight()/4);
		canvas.drawBitmap(bmp, src, dst, null);   
	}

	public void turnMole(int status){

		this.status = status;

	}

	public boolean isClicked(float eventx, float eventy){
		boolean coordx = getX() <= eventx && getX()+getMoleWidth() >= eventx;
		boolean coordy = getY() <=eventy && getY()+getMoleHeight() >= eventy;

		if(coordx && coordy){
			return true;
		}

		return false;
	}
	
	public boolean isHit(){
		if(hitStartTime+ANIMATION_TIME - System.currentTimeMillis() <= 0){
			isHit = false;
			this.turnMole(DIGUP2);
		}
		return isHit;
	}

	public String toString(){
		return "Mole x: "+x+", y: "+y+", width: "+width+", \nheight: "+height+" ,dstWidth: "+gameView.getWidth()/3+", dstHeight: "+gameView.getHeight()/4;
	}
	
	public void doHit(){ //TODO Shouldn't be able to click when already hit
		hitStartTime = System.currentTimeMillis();
		isHit = true;
		this.turnMole(HIT);
		
	}



}
