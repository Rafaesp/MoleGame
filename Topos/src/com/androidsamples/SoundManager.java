package com.androidsamples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;


public class SoundManager {//NO FUNCIONA BIEN PERO COMPILA Y NO SE USA FUERA
	
	
	 private List<MediaPlayer> listLoops;
	 private Context context;
	 private String type;
	 private SoundPool current;
	 private int currentInt;
	 private List<Integer> listHitFX;
	 
	 public SoundManager(String idSound,Context con){//idSound=loops idSound=music1
		 this.type=idSound;
		 context=con;		 	 
		 if(idSound.equals("hitFx")){
			 Log.i("Entra", "en hitFx");
			listHitFX= new ArrayList<Integer>();
			current=new SoundPool(6, AudioManager.STREAM_MUSIC, 0);	
			listHitFX.add(R.raw.hit01);
			listHitFX.add(R.raw.hit02);
			listHitFX.add(R.raw.hit03);
			listHitFX.add(R.raw.hit04);
			listHitFX.add(R.raw.hit05);
			
		 }
	 }
	 
	 
	 public void start(){
		 //int i1=(int) (listHitFX.size()*Math.random());
		
		 //int i=listHitFX.get(i1);
		int i = listHitFX.get(0);
		 currentInt=current.load(context, R.raw.hit01, 1);
		 Log.i("currentINT", currentInt+"");
		 current.play(currentInt, 1.0f, 1.0f, 0, 0, 1.5f);
	 }
	 
	 public boolean isPlaying(){
		 return false;//NADA
	 }
	 
	 public void stop(){
		 	 
	 }
	 
}
