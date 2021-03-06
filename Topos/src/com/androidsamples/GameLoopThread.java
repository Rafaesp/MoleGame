package com.androidsamples;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class GameLoopThread extends Thread {

	private static final int BIGCLICKS = 3;

	private final long FPS = 13;
	private ToposGameView view;
	private boolean running = false;
	private int level;
	private long levelTimeDuration = 30000;
	private boolean levelFinish;
	private boolean gameOver;
	private long playLoopTime = 750;
	private long playLoopStartTime;
	private long levelTimeDigDown = 1500;
	private long levelTimeBigDigDown = 4500;
	private Handler handler;
	private Integer lives;
	private Integer points;
	private Long time;
	private Double playVelocity;
	private CountDownTimer secondsTimer;
	private int bigMolesCount;
	private int weaselCount;
	private boolean canSave;
	private boolean saved;
	private boolean timerStarted;
	private SoundManager sm;
	private Message msg;
	private Bundle data;
	private boolean kidMode;
	private SharedPreferences.Editor editor;

	public GameLoopThread(final ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.handler = txtHandler;
		SharedPreferences sp = view.getContext().getSharedPreferences(
				topos.PREFS, Context.MODE_PRIVATE);
		editor = sp.edit();
		saved = sp.getBoolean("saved", false);
		
		kidMode = PreferenceManager.getDefaultSharedPreferences(
					view.getContext()).getBoolean("KidPref", false);
	
			if(kidMode){
				levelTimeDigDown+=1000;
				playLoopTime+=1000;
				levelTimeDuration -= 10000;
				topos.tracker.trackEvent("Evento", "Preferencia", "kidMode", 1);
			}else{
				topos.tracker.trackEvent("Evento", "Preferencia", "kidMode", 0);
			}
		

		sm = new SoundManager(view.getContext());

		data = new Bundle();

		levelFinish = false;
		secondsTimer = doSecondsTimer();

		if (saved) {
			level = sp.getInt("level", 1);
			points = sp.getInt("points", 0);
			lives = sp.getInt("lives", 10);
			time = levelTimeDuration/1000;
			playLoopTime = sp.getLong("playLoopTime", 1000);	
			updateInfoBar("saved");
		}else{
			level = 1;
			points = 0;
			lives = 10;
			startNextLevel(true);
			updateInfoBar("");
		}
	}

	public void stopGame() {
		sm.stopMusic();
		sm.release();
		secondsTimer.cancel();
		boolean retry = true;
		setRunning(false);
		while (retry) {
			try {
				join();
				retry = false;
			} catch (InterruptedException i) {

			}
		}
	}

	public void updateInfoBar(String type){
		synchronized (view.getHolder()) {
			if(handler.hasMessages(0))
				handler.removeMessages(0);
			data.putString("type", type);
			data.putString("time", time.toString());
			data.putInt("level", level);
			data.putString("lives", lives.toString());
			data.putString("points", points.toString());
			if(type == "gameover")
			data.putBoolean("kidMode", kidMode);
			
			msg = handler.obtainMessage(0);
			msg.setData(data);
			handler.sendMessage(msg);
		}		
	}


	public void click(MoleSprite mole) {
		int newpoints = points;
		if (mole.isBig()) {
			if (mole.getBigClicks() < BIGCLICKS - 1)
				mole.addBigClick();
			else {
				mole.resetBigClicks();
				newpoints += (300 + 10 * (level - 1));
				points = newpoints;
				updateInfoBar("");
				mole.doHit();
			}
		} else if (mole.isWeasel()) {
			mole.doHit();
			newpoints -= 500;
			points = newpoints;
			updateInfoBar("");

		} else {
			mole.doHit();
			newpoints += (100 + 10 * (level - 1));
			points = newpoints;
			updateInfoBar("");
		}				
		sm.startHit();

	}

	public void setRunning(boolean run) {
		running = run;
	}

	private CountDownTimer doSecondsTimer() {
		return new CountDownTimer(levelTimeDuration, 1000) {

			public void onTick(long millisUntilFinished) {
				time = millisUntilFinished / 1000;
				updateInfoBar("");
			}

			public void onFinish() {
				time = 0l;
				updateInfoBar("");
				view.reset();
				levelFinish = true;
				timerStarted = false;
				sm.pauseMusic();
				if (!gameOver){
					canSave = true;
					throwAlertFinalLevel();
				}
			}
		};
	}

	@Override
	public void run() {

		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;

		playLoopStartTime = System.currentTimeMillis();

		while (running) {
			Canvas canvas = null;
			startTime = System.currentTimeMillis();
			if (lives <= 0 && !gameOver) {
				view.reset();
				levelFinish = true;
				gameOver = true;
				sm.pauseMusic();
				updateInfoBar("gameover");
				topos.tracker.trackEvent("Evento", "Game", "level", level);
			} else if(System.currentTimeMillis() - playLoopStartTime > playLoopTime
					&& !levelFinish) {
				play();
			}


			List<MoleSprite> moles = view.getMoles();
			for (MoleSprite mole : moles) {
				if (mole.getStatus() == MoleSprite.DIGUPFULL) {
					if (mole.isBig()) {
						if (System.currentTimeMillis() - mole.getDigStartTime() > levelTimeBigDigDown) {
							mole.digDown();
							sm.startMiss();
							lives = lives - 3;
							updateInfoBar("");
						}
					} else if (mole.isWeasel()) {
						if (System.currentTimeMillis() - mole.getDigStartTime() > levelTimeDigDown) {
							mole.digDown();
						}
					} else {
						if (System.currentTimeMillis() - mole.getDigStartTime() > levelTimeDigDown) {
							mole.digDown();
							sm.startMiss();							
							--lives;
							updateInfoBar("");
						}
					}

				}

				if (mole.isDigging() || mole.isHit())
					view.setRedraw(view.needRedraw() || true);
			}
			if (view.needRedraw()) {
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

			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {
			}

		}

	}

	private void play() {
		List<MoleSprite> moles = view.getMoles();
		playLoopStartTime = System.currentTimeMillis();

		MoleSprite mole;
		do {
			int chosenMole = (int) Math.floor(Math.random() * moles.size());
			mole = moles.get(chosenMole);
		} while (mole.getStatus() != MoleSprite.HOLE);

		if (bigMolesCount < level) {
			if (Math.random() * 100 >= 98.0) {
				mole.setBig();
				bigMolesCount++;
			}
		}
		if (!mole.isBig() && weaselCount < level) {
			if (Math.random() * 100 >= 98.0) {
				mole.setWeasel();
				weaselCount++;
			}
		}

		mole.digUp();

		if(!timerStarted){
			secondsTimer.start();
			timerStarted = true;
		}
	}

	public void startNextLevel(boolean start){
		if(!start){
			canSave = false;
			secondsTimer.cancel();
			level++;
		}else{
		}
		

		if(level==6)
			levelTimeDigDown-=50;
		if(level==8)
			levelTimeDigDown-=60;
		if(level==10)
			levelTimeDigDown-=70;
		
		if(level>=5)
			playVelocity = 1.1;
		else
			playVelocity = 1.2;

		playLoopTime/=playVelocity;
		bigMolesCount = 0;
		time = levelTimeDuration/1000;
		levelFinish = false;
		sm.startMusic();
		updateInfoBar("");
	}

	public void throwAlertFinalLevel() {

		sm.startEnding();
		sm.endingVibrate();
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateInfoBar("level");
	}

	public void saveGame() {
		SharedPreferences prefs = view.getContext().getSharedPreferences(
				topos.PREFS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (canSave) {
			topos.tracker.trackEvent("Evento", "Preferencia", "saved", 1);
			editor.putBoolean("saved", true);
			editor.putInt("level", level);
			editor.putInt("lives", lives);
			editor.putInt("points", points);
			editor.putLong("playLoopTime", playLoopTime);
		} else {
			editor.putBoolean("saved", false);
		}
		editor.putBoolean("kidMode", kidMode);
		editor.commit();
	}



}
