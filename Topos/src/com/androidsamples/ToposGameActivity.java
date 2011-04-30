package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
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
