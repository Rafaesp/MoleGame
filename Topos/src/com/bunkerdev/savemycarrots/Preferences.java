package com.bunkerdev.savemycarrots;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.bunkerdev.savemycarrots.R;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}
}
