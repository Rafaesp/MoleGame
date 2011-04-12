package com.androidsamples;



import java.util.List;

import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GameLoopThread extends Thread {
	private static final int BIGCLICKS = 3;
	
	private static SoundManager missFx;
	private final long FPS=13;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelTimeDuration=30000;
	private boolean levelFinish;
	private boolean gameOver;
	private long playLoopTime=1000;
	private long playLoopStartTime;
	private long levelTimeDigDown=1500;
	private long levelTimeBigDigDown = 4500;
	private Handler handler;
	private Integer lives;
	private Integer points;
	private Long time;
	private Double playVelocity=1.20;
	private CountDownTimer secondsTimer;
	private int bigMolesCount;



	public GameLoopThread(final ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.handler = txtHandler;
		setPoints(0);
		setLives(10);
		levelFinish = false;

		secondsTimer = doSecondsTimer();
	}

	public void stopGame(){
		view.stopMusic1Fx();
		secondsTimer.cancel();
		setRunning(false);
		boolean retry=true;
		setRunning(false);
		while(retry){
			try{
				join();
				retry=false;

			}catch(InterruptedException i){

			}
		}
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

	public void click(MoleSprite mole){
		if(mole.isBig()){
			if(mole.getBigClicks() < BIGCLICKS-1)
				mole.addBigClick();
			else{
				mole.resetBigClicks();
				setPoints(points+300);
				mole.digDown(); //TODO hit()
			}
				
		}else
			mole.digDown(); //TODO hit()
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

		playLoopStartTime=System.currentTimeMillis();
		secondsTimer.start();
		
		while (running) {
			Canvas canvas = null;
			startTime = System.currentTimeMillis();
			if(lives<=0 && !gameOver){
				view.reset();
				levelFinish= true;
				gameOver = true;
				gameOver();
			}
			else if(System.currentTimeMillis()-playLoopStartTime>playLoopTime && !levelFinish){
				play();
			}			

			List<MoleSprite> moles= view.getMoles();
			for(MoleSprite mole : moles){
				if(mole.getStatus()==MoleSprite.DIGUPFULL){		
					if(mole.isBig()){
						if(System.currentTimeMillis()-mole.getDigStartTime()>levelTimeBigDigDown){
							mole.digDown();
							view.startMissFx();
							int newlives = lives-3;
							setLives(newlives);
						}
					}else{
						if(System.currentTimeMillis()-mole.getDigStartTime()>levelTimeDigDown){
							mole.digDown();
							view.startMissFx();
							setLives(--lives);
						}
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

		if(bigMolesCount < level){
			if(Math.random()*100>=98.0){
				mole.setBig();
				bigMolesCount++;
			}
		}
		
		mole.digUp();

	}


	public void startNextLevel(){
		level++;
		if(level>=7){
			levelTimeDigDown-=100;
		}
		bigMolesCount = 0;
		time = levelTimeDuration;
		playLoopTime/=playVelocity;
		secondsTimer = doSecondsTimer();
		secondsTimer.start();
		levelFinish=false;
	}

	public void throwAlertFinalLevel(){
	
			try {// Yo lo veo bien aqui pero si quereis puedo meterlo en SoundManager
				MediaPlayer mp=MediaPlayer.create(view.getContext(),R.raw.finish);
				if(view.getStatusMissFx()){// el aviso sonoro de finish que hacemos lo activamos cuando se activa missFx como esta ahora, o aparte?
				mp.start();
				}
				view.startFinishVibrator();
				Thread.sleep(1000);
				if(mp.isPlaying())
					mp.stop();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
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

