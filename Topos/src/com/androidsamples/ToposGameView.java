package com.androidsamples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class ToposGameView extends SurfaceView implements OnClickListener {


	private static final int WIDTH =topos.getWidth();

	
	private static final int HEIGHT=topos.getHeight();
	
	private Bitmap bmp;
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;;
	private Sprite mole1;
	private Sprite mole2;
	private Sprite mole3;
	private Sprite mole4;
	private Sprite mole5;
	private Sprite mole6;
	private Sprite mole7;
	private Sprite mole8;
	private Sprite mole9;
	private Sprite mole10;
	private Sprite mole11;
	private Sprite mole12;
	
	private Sprite[] moleArray;
	
	
	

	public ToposGameView(Context context) {
		super(context);
		
		

		
		
		
		
		gameLoopThread = new GameLoopThread(this);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bad1);

		setOnClickListener(this);// TODO
		
		mole1 = new Sprite(this, this.bmp, WIDTH/4, (2*HEIGHT)/8);
		mole2 = new Sprite(this, this.bmp, (2*WIDTH)/4, (2*HEIGHT)/8);
		mole3 = new Sprite(this, this.bmp, (3*WIDTH)/4, (2*HEIGHT)/8);
		
		mole4 = new Sprite(this, this.bmp, WIDTH/4, (3*HEIGHT)/8);
		mole5 = new Sprite(this, this.bmp, (2*WIDTH)/4, (3*HEIGHT)/8);
		mole6 = new Sprite(this, this.bmp, (3*WIDTH)/4, (3*HEIGHT)/8);
		
		mole7 = new Sprite(this, this.bmp, WIDTH/4, (4*HEIGHT)/8);
		mole8 = new Sprite(this, this.bmp, (2*WIDTH)/4, (4*HEIGHT)/8);
		mole9 = new Sprite(this, this.bmp, (3*WIDTH)/4, (4*HEIGHT)/8);
		
		mole10 = new Sprite(this, this.bmp, WIDTH/4, (5*HEIGHT)/8);
		mole11 = new Sprite(this, this.bmp, (2*WIDTH)/4, (5*HEIGHT)/8);
		mole12 = new Sprite(this, this.bmp, (3*WIDTH)/4, (5*HEIGHT)/8);
		
		Log.i("Ancho", ""+WIDTH);//TODO
		Log.i("Alto", ""+HEIGHT);
		
		
		
		
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
				// TODO Auto-generated method stub

			}
		});

		//sprite = new Sprite(this, bmp);

	}

	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.GREEN);
//		sprite.onDraw(canvas);
		mole1.onDraw(canvas);
		mole2.onDraw(canvas);
		mole3.onDraw(canvas);
		
		mole4.onDraw(canvas);
		mole5.onDraw(canvas);
		mole6.onDraw(canvas);
		
		mole7.onDraw(canvas);
		mole8.onDraw(canvas);
		mole9.onDraw(canvas);
		
		mole10.onDraw(canvas);
		mole11.onDraw(canvas);
		mole12.onDraw(canvas);

	}

	@Override
	public void onClick(View v) {

//		if (v.getId()==sprite.getId()) {
//			sprite.setMoving(true);
//			

		}

	}


