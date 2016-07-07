package mx.RosasSoto.proyectofinalvideo;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.Toast;

public class solicitudWS {
	SQLiteDatabase db;
	private Context context;
	String dispositivo;
	private String res;
	String video;
	String pass;
	String fecha;
	String hora;
	 public static final int DIALOG_DOWNLOAD_PROGRESS = 1;
	private ProgressDialog pd;
	OnTaskCompleted listener;
	public solicitudWS(Context c, OnTaskCompleted l){
		context =c ;
		listener = l;
        VideosBD VideosBD = new VideosBD(c, "dbVideos", null, 1);
        db = VideosBD.getWritableDatabase();
	}
	
	public void bajarVideos(String dispositivo, String pass){
		new DownloadTask2().execute("getPlaylist", pass, dispositivo);
		pd = ProgressDialog.show(context, "Por favor espere","Consultando", true, false);
		this.dispositivo = dispositivo;
	}
	
	public void insertarReproduccion(String dispositivo, String pass, String video){
		new DownloadTask2().execute("regRepro", pass, dispositivo,video);
		//pd = ProgressDialog.show(context, "Por favor espere","Consultando", true, false);
		this.dispositivo = dispositivo;
	}
	public void insertarReproduccionLocal(String dispositivo, String pass, String video,String hora,String fecha){
		new DownloadTask2().execute("regReproLocal", pass, dispositivo,video,hora,fecha);
		//pd = ProgressDialog.show(context, "Por favor espere","Consultando", true, false);
		this.dispositivo = dispositivo;
	}
	public void autentificar(String dispositivo, String pass){
		new DownloadTask2().execute("autentificar", dispositivo, pass);
		pd = ProgressDialog.show(context, "Por favor espere","Verificando Credenciales", true, false);
		this.dispositivo = dispositivo;
	}
	public void registrarse(String descripcion, String pass,String latitud, String longitud){
		new DownloadTask2().execute("registrarse", pass,latitud,longitud,descripcion);
		pd = ProgressDialog.show(context, "Por favor espere","Verificando Credenciales", true, false);
		this.dispositivo = dispositivo;
	}
	
