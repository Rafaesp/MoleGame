package com.androidsamples;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class ToposGameView extends SurfaceView implements OnTouchListener{

	private static final String tag = "TAG";
	private static final int WIDTH =topos.getWidth()-100; // TODO hay que hacer bien los calculos para que salga centrado en pantalla, no esta resta cutre, (100 es el tamaño de la imagen)
	private static final int HEIGHT=topos.getHeight();
	
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private MoleSprite moleClicked;

	private ArrayList<MoleSprite> moles;

	public ToposGameView(Context context) {
		super(context);

		moles = new ArrayList<MoleSprite>();
		setFocusable(true);
		setOnTouchListener(this);

		gameLoopThread = new GameLoopThread(this);

		int id = 0;
		
		for(int x = 1; x<4; x++){ 
			for(int y = 0; y<4 ; y++){

				MoleSprite mole = new MoleSprite(this, x*WIDTH/4, HEIGHT/6+y*HEIGHT/6, MoleSprite.HOLE);
				mole.setId(id);
				id++;
				moles.add(mole);
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
				moleClicked.turnMole(MoleSprite.BEATEN);
				try {
					Thread.sleep(301); //TODO esto no esta bien hecho, pero hay que conseguir algo asi, hoy no estoy muy lucido.
				} catch (InterruptedException e) {
					throw new RuntimeException("Check onTouch(View,MotionEvent) in ToposGameView.class");
					
				}
				moleClicked.turnMole(MoleSprite.HOLE);
				return true;
			}
		}
		return false;
	}


}