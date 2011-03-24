package com.androidsamples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class ToposGameView extends SurfaceView implements OnClickListener {

	private Bitmap bmp;
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private int x = 0;
	private int xSpeed = 1;
	private Sprite sprite;

	public ToposGameView(Context context) {
		super(context);
		gameLoopThread = new GameLoopThread(this);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bad1);

		setOnClickListener(this);// TODO
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

		sprite = new Sprite(this, bmp);

	}

	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.GREEN);
		sprite.onDraw(canvas);

	}

	@Override
	public void onClick(View v) {

		if (v.getId()==sprite.getId()) {
			sprite.setMoving(true);
			

		}

	}

}
