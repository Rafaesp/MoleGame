package com.androidsamples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.view.Window;
import android.widget.TextView;

public class ToposGameActivity extends Activity {
	
	private static Vibrator vibrator;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gameview);
        
        ToposGameView toposview = (ToposGameView) findViewById(R.id.toposview);
        TextView txtLivesView = (TextView) findViewById(R.id.txtLives);
        toposview.setLivesTxtView(txtLivesView);
        TextView txtTimeView = (TextView) findViewById(R.id.txtTime);
        toposview.setTimeTxtView(txtTimeView);
        TextView txtPointsView = (TextView) findViewById(R.id.txtPoints);
        toposview.setPointsTxtView(txtPointsView);
        
        vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
       
	}
    
    public static Vibrator getVibrator(){
    	return vibrator;
    }
    
}
