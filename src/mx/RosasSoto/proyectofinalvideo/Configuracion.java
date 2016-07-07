package mx.RosasSoto.proyectofinalvideo;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Process;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configuracion extends Activity implements OnTaskCompleted {
	SQLiteDatabase db;
	EditText DispositivoTV;
	private Context context;
	static final int PICK_CONTACT_REQUEST = 1;

	public boolean descargaExistosa = false;
	String dispositivo;
	private String path = "/storage/sdcard0/videos/";
	boolean actualizacion = false;
	String root = "";
	String videoGlobal = "";
	String pass;
	AlertDialog.Builder builder;
	Wifi internetWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.configuracion);
			context = this;
			internetWifi = new Wifi(context);
			int VideosDescargar = 0;
			VideosBD VideosBD = new VideosBD(this, "dbVideos", null, 1);
			db = VideosBD.getWritableDatabase();
			getDispositivo();
			getPass();
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Advertencia");
			builder.setMessage("Se eliminara toda la informacion");

			builder.setPositiveButton("SI",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Do nothing but close the dialog
							// PREGUNTAR SI HAY REPS SI HAY NO ELIMINAR NADA Y
							// NOTIFICAR
							String sqlQuery = "SELECT * FROM Reproduccion;";
							Cursor cursor = db.rawQuery(sqlQuery, null);
							if (cursor.moveToFirst()) {
								Toast.makeText(
										getApplicationContext(),
										"Aun hay registros de reproduccion. Conecte a internet primero",
										Toast.LENGTH_LONG).show();
							} else {
								// ELIMINAR TODOS LOS REGISTROS DE LA BD
								String consulta = "DELETE FROM Video;";
								db.execSQL(consulta);
								consulta = "DELETE FROM Dispositivo;";
								db.execSQL(consulta);
								consulta = "DELETE FROM Playlist;";
								db.execSQL(consulta);
								consulta = "DELETE FROM Reproduccion;";
								db.execSQL(consulta);
								// ELIMINAR TODOS LOS VIDEOS
								File dir = new File(path);
								File listFile[] = dir.listFiles();
								if (listFile != null && listFile.length > 0) {
									for (int i = 0; i < listFile.length; i++) {
										String direccion = listFile[i]
												.toString();
										File file = new File(direccion);
										boolean deleted = file.delete();
									}

								}
								Intent intent = new Intent(Configuracion.this, MainActivity.class); 
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("exitme", true);
								startActivity( intent ); 
							}
							
							dialog.dismiss();
						}

					});
			builder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
							dialog.dismiss();
						}
					});
			// get dispositivo y get passwd

			Button actualizar = (Button) findViewById(R.id.actualizar);
			actualizar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// solicitud WS
					if (internetWifi.wifi()) {
						solicitudWS SW = new solicitudWS(context,
								Configuracion.this);
						SW.bajarVideos(dispositivo, pass);
					} else
						Toast.makeText(getApplicationContext(),
								"No hay conexion wifi", Toast.LENGTH_SHORT)
								.show();

				}
			});
			Button cerrar = (Button) findViewById(R.id.cerrarSesionBtn);
			cerrar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog alert = builder.create();
					alert.show();

				}
			});
		} catch (Exception x) {
			Toast.makeText(getApplicationContext(), x.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void getDispositivo() {
		String sqlQuery = "SELECT id FROM Dispositivo;";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(0).toString();
				dispositivo = id;

			} while (cursor.moveToNext());
		}
	}

	public void getPass() {
		String sqlQuery = "SELECT contra FROM Dispositivo;";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String psw = cursor.getString(0).toString();
				pass = psw;

			} while (cursor.moveToNext());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (item.getItemId()) {
		case R.id.action_settings: {
			return true;
		}
		case R.id.salir:
			setResult(RESULT_OK);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTaskCompleted() {
		// TODO Auto-generated method stub
		actualizacion = true;
		Toast.makeText(getApplicationContext(), "Playlist lista!",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent resultData = new Intent();
			resultData.putExtra("path", actualizacion);
			setResult(Activity.RESULT_OK, resultData);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
