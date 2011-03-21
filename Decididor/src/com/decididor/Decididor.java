package com.decididor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Decididor extends Activity implements OnClickListener, OnLongClickListener{
	private List<CheckBox> lcb;
	private Button btnDecide;
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lcb = new ArrayList<CheckBox>();
        lcb.add((CheckBox) findViewById(R.id.cb1));
        lcb.add((CheckBox) findViewById(R.id.cb2));
        lcb.add((CheckBox) findViewById(R.id.cb3));
        lcb.add((CheckBox) findViewById(R.id.cb4));
        lcb.add((CheckBox) findViewById(R.id.cb5));
        lcb.add((CheckBox) findViewById(R.id.cb6));
        lcb.add((CheckBox) findViewById(R.id.cb7));
        lcb.add((CheckBox) findViewById(R.id.cb8));
        lcb.add((CheckBox) findViewById(R.id.cb9));
        lcb.add((CheckBox) findViewById(R.id.cb10));
        lcb.add((CheckBox) findViewById(R.id.cb11));
        lcb.add((CheckBox) findViewById(R.id.cb12));
        lcb.add((CheckBox) findViewById(R.id.cb13));
        lcb.add((CheckBox) findViewById(R.id.cb14));
        lcb.add((CheckBox) findViewById(R.id.cb15));
        lcb.add((CheckBox) findViewById(R.id.cb16));
        
        
        btnDecide = (Button) findViewById(R.id.btnDecide);
        
        btnDecide.setOnClickListener(this);
        for(CheckBox cb : lcb){
        	cb.setOnLongClickListener(this);
        }
        restauraEtiquetas(lcb);
    }
	protected void restauraEtiquetas(List<CheckBox> lcb) {
		SharedPreferences etiquetas = getPreferences(0);
		int i = 1;
		for(CheckBox cb : lcb){
			String txt = etiquetas.getString("etCB"+i, "Ninguna");
			cb.setText(txt);
			i++;
		}
	}
	protected void guardaEtiquetas(List<CheckBox> lcb){
		SharedPreferences etiquetas = getPreferences(0);
		SharedPreferences.Editor editor = etiquetas.edit();
		int i = 1;
		for(CheckBox cb : lcb){
			editor.putString("etCB"+i, cb.getText().toString());
			i++;
		}
		editor.commit();
	}
	public void onClick(View v) {
		ArrayList<CheckBox> marcados = new ArrayList<CheckBox>();
		for(CheckBox cb : lcb){
			if(cb.isChecked()) marcados.add(cb);
		}
		if(!marcados.isEmpty()){
		Random rand = new Random();
		CheckBox elegida = marcados.get(rand.nextInt(marcados.size()));
		//dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(elegida.getText())
		       .setCancelable(false)
		       .setTitle("La decisión elegida es:")
		       .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		        	   
		               Decididor.this.finish();
		           }
		       })
		       .setNegativeButton("Otra", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		}
	}
	public boolean onLongClick(View v) {
		final CheckBox cb = (CheckBox) v;
		final EditText etNuevaOpcion = new EditText(Decididor.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true)
		       .setTitle("Nueva opción")
		       .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                cb.setText(etNuevaOpcion.getText());
		                guardaEtiquetas(lcb);
		           }
		       })
		       .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		builder.setView(etNuevaOpcion);
		AlertDialog alert = builder.create();
		alert.show();
		return false;
	}
	
}