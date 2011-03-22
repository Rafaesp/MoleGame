package com.silenciador;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class New extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_xml);
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        
        
    }
    
    public void TODO(){
    	GregorianCalendar cal = new GregorianCalendar();
        
        try {
			FileOutputStream fos = openFileOutput("calendar", MODE_PRIVATE);
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			oos.writeObject(cal);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public void onClick(View v) {
		
		Intent i = new Intent(getApplicationContext(), Silenciador.class);
		startActivity(i);
	}
}
