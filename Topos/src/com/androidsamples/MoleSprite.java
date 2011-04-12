package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class MoleSprite extends View{

	public static final int HOLE = 4;
	public static final int DIGUP1 = 3;
	public static final int DIGUP2 = 2;
	public static final int DIGUP3 = 1;
	public static final int DIGUPFULL = 0;
	public static final int HIT1 = 3;
	public static final int HITFULL = 2;
	public static final int ANIMATIONHIT = 1;
	private static final long ANIMATION_HIT_TIME = 400;
	private static final long ANIMATION_DIGGING_TIME = 300;
	private static final int MAX_DIGGING_TICKS = 4; //total number frames of animation -1
	private static final int MAX_HIT_TICKS = 2;
	private static final int BMP_ROWS = 5;
	private static final int BMP_COLUMNS = 2;
	public static final int BIGMOLE = 1;

	private int posx;
	private int posy;
	private int x;
	private int y;

	private ToposGameView view;
	private Bitmap bmp;
	private int width;
	private int height;	
	private int status; //Each row, each frame of the animation
	private int animation = 0; //Each column, for example entering hole or being hit
	private boolean isHit;
	private boolean isDigging;
	private long animationHitStartTime;
	private long animationDigStartTime;
	private int diggingDirection = 0; //Up-> -1   Down -> 1
	private int diggingTick = 0; //How many frames have we already shown
	private int bigClicks;


	private static final String tag = "TAG";

	public MoleSprite(ToposGameView view, int posx, int posy) {
		super(view.getContext());
		this.view = view;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.digginghit);
		this.width = bmp.getWidth() / BMP_COLUMNS;		
		this.height = bmp.getHeight() / BMP_ROWS;

		this.posx=posx;
		this.posy=posy;

		status = HOLE;
		isDigging = false;
		isHit = false;
		bigClicks = 0;
	}

	public int getX() {
		int gap_width = view.getWidth()/10;
		return gap_width/2+(posx)*(view.getWidth()/3);
	}
	public int getY() {
		int gap_height = view.getHeight()/10;
		return gap_height/2+(posy)*view.getHeight()/4;
	}
	public int getMoleWidth() {
		int gap_width = view.getWidth()/10;
		return view.getWidth()/3-gap_width;
	}
	public int getMoleHeight() {
		int gap_height = view.getHeight()/10;
		return view.getHeight()/4-gap_height;
	}
	public int getStatus() {
		return status;
	}
	public void changeStatus(int status){
		this.status = status;
	}

	public boolean isBig() {
		return animation == BIGMOLE;
	}

	public void setBig() {
		this.animation = BIGMOLE;
	}

	public void resetBigClicks(){
		bigClicks = 0;
	}

	public void addBigClick(){
		bigClicks++;
	}

	public Integer getBigClicks(){
		return bigClicks;
	}

	public void onDraw(Canvas canvas) {
		dig();
		hit();
		int srcy = status * height;
		int srcx = animation * width;
		Rect src = new Rect(srcx, srcy, srcx+width, srcy+height);
		Rect dst = new Rect(getX(), getY(), getX()+getMoleWidth(), getY()+getMoleHeight());
		canvas.drawBitmap(bmp, src, dst, null);   
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
		return isHit;
	}

	public String toString(){
		return "Mole x: "+x+", y: "+y+", width: "+width+", \nheight: "+height+" ,dstWidth: "+view.getWidth()/3+", dstHeight: "+view.getHeight()/4;
	}

	private void hit(){
		if(isHit){
			animation = ANIMATIONHIT;
			Long timeElapsed = System.currentTimeMillis() - animationHitStartTime;
			status = HITFULL;
			if(timeElapsed >= ANIMATION_HIT_TIME/MAX_HIT_TICKS){
				status = HIT1;
			}else if(timeElapsed >= ANIMATION_HIT_TIME){
				status = HOLE;
				isHit = false;
				animation = 0;
			}


		}

	}

	public boolean isDigging(){
		return isDigging;
	}

	public boolean isDiggingDown(){
		return isDigging && diggingDirection == 1;
	}

	private void dig(){
		if(isDigging){
			Long timeElapsed = System.currentTimeMillis() - animationDigStartTime;
			if(diggingTick != MAX_DIGGING_TICKS){
				if(timeElapsed >= diggingTick*ANIMATION_DIGGING_TIME/MAX_DIGGING_TICKS){
					status = status+diggingDirection;
					diggingTick++;
				}
			}else{
				diggingTick = 0;
				if(isDiggingDown()){
					animation = 0;
				}
				isDigging = false;
			}

		}
	}

	public void doHit(){
		animationHitStartTime = System.currentTimeMillis();
		isHit = true;
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

	public boolean equals(Object o){
		boolean resX=false;
		boolean resY=false;
		if(o instanceof MoleSprite){
			resX=this.posx==((MoleSprite) o).posx;
			resY=this.posy==((MoleSprite) o).posy;
		}
		return resX && resY;
	}

	public long getDigStartTime() {
		return animationDigStartTime+ANIMATION_DIGGING_TIME;
	}

	public void reset(){
		animation = 0;
		changeStatus(HOLE);
		diggingTick=0;
		isDigging = false;
		isHit = false;
	}
}
