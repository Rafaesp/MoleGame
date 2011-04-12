package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.scoreloop.client.android.ui.OnScoreSubmitObserver;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class ToposGameView extends SurfaceView implements OnTouchListener,
		OnScoreSubmitObserver {

	private static final String tag = "TAG";

	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private List<MoleSprite> moles;
	private boolean needRedraw;
	private TextView livesTxtView;
	private TextView pointsTxtView;
	private TextView timeTxtView;
	private AlertDialog alertDialog;
	private static Vibrator vibrator;
	private Context context;
	private ProgressDialog progressd;
	private SoundManager hitFx;
	private SoundManager missFx;
	private boolean musicEnabled;
	private boolean hitEnabled;
	private boolean missEnabled;
	private boolean vibrationEnabled;
	private boolean endingVibration;
	private boolean endingEnable;

	private SoundManager music1Fx;

	public ToposGameView(Context context) {
		super(context);
		this.context = context;
		initToposGameView();
	}

	public ToposGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initToposGameView();

	}

	private void initToposGameView() {

		moles = new ArrayList<MoleSprite>();
		needRedraw = true;
		setFocusable(true);
		setOnTouchListener(this);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		musicEnabled = sp.getBoolean("MusicPref", true);
		missEnabled = sp.getBoolean("MissPref", true);
		hitEnabled = sp.getBoolean("HitPref", true);
		vibrationEnabled = sp.getBoolean("VibrationPref", true);
		endingEnable = sp.getBoolean("EndingVibrationPref", true);
		endingVibration  = sp.getBoolean("EndingVibrationPref", true);

		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(this);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {
				Bundle b = m.getData();
				if (b.getString("lives") != null)
					livesTxtView.setText(m.getData().getString("lives"));
				if (b.getString("points") != null)
					pointsTxtView.setText(b.getString("points"));
				if (b.getString("time") != null)
					timeTxtView.setText(b.getString("time"));
				if (b.getString("type") != null) {
					AlertDialog.Builder builder;

					LayoutInflater inflater = (LayoutInflater) getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.levelview, null);

					TextView levelScore = (TextView) layout
							.findViewById(R.id.txtValueScore);
					levelScore.setText(m.getData().getString("points"));
					TextView txtLevel = (TextView) layout
							.findViewById(R.id.txtLevelX);
					txtLevel.setText("Level " + m.getData().getInt("level"));

					builder = new AlertDialog.Builder(getContext());
					builder.setCancelable(false);
					builder.setView(layout);
					if (m.getData().getString("type") == "gameover") {
						builder.setTitle(R.string.txtAlertDialogGameOver);
						final Double sc = new Double(b.getString("points"));
						builder.setPositiveButton(R.string.submitScore,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										ScoreloopManagerSingleton.get()
												.onGamePlayEnded(sc, null);
										progressd = new ProgressDialog(context);
										progressd
												.setMessage("Submitting score, please wait.");
										progressd.show();
									}
								});
						builder.setNegativeButton(R.string.txtButtonMain,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										gameLoopThread.stopMusic();
										goMainMenu();
										
									}
								});
					} else {
						builder.setTitle(R.string.txtAlertDialogFinishedLevel);
						builder.setPositiveButton(R.string.txtButtonNextLevel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										alertDialog.dismiss();
										gameLoopThread.startNextLevel();
									}
								});
						builder.setNegativeButton(R.string.txtButtonMain,
								new DialogInterface.OnClickListener() {// TODO
																		// negativo
																		// para
																		// guardar
																		// partida
																		// y
																		// volver
																		// menu
									public void onClick(DialogInterface dialog,
											int id) {
										goMainMenu();
									}
								});
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
				gameLoopThread.stopGame();
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				Log.i(tag, "GameView width: " + getWidth()
						+ " GameView height: " + getHeight());
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

	private void createMoles() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 4; y++) {
				MoleSprite mole = new MoleSprite(this, x, y);
				moles.add(mole);
			}
		}
		Log.i(tag, "Moles created");
	}

	public GameLoopThread getGameLoopThread() {
		return gameLoopThread;
	}

	public List<MoleSprite> getMoles() {
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

	public boolean needRedraw() {
		return needRedraw;
	}

	public void setRedraw(boolean need) {
		needRedraw = need;
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		needRedraw = false;
		for (MoleSprite mole : moles) {
			mole.onDraw(canvas);
		}

	}

	public void reset() {
		for (MoleSprite mole : moles)
			mole.reset();
		needRedraw = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean clicked = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (getHolder()) {
				for (MoleSprite mole : moles) {
					if (mole.isClicked(event.getX(), event.getY())) {
						if (mole.getStatus() != MoleSprite.HOLE && !mole.isDiggingDown() && !mole.isHit()) {
							clicked = true;
							gameLoopThread.click(mole);
							if (hitEnabled)					
								hitFx.start();
							if (vibrationEnabled)
								vibrator.vibrate(40);

						}
					}
				}
			}
		}
		return clicked;
	}

	public void setVibrator(Vibrator v) {
		vibrator = v;
	}

	public void setSoundManager(SoundManager sound) {
		if (sound.getType().equals("hitFx")) {// TODO poner como constantes
			hitFx = sound;
		} else if (sound.getType().equals("missFx")) {
			missFx = sound;
		} else if (sound.getType().equals("music1Fx")){
			music1Fx =sound;
		}
	}

	@Override
	public void onScoreSubmit(int status, Exception error) {
		progressd.dismiss();
		Toast result = Toast.makeText(context, "", 5000);
		switch (status) {
		case OnScoreSubmitObserver.STATUS_ERROR_NETWORK:
			result.setText(R.string.sl_networkError);
			break;

		case OnScoreSubmitObserver.STATUS_SUCCESS_SCORE:
			result.setText(R.string.sl_success);
			break;

		default:
			break;
		}
		result.show();
		goMainMenu();
	}

	public void goMainMenu() {
		context.startActivity(new Intent(context, topos.class));
	}

	public void startMissFx() {
		if (missEnabled)
			missFx.start();
	}
	
	public void startMusic1Fx(){
		if(musicEnabled){
			music1Fx.start();
		}
	}
	public void stopMusic1Fx(){
		if(music1Fx.isPlaying()==true){
			music1Fx.stop();
		}
	}
	
	public void startFinishVibrator(){
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
	
	public boolean getStatusEndingFx(){
		return endingEnable;
	}
	public boolean getStatusEndingVibration(){
		return endingVibration;
	}

}