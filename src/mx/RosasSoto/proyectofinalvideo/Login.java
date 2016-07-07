package mx.RosasSoto.proyectofinalvideo;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnTaskCompleted {

	private Context context;
	private String res;
	private ProgressDialog pd;
	private TextView dispositivo;
	private TextView contra;
	private Button entrar;
	private String latitud;
	private String descripcion;
	private String longitud;
	SQLiteDatabase db;
	VideosBD VideosBD ;
	Wifi internetWifi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        context=this;
        internetWifi= new Wifi(context);
		setContentView(R.layout.activity_login);
        VideosBD = new VideosBD(this, "dbVideos", null, 1);
		db= VideosBD.getReadableDatabase();
        String sqlQuery = "SELECT * FROM Dispositivo;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) 
        {
        	Intent act = new Intent(this, MainActivity.class);
        	finish();
        	startActivity(act);
        }
        dispositivo=(TextView)findViewById(R.id.dispositivoTxt);
        contra=(TextView)findViewById(R.id.contraTxt);
        entrar=(Button)findViewById(R.id.entrarBtn);
        entrar.setOnClickListener(listener);
	}
	  private OnClickListener listener = new OnClickListener() {
			public void onClick(View arg0) {
					if(internetWifi.wifi())
					{
						solicitudWS SW = new solicitudWS(context,
							Login.this);
						SW.autentificar(dispositivo.getText().toString(), contra.getText().toString());
					}
					else
					{
						Toast.makeText(context,"Se requiere de conexión de Wifi",Toast.LENGTH_SHORT).show();

					}
				}
	  };
	  
	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	public void salir(View v)
	{
		finish();
	}
	public void registrarse(View v)
	{
    	Intent act = new Intent(this, Registrarse.class);
    	finish();
    	startActivity(act);
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
	public void onTaskCompleted() {
		// TODO Auto-generated method stub
		db= VideosBD.getReadableDatabase();
        String sqlQuery = "SELECT * FROM Dispositivo;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) 
        {
        	Intent act = new Intent(this, MainActivity.class);
        	finish();
        	startActivity(act);
        }
	}
}