    public ArrayList<String> getVideosLocales(){
    	ArrayList<String> videos = new ArrayList<String>();
      	String sqlQuery = "SELECT nombre FROM Video;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {	

        	do {
        		String video = cursor.getString(0).toString();
        		videos.add(video);

        	} while (cursor.moveToNext());
        }
    	return videos;
    }
	
	


	private class DownloadTask2 extends AsyncTask<String, String, Object> {
	    @SuppressWarnings({ "unused", "unused", "unused" })
		@Override
		protected String doInBackground(String... args) {
			CargaDatosWS ws=new CargaDatosWS();
			//Se invoca nuestro metodo

			String function = args[0];
			if (function.equals("getPlaylist")){
				pass = args[1];
				dispositivo = args[2];
				res=ws.getPlaylist(pass, dispositivo);	
			}
			if (function.equals("regRepro")){
				pass = args[1];
				dispositivo = args[2];
				video= args[3];
				Time today = new Time(Time.getCurrentTimezone());
				today.setToNow();
				String fecha2= today.year+"/"+(today.month+1)+"/"+today.monthDay;
				String hora2=today.hour+":"+today.minute+":"+today.second;
				res=ws.reproduccion(dispositivo, pass, video, hora2, fecha2);	
			}
			if (function.equals("regReproLocal")){
				pass = args[1];
				dispositivo = args[2];
				video= args[3];
				fecha= args[5];
				hora=args[4];
				res=ws.reproduccion(dispositivo, pass, video, hora, fecha);	
			}
			if (function.equals("autentificar")){
				pass = args[2];
				dispositivo = args[1];
				res=ws.autentifica(dispositivo, pass);
			}
			if (function.equals("registrarse")){
				pass = args[1];
				String descripcion = args[4];
				String Lat= args[2];
				String Lon=args[3];
				res=ws.registra(pass, descripcion,Lat,Lon);
			}
			return function;
		}
	    private void updateProgress(int downloadedSize, int totalSize) {
			// TODO Auto-generated method stub
			
		}
	    
	    
		@Deprecated
	    protected Dialog onCreateDialog(int id) {
	        // TODO Auto-generated method stub
	        switch (id) {
	        case DIALOG_DOWNLOAD_PROGRESS:
	            pd = new ProgressDialog(context);
	            pd.setMessage("Consultando...");
	            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            pd.setCancelable(false);
	            pd.show();
	            return pd;
	        default:
	            return null;           
	        }
	    }
	    @Override
		protected void onPostExecute(Object result) {
			//Se elimina la pantalla de por favor espere.
	    	if (result.equals("getPlaylist")){
	    		String playliststring = res;
	        	//Toast.makeText(context, "hash"+playliststring, Toast.LENGTH_SHORT).show();;
	    		playliststring = playliststring.replace("playlistResponse{return=","");
	    		playliststring = playliststring.replace("}","");
	    		playliststring = playliststring.replace(";","");
	    		String[] playlistArr = playliststring.split("\\|");
	    		ArrayList<String> playlist = new ArrayList<String>();
	    		String consulta = "DELETE FROM Playlist;";
	    		db.execSQL(consulta);
	    		//INSERT IN DB
	    		for(int i=0;i<playlistArr.length- 1;i++){
	    			String[] valores = playlistArr[i].split("\\@");
	    			String id = valores[0];
	    			String nombre = valores[1];
	    			String orden = valores[2];
	    			try {
	    			String consulta2 = "INSERT INTO Playlist (id_video,id_dispositivo, orden) VALUES ('"+id+"', '"+dispositivo+ "', '"+orden+"')";
	    			db.execSQL(consulta2);
		        	//Toast.makeText(context, id+"|"+nombre, Toast.LENGTH_SHORT).show();;

	    			playlist.add(id+"|"+nombre);
	    			  }catch(Exception e){
	    		           // here you can catch all the exceptions
	    		           
	    		        }
	    		}
	    //		playlist.remove(playlist.size()-1);
	    		ArrayList<String> videosLocales = getVideosLocales();
	    		obtenerVideos OV = new obtenerVideos(context, dispositivo, listener);
	    		OV.obtenerVideos(videosLocales,playlist);
				pd.dismiss();

	    	}

	    	if (result.equals("regRepro"))
    		{
	    		 if(res.equals("0"))
	    		 {
	    			 insertarReproduccion(dispositivo, pass, video);
	    		 }
    		}
	    	if (result.equals("regReproLocal"))
    		{
	    		 if(res.equals("0"))
	    		 {
	    			 insertarReproduccionLocal(dispositivo, pass, video,hora,fecha);
	    		 }
    		}
	    	if(result.equals("autentificar"))
	    	{
				 if(!res.equals("0") )
				 {
					 String[] items = res.split(Pattern.quote("|"));
					 String descripcion=items[0];
					 String latitud=items[1];
					 String longitud=items[2];
					 db.execSQL("INSERT INTO Dispositivo (id,descripcion,contra, latitud, longitud) VALUES ('"+dispositivo+"', '"+descripcion+"','"+pass+"','"+latitud+"','"+longitud+"')");
					 listener.onTaskCompleted();
				 }
				 pd.dismiss();
	    	}
	    	if (result.equals("registrarse"))
	    	{
	    		 pd.dismiss();
				 if(!res.equals("0") )
				 {
					Toast.makeText(context,"Registro Exitoso su id es: " +res,Toast.LENGTH_LONG).show();
		        	listener.onTaskCompleted();
				 }
				 else
				 {
					Toast.makeText(context,"No se pudo completar su registro",Toast.LENGTH_LONG).show();
				 }
	    	}
	    	
			super.onPostExecute(result);
		}
	}
}
