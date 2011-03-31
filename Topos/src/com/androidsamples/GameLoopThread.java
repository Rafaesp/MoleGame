package com.androidsamples;


import android.graphics.Canvas;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;



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

	//	private void play(){
	//		ArrayList<MoleSprite> moles=view.getMoles();
	//		int chosenMole = (int) Math.floor(12*Math.random()-0.01);
	//		MoleSprite mole=moles.get(chosenMole);
	//		if(mole.getStatus()==0){
	//			mole.digUp();
	//		}
	//		
	//	}




}
