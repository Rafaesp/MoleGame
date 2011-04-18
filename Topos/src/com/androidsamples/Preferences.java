package com.androidsamples;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		startActivity(new Intent(getApplicationContext(), topos.class));
	}
}
