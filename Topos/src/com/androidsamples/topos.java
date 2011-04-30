package com.androidsamples;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.scoreloop.client.android.ui.EntryScreenActivity;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class topos extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final int PREFERENCES = Menu.FIRST;
	private static final int SALIR = Menu.FIRST + 1;
	
	public static final String PREFS = "prefs";
	
	private Button btnContinue;
	AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScoreloopManagerSingleton.destroy();
		ScoreloopManagerSingleton.init(this);
		setContentView(R.layout.main);


		adView = new AdView(this, AdSize.BANNER, "a14d9ccf09ec04d");
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
		layout.addView(adView);
	
	    TextView title= (TextView) findViewById(R.id.textView1);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/gooddogp.ttf");
        title.setTextColor(Color.rgb(0xFF, 0x7F	, 00));
        title.setTypeface(tf);
		
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);
		btnPlay.setTypeface(tf);
		
		btnContinue = (Button) findViewById(R.id.btnContinue);
		btnContinue.setOnClickListener(this);
		btnContinue.setTypeface(tf);
		
		Button btnRanking = (Button) findViewById(R.id.btnRanking);
		btnRanking.setOnClickListener(this);
		btnRanking.setTypeface(tf);
		
		Button prefBtn = (Button) findViewById(R.id.prefButton);
		prefBtn.setOnClickListener(this);
		prefBtn.setTypeface(tf);
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		AdRequest request = new AdRequest();
		adView.loadAd(request);
		
		SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
		Log.i("TAG", "OnResume");
		if(sp.getBoolean("saved", false)){
			btnContinue.setEnabled(true);
		}else{
			btnContinue.setEnabled(false);
		}
	
	}
	

	public void onClick(View v) {
		int id = v.getId();

		Intent i = null;
		switch (id) {
		case R.id.btnPlay:
			SharedPreferences.Editor edit= getSharedPreferences(PREFS, MODE_PRIVATE).edit();
			edit.putBoolean("saved", false);
			edit.commit();
			Log.i("TAG", "topos: saved=false");
			i = new Intent(getApplicationContext(), ToposGameActivity.class);
			startActivity(i);
			break;
		case R.id.btnContinue:
			i = new Intent(getApplicationContext(), ToposGameActivity.class);
			
			startActivity(i);
			break;			
		case R.id.btnRanking:
			startActivity(new Intent(getApplicationContext(),
					EntryScreenActivity.class));
			break;

		case R.id.prefButton:
			startActivity(new Intent(getApplicationContext(), Preferences.class));
			break;

		default:
			break;
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, PREFERENCES, 0, R.string.pref);
		menu.add(Menu.NONE, SALIR, 1, R.string.btnExit);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case PREFERENCES:
			startActivity(new Intent(getApplicationContext(), Preferences.class));
			break;
		case SALIR:
			exit();
			break;
		}
		return true;
	}

	private void exit() {
		setResult(RESULT_OK);
		finish();
	}

}