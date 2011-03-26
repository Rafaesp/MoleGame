package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class ToposGameView extends SurfaceView implements OnClickListener {

	private static final String tag = "TAG";
	private static final int WIDTH =topos.getWidth();
	private static final int HEIGHT=topos.getHeight();
	private static final int FRONT = 0;
	private static final int LEFT = 1;
	private static final int BACK = 3;
	private static final int RIGHT = 2;

	private Bitmap bmp;
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;;

	private List<MoleSprite> moles;

	public ToposGameView(Context context) {
		super(context);

		moles = new ArrayList<MoleSprite>();


		gameLoopThread = new GameLoopThread(this);

		int id = 0;
		for(int x = 0; x<3; x++){
			for(int y = 0; y<4 ; y++){
				
				MoleSprite mole = new MoleSprite(this, x*WIDTH/3, HEIGHT/6+y*HEIGHT/6, FRONT);
				mole.setOnClickListener(this);
				mole.setId(id);
				id++;
				Log.i(tag, "Id: "+mole.getId());
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
				// TODO Auto-generated method stub

			}
		});

	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		for(MoleSprite mole : moles){
			mole.onDraw(canvas);
		}

	}

	@Override
	public void onClick(View v) { //TODO I don't like. Must be another way.
		Log.i(tag, "Click en "+v.getId());
		for(MoleSprite mole : moles){
			if(v.getId()==mole.getId()){
				Log.i(tag,"MoleSprite clicked "+mole.toString());
			}
		}

	}
}


