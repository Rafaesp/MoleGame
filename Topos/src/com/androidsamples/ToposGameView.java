package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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


	private List<Sprite> moles;


	public ToposGameView(Context context) {
		super(context);

		moles = new ArrayList<Sprite>();

		gameLoopThread = new GameLoopThread(this);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bad1);

		setOnClickListener(this);// TODO

		for(int i = 0; i<4; i++){
			moles.add(new Sprite(this, this.bmp, WIDTH/4, (2*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (2*WIDTH)/4, (2*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (3*WIDTH)/4, (2*HEIGHT)/8));
			
			moles.add(new Sprite(this, this.bmp, WIDTH/4, (3*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (2*WIDTH)/4, (3*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (3*WIDTH)/4, (3*HEIGHT)/8));
			
			moles.add(new Sprite(this, this.bmp, WIDTH/4, (4*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (2*WIDTH)/4, (4*HEIGHT)/8));
			moles.add(new Sprite(this, this.bmp, (3*WIDTH)/4, (4*HEIGHT)/8));
		}

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
		for(Sprite mole : moles){
			mole.onDraw(canvas);
		}

	}

	@Override
	public void onClick(View v) { //TODO I don't like. Must be another way.
		

			//TODO No he podido hacer mas pruebas, esto se deberia hacer con un swich case, pero lo que pasa 
			// es que al no estar nombrados en algun XML los sprites, no tienen un id en R, entonces el problema que esto causa es
			// que no sabemos con que atributo compararlo es decir con if(v.getId()==clicked.getId()) todos entran en el if
			// y haciendo un casting como aparece ahora no entra ninguna vez, no se como identificar la View en la que se ha hecho click
			for(Sprite clicked : moles){
				if(v.equals((View) clicked) ){
					
					Log.i("onClick","Debe aparecer una vez");
					clicked.setBeating(true); //TODO
											  //ya se que este metodo para hacer click en un solo elemento es muy "guarro", pero no debe estar muy
											  // lejos de lo real, que seguro que es un onDraw(en Sprite) con distintos casos, parecido a cuando trabajamos
											  // con botones.
					clicked.setBeaten();
					break;
				}
			}
			
	
		
		

	}
}


