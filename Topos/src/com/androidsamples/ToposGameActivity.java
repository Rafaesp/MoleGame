package com.androidsamples;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
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
		SharedPreferences prefs = getSharedPreferences(topos.PREFS, MODE_APPEND);
		SharedPreferences.Editor editor= prefs.edit();
		if(toposview.getGameLoopThread().canSave()){			
			editor.putBoolean("saved", true);
			editor.putInt("level", toposview.getGameLoopThread().getLevel());
			editor.putInt("points", toposview.getGameLoopThread().getLives());
			editor.putInt("points", toposview.getGameLoopThread().getPoints());
			editor.putFloat("playVelocity", toposview.getGameLoopThread().getPlayVelocity().floatValue());
		}else{
			editor.putBoolean("saved", false);
		}
		
		editor.commit();
		finish();
		super.onPause();
	}
    
    

}
