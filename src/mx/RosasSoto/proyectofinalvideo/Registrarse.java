package mx.RosasSoto.proyectofinalvideo;

import java.util.regex.Pattern;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Registrarse extends Activity implements OnClickListener, OnTaskCompleted {
	AppLocationService appLocationService;
	String lat;
	String lon;
	Button guardar;
	private Context context;
	private String res;
	private ProgressDialog pd;
	TextView descripcion;
	Wifi internetWifi;
	TextView pass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		internetWifi= new Wifi(context);
		setContentView(R.layout.activity_registrarse);
		appLocationService = new AppLocationService(Registrarse.this);
		Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
		if (gpsLocation != null) {
			double latitude = gpsLocation.getLatitude();
			double longitude = gpsLocation.getLongitude();	
			lat=String.valueOf(latitude);
			lon=String.valueOf(longitude);
		}
		Toast.makeText(context,"Latitud su id es: " +lat + " Longitud: " + lon,Toast.LENGTH_LONG).show();

		descripcion= (TextView) findViewById(R.id.desTXT);
		pass= (TextView) findViewById(R.id.contraPass);
		guardar= (Button) findViewById(R.id.guadarBtn);
		guardar.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registrarse, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==findViewById(R.id.guadarBtn).getId())
		{
			if(internetWifi.wifi())
			{
				solicitudWS SW = new solicitudWS(context,Registrarse.this);
				SW.registrarse(descripcion.getText().toString(), pass.getText().toString(), lat, lon);
			}
			else
			{
				Toast.makeText(context,"Se requiere de conexión de Wifi",Toast.LENGTH_SHORT).show();
			}


		}
	}
	@Override
	public void onTaskCompleted() {
		// TODO Auto-generated method stub
		Intent act = new Intent(context, Login.class);
    	finish();
    	startActivity(act);
	}
}
