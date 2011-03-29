package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ToposGameActivity extends Activity {
	

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(new ToposGameView(this));
        setContentView(R.layout.gameview);
	}
}
