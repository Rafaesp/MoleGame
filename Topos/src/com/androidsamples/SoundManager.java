package com.androidsamples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;


public class SoundManager {//NO FUNCIONA BIEN PERO COMPILA Y NO SE USA FUERA
	
	
	 private List<MediaPlayer> listLoops;
	 private Context context;
	 private String type;
	 private MediaPlayer music1;
	 private MediaPlayer actual;
	 
	 public SoundManager(String idSound,Context con){//idSound=loops idSound=music1
		 this.type=idSound;
		 if(idSound.equals(type)){
			 context=con;
			 listLoops= new LinkedList<MediaPlayer>();
			 listLoops.add(MediaPlayer.create(context,R.raw.hit01));
			 listLoops.add(MediaPlayer.create(context,R.raw.hit02));
			 listLoops.add(MediaPlayer.create(context,R.raw.hit03));
			 listLoops.add(MediaPlayer.create(context,R.raw.hit04));
			 listLoops.add(MediaPlayer.create(context,R.raw.hit05));
		 }else if(idSound.equals(type)){
			// actual =MediaPlayer.create(context,R.raw.music1); // TODO no tenemos el recurso aun.
		 }
	 }
	 
	 public void start(){
		 
		 if(type.equals("loops")){
			actual=listLoops.get((int) (5*Math.random()));//puede saltar fuera del indice, pero asi comprobamos que el ramdom va bien porque nose si puede salir 5 y la lista tiene indice 4	 			
			actual.start();
		 }else if(type.equals("music1")){
			// actual.start();
		 }
		 actual.start();
	 }
	 
	 public boolean isPlaying(){
		 return actual.isPlaying();
	 }
	 
	 public void stop(){
		 actual.stop();		 
	 }
	 
}
