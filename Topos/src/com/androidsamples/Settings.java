package com.androidsamples;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Settings extends Activity implements OnCheckedChangeListener{

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.settings);
		ToggleButton tbtn = (ToggleButton) findViewById(R.id.tbtnSound);
		tbtn.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//I get shared preferences (Data Storage method)
		SharedPreferences sp = getSharedPreferences("TOPOS", MODE_PRIVATE);
		//To be able to edit
		SharedPreferences.Editor editor= sp.edit();
		//Put boolean isChecked on the shared preferences which is a "map"
		//key = sound - value = isChecked
		editor.putBoolean("sound", isChecked);		
	}
	
	

}
