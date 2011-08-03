package com.androidsamples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.bunkerdev.savemycarrots.R;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}
	
	@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			if(preference.getKey().equals("KidPref")){
				SharedPreferences.Editor editor = getSharedPreferences(topos.PREFS, MODE_PRIVATE).edit();
				editor.putBoolean("saved", false);
				editor.commit();
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
}
