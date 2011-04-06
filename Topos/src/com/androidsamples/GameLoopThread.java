package com.androidsamples;



import java.util.List;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class GameLoopThread extends Thread {
	private static SoundManager missFx;
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelTimeDuration=5000;
	private boolean levelFinish;
	private boolean gameOver;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();
	private long levelTimeDigDown=1500;
	private Handler handler;
	private Integer lives;
	private Integer points;
	private Long time;
	private Double playVelocity=1.25;
	private CountDownTimer secondsTimer;


	public GameLoopThread(final ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.handler = txtHandler;
		setPoints(0);
		setLives(10);
		levelFinish = false;
		
		secondsTimer = doSecondsTimer();
		secondsTimer.start();
		
		

	}
	
	public void stopGame(){
		secondsTimer.cancel();
		setRunning(false);
	}
	

	public void setLives(int newlives){
		lives = newlives;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("lives", lives.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}

	public void setTime(long seconds){
		time = seconds;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("time", time.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}

	public void setPoints(Integer points){
		this.points = points;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("points", points.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}

	public void click(boolean clicked){
		if(clicked)
			setPoints(points+100);
	}

	public void setRunning(boolean run) {
		running = run;
	}
	
	private CountDownTimer doSecondsTimer(){
		 return new CountDownTimer(levelTimeDuration, 1000) {

			public void onTick(long millisUntilFinished) {
				setTime(millisUntilFinished / 1000);
			}

			public void onFinish() {
				view.reset();
				levelFinish= true;
				if(!gameOver)
				throwAlertFinalLevel();
			}
		};
	}

	@Override
	public void run() {

		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;

		while (running) {
			if(lives<=0 && !gameOver){
				view.reset();
				levelFinish= true;
				gameOver = true;
				gameOver();
			}
			else if(System.currentTimeMillis()-playLoopStartTime>playLoopTime && !levelFinish){
				play();
			}
			Canvas canvas = null;
			startTime = System.currentTimeMillis();

			List<MoleSprite> moles= view.getMoles();
			for(MoleSprite mole : moles){
				if(mole.getStatus()==MoleSprite.DIGUPFULL){					
					if(System.currentTimeMillis()-mole.getDigStartTime()>levelTimeDigDown){
						mole.digDown();
						view.startMissFx();
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

	private void gameOver() {
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("type", "gameover");
			data.putInt("level", level);
			data.putString("points", points.toString());
			m.setData(data);
			handler.sendMessage(m);
		}		
	}

	private void play(){
		List<MoleSprite> moles=view.getMoles();
		playLoopStartTime = System.currentTimeMillis();

		MoleSprite mole;
		do{
			int chosenMole = (int) Math.floor(Math.random()*moles.size());
			mole=moles.get(chosenMole);
		}while(mole.getStatus()!=MoleSprite.HOLE);
		//TODO Elegir tipo del topo (dificultad)
		mole.digUp();

	}


	public void startNextLevel(){
		level++;
		if(levelTimeDuration<120000){
			levelTimeDuration+=10000;
			time = levelTimeDuration;
			playLoopTime/=playVelocity;
		}else{
			levelTimeDigDown-=100;

		}
		secondsTimer = doSecondsTimer();
		secondsTimer.start();
		levelFinish=false;
	}

	public void throwAlertFinalLevel(){
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("type", "level");
			data.putInt("level", level);
			data.putString("points", points.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}



}

