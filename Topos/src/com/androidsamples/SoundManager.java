package com.androidsamples;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundManager {
	
	private static final String HITFX="hitFx";
	private static final String MUSIC1FX="music1";
	private static final String MISSFX="missFx";
	 private Context context;
	 private SoundPool current;
	 private List<Integer> listHitFX;
	 private String type;
	 private Integer missFxInt;
	
	 
	 public SoundManager(String idSound,Context con){//idSound=ver constantes
		 type=idSound;
		 context=con;	
		 current=new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
		 if(idSound.equals(HITFX)){
			 listHitFX= new ArrayList<Integer>();	 		
			 listHitFX.add(current.load(context, R.raw.hit01, 1));
			 listHitFX.add(current.load(context, R.raw.hit02, 1));
			 listHitFX.add(current.load(context, R.raw.hit03, 1));
			 listHitFX.add(current.load(context, R.raw.hit04, 1));
			 listHitFX.add(current.load(context, R.raw.hit05, 1));
		 }else if(type.equals(MISSFX)){
			 
			 missFxInt=current.load(context, R.raw.laugh01, 1);//TODO recurso de prueba
			 
		 }else if(type.equals(MUSIC1FX)){
			 
		 }
	 }

	 public void start(){//Lo dejamos asi, o hacemos distintos starts? startHiFx startMusic1Fx startMissFx o hacer un switch case
		 if(type.equals(HITFX)){
			 current.play(listHitFX.get((int) (listHitFX.size()*Math.random())), 1.0f, 1.0f, 0, 0, 1.5f);	 
		 }else if(type.equals(MISSFX)){
			 current.play(missFxInt, 1.0f, 1.0f, 0, 0, 1.5f);	 
		 }else if(type.equals(MUSIC1FX)){
			 
		 }
		
	 }


	public String getType() {
		return type;
	}
	 

	 
}
