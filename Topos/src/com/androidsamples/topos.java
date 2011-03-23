package com.androidsamples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Topos extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
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

		default:
			break;
		}
		
	}
    
}