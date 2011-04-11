package com.androidsamples;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;


public class SoundManager {
	
	private static final String HITFX="hitFx";
	private static final String MUSIC1FX="music1Fx";
	private static final String MISSFX="missFx";
	 private Context context;
	 private SoundPool current;
	 private List<Integer> listHitFX;
	 private String type;
	 private Integer fxInt;
	 private MediaPlayer mpMusci1;
	
	 
	 public SoundManager(String idSound,Context con){//idSound=ver constantes
		 type=idSound;
		 context=con;	
		 current=new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
		 if(idSound.equals(HITFX)){
			 listHitFX= new ArrayList<Integer>();	 		
			 listHitFX.add(current.load(context, R.raw.hit01, 1));
			// listHitFX.add(current.load(context, R.raw.hit02, 1)); hit02 es el que estaba mal
			 listHitFX.add(current.load(context, R.raw.hit03, 1));
			 listHitFX.add(current.load(context, R.raw.hit04, 1));
			 listHitFX.add(current.load(context, R.raw.hit05, 1));
		 }else if(type.equals(MISSFX)){
			 
			 fxInt=current.load(context, R.raw.laugh01, 1);//TODO recurso de prueba
			 
		 }else if(type.equals(MUSIC1FX)){
			
			 mpMusci1=MediaPlayer.create(context, R.raw.music1_2);
			 mpMusci1.setLooping(true);
		 }
	 }

	 public void start(){//Lo dejamos asi, o hacemos distintos starts? startHiFx startMusic1Fx startMissFx o hacer un switch case
		 if(type.equals(HITFX)){
			 current.play(listHitFX.get((int) (listHitFX.size()*Math.random())), 1.0f, 1.0f, 0, 0, 1.5f);	 
		 }else if(type.equals(MISSFX)){
			 current.play(fxInt, 1.0f, 1.0f, 0, 0, 1.5f);	 
		 }else if(type.equals(MUSIC1FX)){
			 mpMusci1.start();
		 }
		
	 }


	public String getType() {
		return type;
	}
	
	public boolean isPlaying(){
		if(type.equals(MUSIC1FX)){
			return mpMusci1.isPlaying();
		}
		throw new RuntimeException("Check isPlaying");
	}
	
	public void stop(){
		if(type.equals(MUSIC1FX)){
			mpMusci1.stop();
		}else{
		throw new RuntimeException("Check stop");
		}
	}
	 
public Context getContext(){
	return context;
}
	 
}
