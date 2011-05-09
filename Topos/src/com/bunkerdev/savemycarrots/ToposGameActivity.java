package com.bunkerdev.savemycarrots;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ToposGameActivity extends Activity {

	private ToposGameView toposview;
	private LinearLayout infoBar;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gameview);

		toposview = (ToposGameView) findViewById(R.id.toposview);
		infoBar = (LinearLayout) findViewById(R.id.infoBar);
		toposview.setInfoBar(infoBar);

	}

	@Override
	protected void onPause() {
		super.onPause();
		toposview.getGameLoopThread().saveGame();
		toposview.getGameLoopThread().stopGame();
		System.gc();
		finish();
	}

}
