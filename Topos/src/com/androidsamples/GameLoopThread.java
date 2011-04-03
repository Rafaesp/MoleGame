package com.androidsamples;



import java.util.List;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelStartTime=System.currentTimeMillis();
	private long levelTimeDuration=30000;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();
	
	private Handler txtHandler;
	private Integer lives;


	public GameLoopThread(ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.txtHandler = txtHandler;
		setLives(50);
	}

	public void setLives(int newlives){
		lives = newlives;
		synchronized (view.getHolder()) {
			Message m = txtHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("lives", lives.toString());
			m.setData(data);
			txtHandler.sendMessage(m);
		}
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
				play();
			}
			Canvas canvas = null;
			startTime = System.currentTimeMillis();

			List<MoleSprite> moles= view.getMoles();
			for(MoleSprite mole : moles){
				if(mole.getStatus()==MoleSprite.DIGUPFULL){					
					if(System.currentTimeMillis()-mole.getDigStartTime()>1500){//TODO probar tiempo adecuado.
						mole.digDown();
						setLives(--lives);
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
			//TODO Change level. Intent 
			levelStartTime=System.currentTimeMillis();
			levelTimeDuration+=10000;
			playLoopTime/=2;

		}

		if(level<=7){//a partir del nivel 7, tardaran menos en bajarse, aun no implementado, en teoria con nivel 7 tendrian que salir 7 topos "casi" a la vez, pero aun hay que afinar valores.
			MoleSprite mole;
			do{
				int chosenMole = (int) Math.floor(Math.random()*moles.size());
				mole=moles.get(chosenMole);
			}while(mole.getStatus()!=MoleSprite.HOLE);

			mole.digUp();

		}
	}




}

