package mx.RosasSoto.proyectofinalvideo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnTaskCompleted {
	int i = 0;
	private CustomVideoView video;
	private File root;
	private ArrayList<String> videos;
	static final int PICK_CONTACT_REQUEST = 1;
	private String path = "/storage/";
	Context context;
	boolean actualizacion = false;
	SQLiteDatabase db;
	String dispositivo;
	String pass;
	Wifi internetWifi;
	String reproducido;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			if( getIntent().getBooleanExtra("exitme", false)){
		    	Intent act = new Intent(this, Login.class);
		    	finish();
		    	startActivity(act);
		    }
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			context = this;
			setContentView(R.layout.activity_main);
			VideosBD VideosBD = new VideosBD(this, "dbVideos", null, 1);
			internetWifi = new Wifi(context);
			final Button ConfBtn = (Button) findViewById(R.id.confBtn);
			final LinearLayout ConfLay = (LinearLayout) findViewById(R.id.confLay);
			video = (CustomVideoView) findViewById(R.id.video);
			video.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

				@Override
				public void onPlay() {
					ConfLay.setAlpha(0);
					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);

				}

				@Override
				public void onPause() {
					ConfLay.setAlpha(1);
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);

				}
			});
			video.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (ConfLay.getAlpha() == 0) {
						ConfLay.setAlpha(1);
						getWindow().clearFlags(
								WindowManager.LayoutParams.FLAG_FULLSCREEN);

					} else {
						ConfLay.setAlpha(0);
						;
						getWindow().addFlags(
								WindowManager.LayoutParams.FLAG_FULLSCREEN);

					}
					return false;
				}
			});

			videos = new ArrayList<String>();
			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/videos");
			boolean success = true;
			db = VideosBD.getWritableDatabase();
			getDispositivo();
			getPass();
			if (!folder.exists()) {
				success = folder.mkdir();
				MediaScannerConnection.scanFile(this,
						new String[] { path.toString() }, null, null);
			}
			if (success) {
				// Do something on success
				videos.clear(); // limpia la lista de videos
				videos = getVideos();
			} else {
				// Do something else on failure
			}
			if (videos.size() > 0) // mientras tenga videos que reproduzca el
									// primero, en caso contrario mostrar msj
			{
				video.setVideoPath(videos.get(0));
				video.setMediaController(new MediaController(this));
				video.start();

			} else {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
				dlgAlert.setMessage("No hay videos en esta carpeta");
				dlgAlert.setTitle("Alerta");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
			}
			// este listener hace el carrucel, al notar que termina un video,
			// reproduce el siguiente, en caso contrario muestra msj al usuario
			video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(final MediaPlayer mp) {
					if (videos.size() > 0) {
						i = (i + 1) % videos.size();
						if (i == 0) {
							// last vid
							video.stopPlayback();
							video.clearAnimation();
							String[] dir = videos.get(videos.size() - 1).split(
									"\\/");
							registrarReproduccion(dir[4]);
							if (internetWifi.wifi()) {
								solicitudWS SW = new solicitudWS(context,
										MainActivity.this);
								SW.bajarVideos(dispositivo, pass);
							} else {
								Toast.makeText(getApplicationContext(),
										"No hay conexion wifi",
										Toast.LENGTH_SHORT).show();
								video.setVideoPath(videos.get(0));
								video.start();
							}
							videos = getVideos();

						} else {
							video.setVideoPath(videos.get(i));
							video.start();
							String[] dir = videos.get(i - 1).split("\\/");

							registrarReproduccion(dir[4]);
							/** registrar que se hizo una reproducion **/
						}
					} else {
						AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
								getBaseContext());
						dlgAlert.setMessage("No hay videos en esta carpeta");
						dlgAlert.setTitle("Alerta");
						dlgAlert.setPositiveButton("OK", null);
						dlgAlert.setCancelable(true);
						dlgAlert.create().show();
					}
				}
			});
		} catch (Exception x) {
			Toast.makeText(getApplicationContext(), x.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public ArrayList<String> getVideos() {
		ArrayList<String> vid = new ArrayList<String>();
		String sqlQuery = "SELECT nombre, direccion FROM Video, Playlist WHERE id = id_video ORDER BY CAST(orden as int)";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {

			do {
				String video = cursor.getString(0).toString();
				String dirss = cursor.getString(1).toString();
				vid.add(dirss);
			} while (cursor.moveToNext());
		}
		return vid;
	}

	public void registrarReproduccion(String vi) {
		solicitudWS SW = new solicitudWS(context, MainActivity.this);
		if (internetWifi.wifi()) {
			ArrayList<String> reprodPend = getReproduccionLocal();
			for (int i = 0; i < reprodPend.size(); i++) {
				String[] rep = reprodPend.get(i).split("\\|");
				String vid = rep[0];
				String dis = rep[1];
				String hora = rep[2];
				String fecha = rep[3];
				SW.insertarReproduccionLocal(dis, pass, vid, hora, fecha);
				db.execSQL("DELETE FROM Reproduccion WHERE id_video='" + vid
						+ "' AND id_dispositivo='" + dispositivo
						+ "' AND Hora='" + hora + "' AND Fecha='" + fecha + "'");

			}
			String sqlQuery = "SELECT * FROM Video WHERE nombre='" + vi + "';";
			Cursor cursor = db.rawQuery(sqlQuery, null);
			if (cursor.moveToFirst()) {
				SW.insertarReproduccion(this.dispositivo, pass, cursor.getString(0).toString());
			}

		} else {
			String sqlQuery = "SELECT * FROM Video WHERE nombre='" + vi + "';";
			Cursor cursor = db.rawQuery(sqlQuery, null);
			if (cursor.moveToFirst()) {
				String vid = cursor.getString(0).toString();
				Time today = new Time(Time.getCurrentTimezone());
				today.setToNow();
				String fecha2 = today.year + "/" + (today.month + 1) + "/"
						+ today.monthDay;
				String hora2 = today.hour + ":" + today.minute + ":"
						+ today.second;
				db.execSQL("INSERT INTO Reproduccion (id_video,id_dispositivo, Hora,Fecha) VALUES ('"
						+ vid
						+ "', '"
						+ dispositivo
						+ "','"
						+ hora2
						+ "','"
						+ fecha2 + "')");

			}

		}
	}

	public ArrayList<String> getReproduccionLocal() {
		ArrayList<String> rep = new ArrayList<String>();

		String sqlQuery = "SELECT * FROM Reproduccion;";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(0).toString();
				String vid = cursor.getString(1).toString();
				String ho = cursor.getString(2).toString();
				String fe = cursor.getString(3).toString();
				rep.add(id + "|" + vid + "|" + ho + "|" + fe);

			} while (cursor.moveToNext());
		}
		return rep;
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

	public void abrirConfiguracion(View v) {
		Intent intent = new Intent(MainActivity.this, Configuracion.class);
		intent.putExtra("actualizacion", actualizacion);
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
	}
	
	public void Salir(View v) {
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (item.getItemId()) {
		case R.id.action_settings: {
			Intent intent = new Intent(MainActivity.this, Configuracion.class);
			intent.putExtra("actualizacion", actualizacion);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
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

		Toast.makeText(getApplicationContext(), "Playlist lista!",
				Toast.LENGTH_SHORT).show();
		videos = getVideos();
		if (videos.size() > 0) // mientras tenga videos que reproduzca el
		// primero, en caso contrario mostrar msj
		{

			video.setVideoPath(videos.get(0));
			video.setMediaController(new MediaController(this));
			video.start();

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		videos = getVideos();
		if (videos.size() > 0) // mientras tenga videos que reproduzca el
		// primero, en caso contrario mostrar msj
		{

			video.setVideoPath(videos.get(0));
			video.setMediaController(new MediaController(this));
			video.start();

		}
	}
}
