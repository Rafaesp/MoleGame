package com.androidsamples;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class ToposGameView extends SurfaceView implements OnTouchListener{

	private static final String tag = "TAG";
	private static final int WIDTH =topos.getWidth();
	private static final int HEIGHT=topos.getHeight();
	
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private MoleSprite moleClicked;

	private ArrayList<MoleSprite> moles;

	public ToposGameView(Context context) {
		super(context);
		initToposGameView();		
	}
	

	public ToposGameView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        initToposGameView();     
	}
	
	private void initToposGameView(){
		moles = new ArrayList<MoleSprite>();
		setFocusable(true);
		setOnTouchListener(this);

		gameLoopThread = new GameLoopThread(this);

		for(int x = 0; x<3; x++){
			for(int y = 0; y<4 ; y++){

//				MoleSprite mole = new MoleSprite(this, x*WIDTH/3, HEIGHT/6+y*HEIGHT/6, MoleSprite.FRONT);
				MoleSprite mole = new MoleSprite(this, x*WIDTH/3, y*HEIGHT/4, 0);
				moles.add(mole);
				Log.i(tag, mole.getX()+" "+mole.getY()+" "+mole.getMoleWidth()+" "+mole.getMoleHeight());
			}
		}

		holder = getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				boolean retry=true;
				gameLoopThread.setRunning(false);
				while(retry){
					try{
						gameLoopThread.join();
						retry=false;

					}catch(InterruptedException i){

					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				gameLoopThread.setRunning(true);
				gameLoopThread.start();

			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {

			}
		});

	}
	
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		for(MoleSprite mole : moles){
			mole.onDraw(canvas);
		}

	}


	private boolean isMoleClicked(float x, float y){
		for(MoleSprite mole : moles){
			boolean coordx = mole.getX() <= x && mole.getX()+mole.getMoleWidth() >= x;
			boolean coordy = mole.getY() <=y && mole.getY()+mole.getMoleHeight() >= y;

			if(coordx && coordy){
				moleClicked = mole;
				return true;
			}
		}
		return false;		
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(isMoleClicked(event.getX(), event.getY())){
				moleClicked.turnMole(MoleSprite.BACK);
				return true;
			}
		}
		return false;
	}


}