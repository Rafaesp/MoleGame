package com.androidsamples;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.scoreloop.client.android.ui.OnScoreSubmitObserver;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class ToposGameView extends SurfaceView implements OnTouchListener,
		OnScoreSubmitObserver {

	private static final String tag = "TAG";

	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private List<MoleSprite> moles;
	private boolean needRedraw;
	private TextView txtLivesView;
	private TextView txtPointsView;
	private TextView txtTimeView;
	private TextView txtLevelView;
	private AlertDialog alertDialog;
	private LinearLayout infoBar;
	private Context context;
	private ProgressDialog progressd;


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

		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(this);
				
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {
				if(infoBar != null){
					txtLivesView = (TextView) infoBar.findViewById(R.id.txtLives);
					txtTimeView = (TextView) infoBar.findViewById(R.id.txtTime);
					txtPointsView = (TextView) infoBar.findViewById(R.id.txtPoints);
					txtLevelView = (TextView) infoBar.findViewById(R.id.txtLevel);
				}
				Bundle b = m.getData();
				if (b.getString("lives") != null)
					txtLivesView.setText(m.getData().getString("lives"));
				if (b.getString("level") != null)
					txtLevelView.setText(m.getData().getString("level"));
				if (b.getString("points") != null)
					txtPointsView.setText(b.getString("points"));
				if (b.getString("time") != null)
					txtTimeView.setText(b.getString("time"));
				
				String type = b.getString("type");
				if (type == "level" || type == "gameover"){
					
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
									
//					AdView adView = new AdView((Activity) context, AdSize.BANNER, "a14d9ccf09ec04d");
//					AdRequest request = new AdRequest();
//
//					LinearLayout adLayout = (LinearLayout) layout.findViewById(R.id.adLayout);
//					adLayout.addView(adView);
//					adView.loadAd(request);
					
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
						builder.setNegativeButton(R.string.txtButtonBack,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										
										goMainMenu();
										
									}
								});
					} else if(b.getString("type")=="level"){
						builder.setTitle(R.string.txtAlertDialogFinishedLevel);
						builder.setPositiveButton(R.string.txtButtonNextLevel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										alertDialog.dismiss();
										gameLoopThread.startNextLevel(false);
									}
								});
						builder.setNegativeButton(R.string.txtButtonBackSave,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										goMainMenu();
									}
								});
					}
					alertDialog = builder.create();
					alertDialog.show();
				}else if (type=="saved"){
					gameLoopThread.startNextLevel(false);
				}
			}
		};

		gameLoopThread = new GameLoopThread(this, handler);

		holder = getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
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
	
	public void setInfoBar(LinearLayout bar){
		infoBar = bar;
	}

	public boolean needRedraw() {
		return needRedraw;
	}

	public void setRedraw(boolean need) {
		needRedraw = need;
	}

	protected void onDraw(Canvas canvas) {
//		Bitmap bit=BitmapFactory.decodeResource(this.getResources(), R.drawable.cespedj);		
//		canvas.drawBitmap(bit, null, new Rect(0, 0, getWidth(), getHeight()),null);
		canvas.drawColor(Color.rgb(00, 0xCD, 00));
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
						}
					}
				}
			}
		}
		return clicked;
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
		((ToposGameActivity)getContext()).finish();
	}



}