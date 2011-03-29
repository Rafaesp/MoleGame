package com.androidsamples;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
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
	private static int gameviewWidth;
	private static int gameViewHeight;
	
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;

	private ArrayList<MoleSprite> moles;

	public ToposGameView(Context context){
		super(context);
		initToposGameView();
	}
	
	public ToposGameView(Context context, AttributeSet attrs){
		super(context, attrs);
		initToposGameView();
		
	}
	
	private void initToposGameView(){
		gameViewHeight = getHeight();
		gameviewWidth = getWidth();
		moles = new ArrayList<MoleSprite>();
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

				MoleSprite mole = new MoleSprite(this, x*getWidth()/3, y*getHeight()/4, MoleSprite.HOLE); // TODO el centrado de la pantalla estaria mal para otras resoluciones.
																											 // hay que saber cuales son las resoluciones mhdpi y ldpi
				moles.add(mole);
				Log.i(tag, mole.toString());
			}
		}
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		for(MoleSprite mole : moles){
		//	Log.i("onDraw","entra");
			mole.onDraw(canvas);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			for(MoleSprite mole : moles){
				if(mole.isClicked(event.getX(), event.getY())){
				
					mole.turnMole(MoleSprite.BEATEN);
					try {
						Thread.sleep(301); //TODO esto no esta bien hecho, pero hay que conseguir algo asi, hoy no estoy muy lucido.
					} catch (InterruptedException e) {
						throw new RuntimeException("Check onTouch(View,MotionEvent) in ToposGameView.class");
						
					}
					mole.turnMole(MoleSprite.HOLE);
				}
			}	
		}
		return false;
	}
	
//	public void play() throws InterruptedException{ TODO                BOCETO
//		long time=System.currentTimeMillis();
//		long actual=time;
//		int level=1;
//		while(true){
//			Thread.sleep(80);
//		for(int i=level-1;i<level;i++){
//			int indexMole =(int) (Math.random()*11);
//			MoleSprite mol=moles.get(indexMole);
//			mol.turnMole(MoleSprite.DIGUP1);
//			Thread.sleep(200);//TODO esto y todos los sleep es un boceto no podemos usar sleep.
//			mol.turnMole(MoleSprite.DIGUP2);
//			Thread.sleep(200);
//			//TODO Implementar: Restar una vida por haber fallado
//			actual=System.currentTimeMillis();
//		}
//			if(actual-time<level*30000){
//				level++;
//			}
//		}		
//	}



}