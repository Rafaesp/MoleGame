package com.androidsamples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.scoreloop.client.android.ui.EntryScreenActivity;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class topos extends Activity implements OnClickListener{
	/** Called when the activity is first created. */

	private static final int SETTINGS = Menu.FIRST;
	private static final int SALIR = Menu.FIRST + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScoreloopManagerSingleton.init(this);

		setContentView(R.layout.main);

		Button btnSettings = (Button) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);

		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);

		Button btnRanking = (Button) findViewById(R.id.btnRanking);
		btnRanking.setOnClickListener(this);

	}

	public void onClick(View v) {
		int id = v.getId();

		Intent i=null;
		switch (id) {
		case R.id.btnSettings:
			i = new Intent(getApplicationContext(), Settings.class);
			startActivity(i);
			break;

		case R.id.btnPlay:
			i = new Intent(getApplicationContext(), ToposGameActivity.class);
			startActivity(i);
			break;
		case R.id.btnRanking:		
			startActivity(new Intent(getApplicationContext(), EntryScreenActivity.class));
			break;

		default:
			break;
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, SETTINGS, 0, R.string.btnSettings);
		menu.add(Menu.NONE, SALIR, 1, R.string.btnExit);
		//menu.add(Menu.NONE, SETTINGS, 0, R.string.btnExit);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch(item.getItemId()) {
		case SETTINGS:
			settings();
			break;
		case SALIR:
			exit();
			break;
		}
		return true;
	}

	private void settings(){
		Intent i=null;
		i = new Intent(getApplicationContext(), Settings.class);
		startActivity(i);
	}

	private void exit(){
		setResult(RESULT_OK);
		finish();
	}


}