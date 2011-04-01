package com.androidsamples;



import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.util.Log;
import android.widget.CheckBox;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelStartTime=System.currentTimeMillis();
	private long levelTimeDuration=30000;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();
	private Random rand;


	public GameLoopThread(ToposGameView view) {
		this.view = view;
		rand = new Random(System.nanoTime());
	}




	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {

		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;

		while (running) {
			if(System.currentTimeMillis()-playLoopStartTime>playLoopTime){
				Log.i("TAG", "Enters play. playLoopStartTime: "+playLoopStartTime);
				play();
			}
			Canvas canvas = null;
			startTime = System.currentTimeMillis();

			Iterator<MoleSprite> it= view.getMoles().iterator();
			MoleSprite mole;
			while(it.hasNext()){
				mole=it.next();
				if(mole.getStatus()==MoleSprite.DIGUPFULL){					
					if(System.currentTimeMillis()-mole.getDigStartTime()>1500){//TODO probar tiempo adecuado.
						mole.digDown();
					}
				}

				if(mole.isDigging() || mole.isHit())
					view.setRedraw(view.needRedraw() || true);
			}

			if(view.needRedraw()){
				try {

					canvas = view.getHolder().lockCanvas();
					synchronized (view.getHolder()) {
						view.onDraw(canvas);
					}

				} finally {
					if (canvas != null) {
						view.getHolder().unlockCanvasAndPost(canvas);
					}
				}
			}

			sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {}

		}
	}

	private void play(){
		List<MoleSprite> moles=view.getMoles();
		playLoopStartTime = System.currentTimeMillis();
		if(System.currentTimeMillis()-levelStartTime>levelTimeDuration){
			level++;
			levelStartTime=System.currentTimeMillis();
			levelTimeDuration=levelTimeDuration+10000;
			playLoopTime=playLoopTime/2;
		}		


		if(level<=7){//a partir del nivel 7, tardaran menos en bajarse, aun no implementado, en teoria con nivel 7 tendrian que salir 7 topos "casi" a la vez, pero aun hay que afinar valores.
			int chosenMole = new Random(System.nanoTime()).nextInt(moles.size());
			MoleSprite mole=moles.get(chosenMole);

			if(mole.getStatus()==MoleSprite.HOLE){
				mole.digUp();
			}
		}
	}




}

