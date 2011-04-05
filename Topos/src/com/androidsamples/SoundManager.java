package com.androidsamples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;


public class SoundManager {
	
	 private MediaPlayer mp;
	 private List<Integer> listLoop;
	 private Context context;
	 private String type;
	 
	 public SoundManager(String idSound,Context con){//idSound=loops idSound=music1
		 this.type=idSound;
		 if(idSound.equals(type)){
			 context=con;
			 listLoop= new LinkedList<Integer>();
			 listLoop.add(R.raw.hit01);
			 listLoop.add(R.raw.hit02);
			 mp= MediaPlayer.create(context,R.raw.hit01);	//Aun posible null pointer exception 
		 }
	 }
	 
	 public void start(){
		 if(type.equals("loops")){
			 
			 int i=listLoop.get((int) (2*Math.random()));
			 		 
			// mp.setDataSource(context, i); // Esta mal hecho, pero lo dejo asi para que se vea la idea, aun solo hay dos tipos de golpes;
		 }
		 mp.start();
	 }
	
}
