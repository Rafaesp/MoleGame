package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ToposGameActivity extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gameview);
        
        ToposGameView toposview = (ToposGameView) findViewById(R.id.toposview);
        TextView txtView = (TextView) findViewById(R.id.txtLives);
        txtView.setText("prueba");
        toposview.setTxtView(txtView);
	}
}
