package com.androidsamples;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private List<MoleSprite> moles;
	private boolean needRedraw;

	public ToposGameView(Context context){
		super(context);
		initToposGameView();
	}

	public ToposGameView(Context context, AttributeSet attrs){
		super(context, attrs);
		initToposGameView();

	}

	private void initToposGameView(){
		moles = new LinkedList<MoleSprite>();
		needRedraw = true;
		setFocusable(true);
		setOnTouchListener(this);

		gameLoopThread = new GameLoopThread(this);

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
				Log.i(tag, "GameView width: "+getWidth()+" GameView height: "+getHeight());
				createMoles();
				gameLoopThread.setRunning(true);
				gameLoopThread.start();

			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {

			}
		});

	}

	private void createMoles(){
		for(int x = 0; x<3; x++){ 
			for(int y = 0; y<4 ; y++){
				MoleSprite mole = new MoleSprite(this, x, y);
				moles.add(mole);
				Log.i(tag, mole.toString());
			}
		}
	}
	
	public List<MoleSprite> getMoles(){
		return moles;
	}
	
	public boolean needRedraw(){
		return needRedraw;
	}
	
	public void setRedraw(boolean need){
		needRedraw = need;
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		needRedraw = false;
		for(MoleSprite mole : moles){
			mole.onDraw(canvas);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			synchronized(getHolder()){
				for(MoleSprite mole : moles){
					if(mole.isClicked(event.getX(), event.getY())){
						Log.i(tag, "Mole clicked: "+mole.toString());
						if(mole.getStatus() == MoleSprite.DIGUPFULL){
							mole.digDown();
//						}else if(mole.getStatus()==MoleSprite.HOLE){
//							mole.digUp();
						}
					}
				}	
			}

		}
		return false;
	}

}