package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ToposGameView extends SurfaceView implements OnTouchListener{

	private static final String tag = "TAG";

	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private List<MoleSprite> moles;
	private boolean needRedraw;
	private TextView livesTxtView;
	private TextView pointsTxtView;
	private TextView timeTxtView;
	private AlertDialog alertDialog;



	public ToposGameView(Context context){
		super(context);
		initToposGameView();
	}

	public ToposGameView(Context context, AttributeSet attrs){
		super(context, attrs);
		initToposGameView();

	}

	private void initToposGameView(){
		moles = new ArrayList<MoleSprite>();
		needRedraw = true;
		setFocusable(true);
		setOnTouchListener(this);

		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message m) {
				Bundle b = m.getData();
				if(b.getString("lives") != null)
					livesTxtView.setText(m.getData().getString("lives"));
				 if(b.getString("points") != null)
					pointsTxtView.setText(b.getString("points"));
				 if(b.getString("time") != null)
					timeTxtView.setText(b.getString("time"));
				 if(b.getString("type") != null){
					AlertDialog.Builder builder;

					LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.levelview, null);

					TextView levelScore = (TextView) layout.findViewById(R.id.txtValueScore);
					levelScore.setText(m.getData().getString("points"));
					TextView txtLevel = (TextView) layout.findViewById(R.id.txtLevelX);
					txtLevel.setText("Level "+m.getData().getInt("level"));

					builder = new AlertDialog.Builder(getContext());
					builder.setView(layout);
					if(m.getData().getString("type") == "gameover"){
						builder.setTitle(R.string.txtAlertDialogGameOver);
						builder.setPositiveButton(R.string.submitScore, new DialogInterface.OnClickListener() { //TODO Enviar puntuacion
							public void onClick(DialogInterface dialog, int id) {	

							}});
						builder.setNegativeButton(R.string.txtButtonMain, new DialogInterface.OnClickListener() {//TODO Volver menu
							public void onClick(DialogInterface dialog, int id) {		        	   

							}});
					}else{
						builder.setTitle(R.string.txtAlertDialogFinishedLevel);
						builder.setPositiveButton(R.string.txtButtonNextLevel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {	
								alertDialog.dismiss();
								gameLoopThread.startNextLevel();
							}});
						builder.setNegativeButton(R.string.txtButtonMain, new DialogInterface.OnClickListener() {//TODO negativo para guardar partida y volver menu
							public void onClick(DialogInterface dialog, int id) {		        	   

							}});
					}
					alertDialog = builder.create();
					alertDialog.show();
				}
			}
		};

		gameLoopThread = new GameLoopThread(this, handler);

		holder = getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				boolean retry=true;
				gameLoopThread.setRunning(false);
				while(retry){
					try{
						gameLoopThread.join();
						retry=false;

					}catch(InterruptedException i){

					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				Log.i(tag, "GameView width: "+getWidth()+" GameView height: "+getHeight());
				createMoles();
				gameLoopThread.setRunning(true);
				gameLoopThread.start();

			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {

			}
		});

	}

	private void createMoles(){
		for(int x = 0; x<3; x++){ 
			for(int y = 0; y<4 ; y++){
				MoleSprite mole = new MoleSprite(this, x, y);
				moles.add(mole);
			}
		}
		Log.i(tag, "Moles created");
	}

	public List<MoleSprite> getMoles(){
		return moles;
	}

	public void setLivesTxtView(TextView txtView) {
		this.livesTxtView = txtView;
	}

	public TextView getLivesTxtView() {
		return livesTxtView;
	}

	public TextView getPointsTxtView() {
		return pointsTxtView;
	}

	public void setPointsTxtView(TextView pointsTxtView) {
		this.pointsTxtView = pointsTxtView;
	}

	public TextView getTimeTxtView() {
		return timeTxtView;
	}

	public void setTimeTxtView(TextView timeTxtView) {
		this.timeTxtView = timeTxtView;
	}

	public boolean needRedraw(){
		return needRedraw;
	}

	public void setRedraw(boolean need){
		needRedraw = need;
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		needRedraw = false;
		for(MoleSprite mole : moles){
			mole.onDraw(canvas);
		}

	}
	public void reset(){
		for(MoleSprite mole :moles)
			mole.reset();
		needRedraw = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean clicked = false;
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			synchronized(getHolder()){
				for(MoleSprite mole : moles){
					if(mole.isClicked(event.getX(), event.getY())){
						Log.i(tag, "Mole clicked: "+mole.toString());
						if(mole.getStatus() == MoleSprite.DIGUPFULL){
							mole.digDown();
							clicked = true;
							ToposGameActivity.getVibrator().vibrate(30);//TODO creo que este valor es bueno, sino 45 estaria bien.
						}
					}
				}
			}
		}
		gameLoopThread.click(clicked);
		return clicked;
	}

}