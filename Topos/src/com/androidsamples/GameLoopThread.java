package com.androidsamples;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GameLoopThread extends Thread {
	private static final String tag = "TAG";

	private static final int BIGCLICKS = 3;

	private final long FPS = 13;
	private ToposGameView view;
	private boolean running = false;
	private int level;
	private long levelTimeDuration = 10000;
	private boolean levelFinish;
	private boolean gameOver;
	private long playLoopTime = 1000;
	private long playLoopStartTime;
	private long levelTimeDigDown = 1500;
	private long levelTimeBigDigDown = 4500;
	private Handler handler;
	private Integer lives;
	private Integer points;
	private Long time;
	private Double playVelocity = 1.20;
	private CountDownTimer secondsTimer;
	private int bigMolesCount;
	private int weaselCount;
	private boolean canSave;
	private boolean saved;
	private boolean timerStarted;
	private SoundManager sm;

	public GameLoopThread(final ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.handler = txtHandler;
		SharedPreferences sp = view.getContext().getSharedPreferences(
				topos.PREFS, Context.MODE_PRIVATE);
		saved = sp.getBoolean("saved", false);

		sm = new SoundManager(view.getContext());

		sm.startMusic();

		levelFinish = false;
		secondsTimer = doSecondsTimer();

		if (saved) {
			level = sp.getInt("level", 1);
			points = sp.getInt("points", 0);
			lives = sp.getInt("lives", 10);
			playLoopTime = sp.getLong("playLoopTime", 1000);
			synchronized (view.getHolder()) {
				Message m = handler.obtainMessage();
				Bundle data = new Bundle();
				data.putString("type", "saved");
				data.putInt("level", level);
				data.putString("lives", lives.toString());
				data.putString("points", points.toString());
				m.setData(data);
				handler.sendMessage(m);
			}
		} else {
			level = 1;
			setPoints(0);
			setLives(10);
			startNextLevel(true);
		}
	}

	public void stopGame() {
		sm.stopMusic();
		secondsTimer.cancel();
		boolean retry = true;
		setRunning(false);
		while (retry) {
			try {
				join();
				retry = false;
				Log.i(tag, "Join thread");
			} catch (InterruptedException i) {

			}
		}
	}

	public void setLives(int newlives) {
		lives = newlives;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("lives", lives.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}

	public void setTime(long seconds) {
		time = seconds;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("time", time.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}

	public void setPoints(Integer points) {
		this.points = points;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("points", points.toString());
			m.setData(data);
			handler.sendMessage(m);
		}
	}
	
	public void setLevel(Integer level) {
		this.level = level;
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putInt("level", level);
			m.setData(data);
			handler.sendMessage(m);
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
				setPoints(newpoints);
				mole.doHit();
			}
		} else if (mole.isWeasel()) {
			mole.doHit();
			newpoints -= 500;
			setPoints(newpoints);

		} else {
			mole.doHit();
			newpoints += (100 + 10 * (level - 1));
			setPoints(newpoints);
		}				
		sm.startHit();

	}

	public void setRunning(boolean run) {
		running = run;
	}

	private CountDownTimer doSecondsTimer() {
		return new CountDownTimer(levelTimeDuration, 1000) {

			public void onTick(long millisUntilFinished) {
				setTime(millisUntilFinished / 1000);
			}

			public void onFinish() {
				setTime(0);
				view.reset();
				levelFinish = true;
				canSave = true;
				timerStarted = false;
				if (!gameOver)
					throwAlertFinalLevel();
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
				canSave = false;
				levelFinish = true;
				gameOver = true;
				gameOver();
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
							int newlives = lives - 3;
							setLives(newlives);
						}
					} else if (mole.isWeasel()) {
						if (System.currentTimeMillis() - mole.getDigStartTime() > levelTimeBigDigDown) {
							mole.digDown();
						}
					} else {
						if (System.currentTimeMillis() - mole.getDigStartTime() > levelTimeDigDown) {
							mole.digDown();
							sm.startMiss();							
							setLives(--lives);
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

	private void gameOver() {
		synchronized (view.getHolder()) {
			Message m = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("type", "gameover");
			data.putInt("level", level);
			data.putInt("time", 0);
			data.putString("points", points.toString());
			m.setData(data);
			handler.sendMessage(m);
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
			setLevel(level+1);
			if(level>=7){
				levelTimeDigDown-=100;
			}
		}
		bigMolesCount = 0;
		setTime(levelTimeDuration/1000);
		playLoopTime/=playVelocity;
		levelFinish = false;
	}

	public void throwAlertFinalLevel() {

		sm.startEnding();
		sm.endingVibrate();
		try {
			Thread.sleep(600);
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

	public void saveGame() {
		SharedPreferences prefs = view.getContext().getSharedPreferences(
				topos.PREFS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (canSave) {
			Log.i(tag, "saving game");
			editor.putBoolean("saved", true);
			editor.putInt("level", level);
			editor.putInt("lives", lives);
			editor.putInt("points", points);
			editor.putLong("playLoopTime", playLoopTime);
		} else {
			editor.putBoolean("saved", false);
		}

		editor.commit();
	}

}
