package com.androidsamples;



import java.util.List;

import android.graphics.Canvas;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelStartTime=System.currentTimeMillis();
	private long levelTimeDuration=30000;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();


	public GameLoopThread(ToposGameView view) {
		this.view = view;
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
			play();
			Canvas canvas = null;
			startTime = System.currentTimeMillis();
			for(MoleSprite mole : view.getMoles()){
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

		if(System.currentTimeMillis()-playLoopStartTime>playLoopTime){	// la idea es k en los primeros niveles no tngamos k tocar el playLoopTime
													
			List<MoleSprite> moles=view.getMoles();

			if(System.currentTimeMillis()-levelStartTime>levelTimeDuration){
				
				level++;
				levelStartTime=System.currentTimeMillis();
				levelTimeDuration=levelTimeDuration+10000;
				playLoopTime=playLoopTime/2;
				
			}		
			
			if(level<=7){
				int chosenMole = (int) Math.floor(12*Math.random()-0.01);
				MoleSprite mole=moles.get(chosenMole);

				if(mole.getStatus()==4){
					mole.digUp();
				} else {
					playLoopStartTime=System.currentTimeMillis();
					play();
				}

			}

		}

	}




}
