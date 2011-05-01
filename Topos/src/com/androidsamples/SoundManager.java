package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;


public class SoundManager {

	private Context context;
	private SoundPool current;
	private List<Integer> listHitFX;
	private String type;
	private Integer fxInt;
	private MediaPlayer mpMusic;
	private MediaPlayer mpEnding;
	private MediaPlayer mpMiss;
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

		if(hitEnabled){
			current=new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
			listHitFX= new ArrayList<Integer>();	 		
			listHitFX.add(current.load(context, R.raw.hit01, 1));
			// listHitFX.add(current.load(context, R.raw.hit02, 1)); hit02 es el que estaba mal
			listHitFX.add(current.load(context, R.raw.hit03, 1));
			listHitFX.add(current.load(context, R.raw.hit04, 1));
			listHitFX.add(current.load(context, R.raw.hit05, 1));
		
		}if(missEnabled){
			mpMiss = MediaPlayer.create(context, R.raw.laugh01);

		}if(musicEnabled){
			mpMusic=MediaPlayer.create(context, R.raw.music1);
			mpMusic.setLooping(true);

		}if(endingEnabled){
			mpEnding = MediaPlayer.create(context, R.raw.finish);
		}
	}

	public void startHit(){
		if(hitEnabled){
			current.play(listHitFX.get((int) (listHitFX.size()*Math.random())), 1.0f, 1.0f, 0, 0, 1.5f);
			if (vibrationEnabled)
				vibrator.vibrate(40);
		}
	}
	public void startMiss(){
		if(missEnabled)
			mpMiss.start();
	}
	public void startMusic(){
		if(musicEnabled)
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


	public String getType() {
		return type;
	}

	public void stopMusic(){
		if(mpMusic != null && mpMusic.isPlaying()){
			mpMusic.stop();
		}
	}


}
