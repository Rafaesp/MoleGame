package com.androidsamples;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import android.graphics.Canvas;
import android.util.Log;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelStartTime=System.currentTimeMillis();
	private long levelTimeDuration=30000;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();

	

	//TODO Hay que reparar un error que se produce al parecer, cuando se borra un elemento de la cola:
	// Lanza FATAL ERROR: ConcurrentModificationException at next() LinkedList
	//Ver: //http://download.oracle.com/javase/1.4.2/docs/api/java/util/ConcurrentModificationException.html

	private Queue<MoleSprite> checkMoles=new LinkedList<MoleSprite>();


	public GameLoopThread(ToposGameView view) {
		this.view = view;
	}




	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {

		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;

		while (running) {
			play();
			Canvas canvas = null;
			startTime = System.currentTimeMillis();
			for(MoleSprite mole : view.getMoles()){
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

	private void play(){
		//Estas variables son para probar que no se cuelga, ya lo quitaremos si no salta nunca la excepcion de abajo.
		boolean aux1=true;
		boolean aux2=true;

		if(System.currentTimeMillis()-playLoopStartTime>playLoopTime){

			List<MoleSprite> moles=view.getMoles();

			if(System.currentTimeMillis()-levelStartTime>levelTimeDuration){

				level++;
				levelStartTime=System.currentTimeMillis();
				levelTimeDuration=levelTimeDuration+10000;
				playLoopTime=playLoopTime/2;

			}		


			if(level<=7){//a partir del nivel 7, tardaran menos en bajarse, aun no implementado, en teoria con nivel 7 tendrian que salir 7 topos "casi" a la vez, pero aun hay que afinar valores.
				int chosenMole = (int) Math.floor(12*Math.random()-0.01);
				MoleSprite mole=moles.get(chosenMole);

				if(mole.getStatus()==4){
					mole.digUp();
					aux1=checkMoles.add(mole);

					Log.i("El topo añandido a la cola es: ",mole.toString());
					mole.setFullDigUpStartTime(System.currentTimeMillis()); // tambien recoge el tiempo que tarda en subir, no solo arriba, no creo que sea un problema.

				}else{

					playLoopStartTime=System.currentTimeMillis();
					play();
				}

				
//				Iterator it = solicitudes.iterator();
//				while(it.hasNext()){
//				 if(!((SolicitudVO)it.next()).isSolutions())
//				  it.remove();
//				}	
				Iterator<MoleSprite> it= checkMoles.iterator();
				MoleSprite moleCheck;
				while(it.hasNext()){
					moleCheck=it.next();
					if(moleCheck.getStatus()==0){					
						if(System.currentTimeMillis()-moleCheck.getFullDigUpStartTime()>1500){//TODO probar tiempo adecuado

							if(moleCheck.equals((checkMoles).peek())){//TODO esta comprobacion es necesaria? habra alguna vez que no se añada bien o borre bien?


								moleCheck.digDown();

								it.remove(); //poll quitaba el elemento de la cola o eso era peek? al final uso remove porque devuelve boolean
								Log.i("El topo borrado de la cola es: ",moleCheck.toString());




							}

						}
					}

					
					
				}
//				for(MoleSprite moleCheck :checkMoles){
//					if(moleCheck.getStatus()==0){					
//						if(System.currentTimeMillis()-moleCheck.getFullDigUpStartTime()>1500){//TODO probar tiempo adecuado
//
//							if(moleCheck.equals((checkMoles).peek())){//TODO esta comprobacion es necesaria? habra alguna vez que no se añada bien o borre bien?
//
//
//								moleCheck.digDown();
//
//								aux2=checkMoles.remove(moleCheck); //poll quitaba el elemento de la cola o eso era peek? al final uso remove porque devuelve boolean
//								Log.i("El topo borrado de la cola es: ",moleCheck.toString());
//
//
//
//
//							}
//
//						}
//					}
//
//				}

			}

			if(!aux1 || !aux2) throw new IllegalArgumentException("Failure to add or remove a mole from the queue");

		}




	}
}
