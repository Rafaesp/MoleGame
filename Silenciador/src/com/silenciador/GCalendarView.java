package com.silenciador;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



//http://code.google.com/p/google-api-java-client/wiki/AndroidAccountManager
public class GCalendarView extends LinearLayout{
	
	public GCalendarView(Context context) {
		super(context);
		
		TextView tv = new TextView(context);
		tv.setText("asd");
		addView(tv);
		
	}
	
	
	
}
