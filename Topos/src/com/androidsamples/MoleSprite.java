package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class MoleSprite extends View{

	public static final int HOLE = 4;
	public static final int DIGUP1 = 3;
	public static final int DIGUP2 = 2;
	public static final int DIGUP3 = 1;
	public static final int DIGUPFULL = 0;
	public static final int HIT = -1;
	private static final long ANIMATION_HIT_TIME = 500;
	private static final long ANIMATION_DIGGING_TIME = 500;

	private static final int BMP_ROWS = 5;
	private static final int BMP_COLUMNS = 1;
	private int x = 0;
	private int y = 0;

	private ToposGameView gameView;
	private Bitmap bmp;
	private int width;
	private int height;	
	private int status; //Each row, each frame of the animation
	private int animation = 0; //Each column, for example entering hole or being hit
	private boolean isHit = false;

	private boolean isDigging;
	private long animationHitStartTime;
	private long animationDigStartTime;
	private int diggingDirection = 0; //Up-> -1   Down -> 1
	private int diggingTick = 1; //How many frames have we already shown

	private static final String tag = "TAG";

	public MoleSprite(ToposGameView gameView, int x, int y, int status) {
		super(gameView.getContext());
		this.gameView = gameView;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.topos75x90);
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
		return x + gameView.getWidth()/3;
	}


	public int getMoleHeight() {
		return y+gameView.getHeight()/4;
	}


	public int getStatus() {
		return status;
	}


	public void onDraw(Canvas canvas) {
		isHit();
		isDigging();
		int srcy = status * height;
		int srcx = animation * width;
		Rect src = new Rect(srcx, srcy, srcx+width, srcy+height);
		Rect dst = new Rect(x, y, x + gameView.getWidth()/3, y+gameView.getHeight()/4);
		canvas.drawBitmap(bmp, src, dst, null);   
	}

	public void changeStatus(int status){
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
		if(animationHitStartTime+ANIMATION_HIT_TIME >= System.currentTimeMillis()){
			isHit = false;
			this.changeStatus(DIGUPFULL);

		}
		return isHit;
	}

	public String toString(){
		return "Mole x: "+x+", y: "+y+", width: "+width+", \nheight: "+height+" ,dstWidth: "+gameView.getWidth()/3+", dstHeight: "+gameView.getHeight()/4;
	}

	public void hit(){ //TODO Shouldn't be able to click when already hit
		animationHitStartTime = System.currentTimeMillis();
		isHit = true;
		this.changeStatus(HIT);

	}

	public boolean isDigging(){
		Long timeElapsed = System.currentTimeMillis() - animationDigStartTime;
		if(timeElapsed <= ANIMATION_DIGGING_TIME){
			Log.i(tag, "Enters in digging if1");
			if(diggingTick <=4 && timeElapsed <= diggingTick*ANIMATION_DIGGING_TIME/4){
				status = status+diggingDirection;
				diggingTick++;
				Log.i(tag, "timeElapsed: "+timeElapsed.intValue());
			}
		}else{
			diggingTick = 1;	
		}
		isDigging = false;

		return isDigging;
	}

	public void digUp(){
		diggingDirection = -1;
		animationDigStartTime = System.currentTimeMillis();
		isDigging = true;
	}

	public void digDown(){
		diggingDirection = 1;
		animationDigStartTime = System.currentTimeMillis();
		isDigging = true;
	}

}
