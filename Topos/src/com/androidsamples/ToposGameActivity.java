package com.androidsamples;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class ToposGameActivity extends Activity {

	private ToposGameView toposview;



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gameview);

		toposview = (ToposGameView) findViewById(R.id.toposview);
		TextView txtLivesView = (TextView) findViewById(R.id.txtLives);
		toposview.setLivesTxtView(txtLivesView);
		TextView txtTimeView = (TextView) findViewById(R.id.txtTime);
		toposview.setTimeTxtView(txtTimeView);
		TextView txtPointsView = (TextView) findViewById(R.id.txtPoints);
		toposview.setPointsTxtView(txtPointsView);


		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		if(settings.getBoolean("MusicPref", true)){
			SoundManager music1Fx= new SoundManager("music1Fx", this.getApplicationContext());
			toposview.setSoundManager(music1Fx);
			toposview.startMusic1Fx();
		}
		if(settings.getBoolean("HitPref", true)){
			SoundManager hitFx= new SoundManager("hitFx", this.getApplicationContext());
			toposview.setSoundManager(hitFx);
		}
		if(settings.getBoolean("MissPref", true)){
			SoundManager missFx= new SoundManager("missFx", this.getApplicationContext());
			toposview.setSoundManager(missFx);
		}
		if(settings.getBoolean("VibrationPref", true)){
			Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			toposview.setVibrator(vibrator);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("TAG", "OnPauseGameActivity");
		toposview.getGameLoopThread().saveGame();
		toposview.getGameLoopThread().stopGame();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		toposview.getGameLoopThread().saveGame();
		toposview.getGameLoopThread().stopGame();
		finish();
	}

}
