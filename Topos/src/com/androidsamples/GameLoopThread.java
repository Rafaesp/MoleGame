package com.androidsamples;



import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

	private Queue<MoleSprite> checkMoles= new LinkedList<MoleSprite>();


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

		if(System.currentTimeMillis()-playLoopStartTime>playLoopTime){

			List<MoleSprite> moles=view.getMoles();

			if(System.currentTimeMillis()-levelStartTime>levelTimeDuration){

				level++;
				levelStartTime=System.currentTimeMillis();
				levelTimeDuration=levelTimeDuration+10000;
				playLoopTime=playLoopTime/2;

			}		

			if(level<=7){//a partir del nivel 7, tardaran menos en bajarse, aun no implementado, en teoria con nivel 7 tendrian que salir 7 topos "casi" a la vez.
				int chosenMole = (int) Math.floor(12*Math.random()-0.01);
				MoleSprite mole=moles.get(chosenMole);

				if(mole.getStatus()==4){
					mole.digUp();
					checkMoles.add(mole);
					mole.setFullDigUpStartTime(System.currentTimeMillis()); // tambien recoje el tiempo que tarda en subir, no solo arriba, no creo que sea un problema.

				}else{

					playLoopStartTime=System.currentTimeMillis();
					play();
				}

				for(MoleSprite moleCheck :checkMoles){
					if(moleCheck.getStatus()==0){					
						if(System.currentTimeMillis()-moleCheck.getFullDigUpStartTime()>1500){//TODO probar tiempo adecuado
							moleCheck.digDown();
						}
					}

				}

			}

		}




	}
}
