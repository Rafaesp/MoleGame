package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToposGameActivity extends Activity {

	private ToposGameView toposview;
	private LinearLayout infoBar;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gameview);

		toposview = (ToposGameView) findViewById(R.id.toposview);
		infoBar = (LinearLayout) findViewById(R.id.infoBar);
		toposview.setInfoBar(infoBar);

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("TAG", "OnPauseGameActivity");
		toposview.getGameLoopThread().saveGame();
		toposview.getGameLoopThread().stopGame();
		System.gc();
	}

}
