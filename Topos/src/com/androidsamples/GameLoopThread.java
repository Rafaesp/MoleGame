package com.androidsamples;



import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameLoopThread extends Thread {
	private final long FPS=30;
	private ToposGameView view;
	private boolean running = false;
	private int level=1;
	private long levelStartTime=System.currentTimeMillis();
	private long levelTimeDuration=5000;
	private boolean levelFinish;
	private long playLoopTime=1000;
	private long playLoopStartTime=System.currentTimeMillis();
	private long levelTimeDigDown=1500;
	private Handler txtHandler;
	private Integer lives;
	private Integer points;
	private Integer time;
	private Double playVelocity=1.25;
	private CountDownTimer secondsTimer;
	private AlertDialog alertDialog;


	public GameLoopThread(final ToposGameView view, Handler txtHandler) {
		this.view = view;
		this.txtHandler = txtHandler;
		setPoints(0);
		setLives(50);
		levelFinish = false;
		secondsTimer = new CountDownTimer(levelTimeDuration, 1000) {

			public void onTick(long millisUntilFinished) {
				setTime(millisUntilFinished / 1000);
			}

			public void onFinish() {
				view.reset();
				levelFinish= true;
				throwAlertFinalLevel(level,levelTimeDuration);//He puesto la view como constante, si no no andaba =S
			}
		};

		secondsTimer.start();

	}

	public void setLives(int newlives){
		lives = newlives;
		synchronized (view.getHolder()) {
			Message m = txtHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("lives", lives.toString());
			m.setData(data);
			txtHandler.sendMessage(m);
		}
	}

	public void setTime(long seconds){
		time = new Integer((int) seconds);
		synchronized (view.getHolder()) {
			Message m = txtHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("time", time.toString());
			m.setData(data);
			txtHandler.sendMessage(m);
		}
	}

	public void setPoints(Integer points){
		this.points = points;
		synchronized (view.getHolder()) {
			Message m = txtHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("points", points.toString());
			m.setData(data);
			txtHandler.sendMessage(m);
		}
	}

	public void click(boolean clicked){
		if(clicked)
			setPoints(points+100);
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
			if(System.currentTimeMillis()-playLoopStartTime>playLoopTime && !levelFinish){
				play();
			}
			Canvas canvas = null;
			startTime = System.currentTimeMillis();

			List<MoleSprite> moles= view.getMoles();
			for(MoleSprite mole : moles){
				if(mole.getStatus()==MoleSprite.DIGUPFULL){					
					if(System.currentTimeMillis()-mole.getDigStartTime()>levelTimeDigDown){//TODO probar tiempo adecuado.
						mole.digDown();
						setLives(--lives);
					}
				}

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
		List<MoleSprite> moles=view.getMoles();
		playLoopStartTime = System.currentTimeMillis();

		MoleSprite mole;
		do{
			int chosenMole = (int) Math.floor(Math.random()*moles.size());
			mole=moles.get(chosenMole);
		}while(mole.getStatus()!=MoleSprite.HOLE);

		mole.digUp();

	}


	public void startNextLevel(){//Metodo ejecutado por el boton del la advertencia al final de nivel, reestablece los valores, para el siguiente nivel y lo ejecuta.
		// ¿Donde se le dice que siga redibujando de nuevo la barra superior tambien?
		if(alertDialog.isShowing())
			alertDialog.dismiss();
		level++;
		levelStartTime=System.currentTimeMillis();
		if(levelTimeDuration<120000){	//TODO 2 min, crear variable, aunque no creo que se vaya a modificar el valor mas de una vez;
			levelTimeDuration+=10000;
			setTime(time+10000);
			playLoopTime/=playVelocity;
		}else{
			//TODO levelTimeDigDown-=

		}
		secondsTimer.start();
		levelFinish=false;
		setRunning(true);
	}

	public void throwAlertFinalLevel(int level, long levelTimeDuration){//no se usa aun level y levelTimeDuration, no se como cambiar su valor si esta hecho en xml
		//TODO hacer un Alert "bonito" este es de pruebas
		AlertDialog.Builder builder;

		LayoutInflater inflater =(LayoutInflater)view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.levelview, null);

		TextView levelScore = (TextView) layout.findViewById(R.id.txtValueScore);
		levelScore.setText(points.toString());
		TextView txtLevel = (TextView) layout.findViewById(R.id.txtLevelX);
		txtLevel.setText("Level");

		builder = new AlertDialog.Builder(view.getContext());
		builder.setTitle(R.string.txtAlertDialogFinishedLevel);
		builder.setView(layout);

		builder.setPositiveButton(R.string.txtButtonNextLevel, new DialogInterface.OnClickListener() {//TODO boton positivo para seguir jugando y negativo para ir al menu
			public void onClick(DialogInterface dialog, int id) {	
				startNextLevel();
			}});
		builder.setNegativeButton(R.string.txtButtonMain, new DialogInterface.OnClickListener() {//TODO boton positivo para seguir jugando y negativo para ir al menu
			public void onClick(DialogInterface dialog, int id) {		        	   

			}});
		alertDialog = builder.create();
		alertDialog.show();

	}


}

