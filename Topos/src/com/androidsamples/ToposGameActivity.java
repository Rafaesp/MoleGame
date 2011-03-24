package com.androidsamples;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class ToposGameActivity extends Activity {
	
	public DisplayMetrics metrics;
	public static int width;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ToposGameView(this));
      
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width=metrics.widthPixels;
 
       
        
        }
    
    public static int getWidth(){
    	return width;
    }

    
    
    
    
	
		
		
	}

