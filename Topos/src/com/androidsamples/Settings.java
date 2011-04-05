package com.androidsamples;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Settings extends Activity implements OnCheckedChangeListener {

	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.settings);
		ToggleButton tbtnSound = (ToggleButton) findViewById(R.id.tbtnSound);
		tbtnSound.setOnCheckedChangeListener(this);
		ToggleButton tbtnVibration = (ToggleButton) findViewById(R.id.tbtnVibration);
		tbtnVibration.setOnCheckedChangeListener(this);

		SharedPreferences sp = getSharedPreferences("TOPOS", MODE_PRIVATE);
		tbtnSound.setChecked(sp.getBoolean("sound", true));
		tbtnVibration.setChecked(sp.getBoolean("vibration", true));
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// I get shared preferences (Data Storage method)
		SharedPreferences sp = getSharedPreferences("TOPOS", MODE_PRIVATE);
		// To be able to edit
		SharedPreferences.Editor editor = sp.edit();
		// Put boolean isChecked on the shared preferences which is a "map"
		// key = sound - value = isChecked
		switch (buttonView.getId()) {

		case R.id.tbtnSound:
			editor.putBoolean("sound", isChecked);
			editor.commit();
			break;

		case R.id.tbtnVibration:
			editor.putBoolean("vibration", isChecked);
			editor.commit();
			break;

		default:
			throw new IllegalArgumentException("Unexpected ID on preferences");
		}

	}

}
