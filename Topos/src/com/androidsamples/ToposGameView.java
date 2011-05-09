package com.androidsamples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
	private PopupWindow pw;
	private LinearLayout infoBar;
	private Context context;
	private ProgressDialog progressd;
	private HashMap<String,RectPair> statusMap;
	private boolean bgEnabled;


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
		
		bgEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("BackgroundPref", true);
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

				txtLivesView.setText(m.getData().getString("lives"));
				txtLevelView.setText(m.getData().getInt("level")+"");
				txtPointsView.setText(b.getString("points"));
				txtTimeView.setText(b.getString("time"));

				String type = b.getString("type");
				if (type == "level" || type == "gameover"){

					LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View levelLayout = inflater.inflate(R.layout.levelview, null);
					TextView levelValueScore = (TextView) levelLayout
					.findViewById(R.id.txtValueScore);
					levelValueScore.setText(m.getData().getString("points"));
					TextView levelScore = (TextView) levelLayout
					.findViewById(R.id.txtScore);
					TextView txtLevel = (TextView) levelLayout
					.findViewById(R.id.txtLevel);
					TextView txtValueLevel = (TextView) levelLayout
					.findViewById(R.id.txtValueLevel);
					txtValueLevel.setText(m.getData().getInt("level")+"");
					TextView txtTitle = (TextView) levelLayout.findViewById(R.id.txtTitle);
					
					Button positiveBtn = (Button) levelLayout.findViewById(R.id.btnNext);
					Button backBtn = (Button) levelLayout.findViewById(R.id.btnBack);
				
					Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/gooddogp.ttf");
			        levelScore.setTypeface(tf);
			        levelScore.setTextColor(Color.rgb(0xFF, 0x7F, 00));
			        levelValueScore.setTypeface(tf);
			        levelValueScore.setTextColor(Color.rgb(0xFF, 0x7F, 00));
			        txtValueLevel.setTypeface(tf);
			        txtValueLevel.setTextColor(Color.rgb(0xFF, 0x7F, 00));
			        txtLevel.setTypeface(tf);
			        txtLevel.setTextColor(Color.rgb(0xFF, 0x7F, 00));
			        txtTitle.setTypeface(tf);
			        txtTitle.setTextColor(Color.rgb(0xFF, 0x7F, 00));
			        positiveBtn.setTypeface(tf);
			        txtValueLevel.setTypeface(tf);
			        backBtn.setTypeface(tf);
					levelLayout.setBackgroundColor(Color.rgb(23, 86, 164));

					pw = new PopupWindow(levelLayout,getWidth(),(int)(getHeight()/1.9));


					AdView adView = new AdView((Activity) context, AdSize.BANNER, "a14d9ccf09ec04d");
					AdRequest request = new AdRequest();

					Integer dim=(int) (getHeight()*0.52);
					pw = new PopupWindow(levelLayout,getWidth(), dim);
					Log.i("DIMENSION DEL POPUP", dim.toString());
					
					LinearLayout adLayout = (LinearLayout) levelLayout.findViewById(R.id.adLayout);
					adLayout.addView(adView);
					adView.loadAd(request);

					if (m.getData().getString("type") == "gameover") {
						txtTitle.setText(R.string.txtAlertDialogGameOver);
						final Double sc = new Double(b.getString("points"));
						positiveBtn.setText(R.string.submitScore);
						positiveBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								ScoreloopManagerSingleton.get()
								.onGamePlayEnded(sc, null);
								progressd = new ProgressDialog(context);
								progressd.setMessage(context.getText(R.string.submitting));
								progressd.show();
							}
						});
						backBtn.setText(R.string.txtButtonBack);
						backBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								goMainMenu();								
							}
						});

					} else if(b.getString("type")=="level"){
						txtTitle.setText(R.string.txtAlertDialogFinishedLevel);

						positiveBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								pw.dismiss();
								gameLoopThread.startNextLevel(false);
								
							}
						});
						backBtn.setText(R.string.txtButtonBackSave);
						backBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								goMainMenu();								
							}
						});
					}

					ToposGameView toposview = (ToposGameView) findViewById(R.id.toposview);
					pw.showAtLocation(toposview, Gravity.CENTER_VERTICAL, 0, 0);
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

		statusMap = new HashMap<String, RectPair>();
	}

	private void createMoles() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 4; y++) {
				MoleSprite mole = new MoleSprite(this, x, y);
				moles.add(mole);
			}
		}
	}
	
	public void destroyMoles(){
		int size = moles.size();
		for(int i = 0; i<size; ++i){
			moles.get(i).recycleBmp();
		}
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

	public HashMap<String, RectPair> getStatusMap() {
		return statusMap;
	}

	public boolean needRedraw() {
		return needRedraw;
	}

	public void setRedraw(boolean need) {
		needRedraw = need;
	}

	protected void onDraw(Canvas canvas) {
		if(bgEnabled){
				Bitmap bit=BitmapFactory.decodeResource(this.getResources(), R.drawable.cesped);		
				canvas.drawBitmap(bit, null, new Rect(0, 0, getWidth(), getHeight()),null);
		}else
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

	class RectPair{
		public Rect src;
		public Rect dst;
		public RectPair(Rect r1, Rect r2){
			src = r1;
			dst = r2;
		}
	}
}
