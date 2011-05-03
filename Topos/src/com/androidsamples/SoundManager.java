package com.androidsamples;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;


public class SoundManager {

	private Context context;
	private SoundPool spool;
	private Integer fxMiss;
	private Integer fxHit;
	private MediaPlayer mpMusic;
	private MediaPlayer mpEnding;
	private Vibrator vibrator;
	private boolean vibrationEnabled;
	private boolean endingEnabled;
	private boolean endingVibration;
	private boolean musicEnabled;
	private boolean hitEnabled;
	private boolean missEnabled;



	public SoundManager(Context con){
		context=con;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(
				context);
		vibrationEnabled = sp.getBoolean("VibrationPref", true);
		endingEnabled = sp.getBoolean("EndingPref", true);
		endingVibration  = sp.getBoolean("EndingVibrationPref", true);
		musicEnabled = sp.getBoolean("MusicPref", true);
		hitEnabled = sp.getBoolean("HitPref", true);
		missEnabled = sp.getBoolean("MissPref", true);

		if(endingVibration)
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		if(hitEnabled || missEnabled){
			spool=new SoundPool(6, AudioManager.STREAM_MUSIC, 0);	

			if(hitEnabled)
				fxHit = spool.load(context, R.raw.punch, 1);

		}if(missEnabled){
			fxMiss=spool.load(context, R.raw.laugh01, 1);

		}if(musicEnabled){
			mpMusic=MediaPlayer.create(context, R.raw.bgmusic);

		}if(endingEnabled){
			mpEnding = MediaPlayer.create(context, R.raw.finish);
		}
	}

	public void startHit(){
		if(hitEnabled)
			spool.play(fxHit, 1.0f, 1.0f, 0, 0, 1.5f);
		if (vibrationEnabled)
			vibrator.vibrate(40);

	}
	public void startMiss(){
		if(missEnabled)
			spool.play(fxMiss, 1.0f, 1.0f, 0, 0, 1.5f);
	}
	public void startMusic(){
		if(musicEnabled)
			mpMusic=MediaPlayer.create(context, R.raw.bgmusic); //TODO con esta linea se arregla el bug de que ma musica solo sonaba en el primer
																// nivel, pero no se asta que punto esto es optimo.
			mpMusic.start();
	}

	public void startEnding(){
		if(endingEnabled)
			mpEnding.start();
	}

	public void endingVibrate(){
		if(vibrationEnabled){
			vibrator.vibrate(300);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			vibrator.vibrate(300);
		}
	}

	public void stopMusic(){
		if(mpMusic != null && mpMusic.isPlaying()){
			mpMusic.stop();
		}
	}

	public void release(){
		if(musicEnabled)
			mpMusic.release();
		if(endingEnabled)
			mpEnding.release();
		if(hitEnabled || missEnabled)
			spool.release();

		mpMusic = null;
		mpEnding = null;
		spool = null;
	}


}
