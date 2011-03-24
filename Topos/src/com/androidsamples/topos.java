package com.androidsamples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class topos extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    
	private static final int SETTINGS = Menu.FIRST;
	private static final int SALIR = Menu.FIRST + 1;
	
	public DisplayMetrics metrics;
	public static int width;
	public static int height;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width=metrics.widthPixels;
        height=metrics.heightPixels;
 
       
        
        }
    
    public static int getWidth(){
    	return width;
    }
    public static int getHeight(){
    	return height;
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