package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class ToposGameActivity extends Activity{
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ToposGameView(this));
        
          }

}
