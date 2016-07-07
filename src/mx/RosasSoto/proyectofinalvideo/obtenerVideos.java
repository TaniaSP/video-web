package mx.RosasSoto.proyectofinalvideo;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import descarga.DownloadTask;

public class obtenerVideos implements OnTaskCompleted {
	Context context;
	String dispositivo;
	SQLiteDatabase db;
	int cantaDescargar = 0;
	int count=0;
	private OnTaskCompleted listener;

	public obtenerVideos(Context c, String d, OnTaskCompleted listener){
		context = c;
		dispositivo = d;
        VideosBD VideosBD = new VideosBD(c, "dbVideos", null, 1);
        db = VideosBD.getWritableDatabase();
        this.listener=listener;
	}
	
    public void obtenerVideos(ArrayList<String> videos, ArrayList<String> playlist){
    	
    	ArrayList<String> playlistSinId = new ArrayList<String>();
    	for (int i=0;i<playlist.size();i++){
    		String actual = playlist.get(i).split("\\|")[1];
    		playlistSinId.add(actual);
    	}
    	ArrayList<String> eliminarVideos = new ArrayList<String>();
    	for (int i=0;i < videos.size();i++){
    		String actual = videos.get(i);
    		if (!playlistSinId.contains(actual))
    		{
    			videos.remove(i);
    			eliminarVideos.add(actual);
    			i--;
    		}
    	}
    	eliminarVideos(eliminarVideos);
    	ArrayList<String> bajarVideos = new ArrayList<String>();
    	for (int i=0;i < playlist.size();i++){
    		String actual = playlist.get(i).split("\\|")[1];
    		String actual2 = playlist.get(i); //con el id del video
    		if (!videos.contains(actual))
    		{
    			bajarVideos.add(actual2);
    			videos.add(actual);
    		}
    	}
    	if (bajarVideos.size() == 0) {
    		listener.onTaskCompleted();
    	}
    	cantaDescargar = bajarVideos.size();
    	bajarVideos(bajarVideos);
  	
    }
    
    
    
    public void eliminarVideos(ArrayList<String> eliminarVideos){
    	for (int i = 0; i < eliminarVideos.size();i++){ 		
          	String sqlQuery = "SELECT direccion FROM Video WHERE nombre = '"+ eliminarVideos.get(i) +"';";
            Cursor cursor = db.rawQuery(sqlQuery, null);
            if (cursor.moveToFirst()) {	
        		String direccion = cursor.getString(0).toString();
        		File file = new File(direccion);
                boolean deleted = file.delete();
                if (deleted){
              	  String consulta = "DELETE FROM Video WHERE nombre = '"+eliminarVideos.get(i)+"';";
                  Toast.makeText(context, eliminarVideos.get(i)+ " eliminado", Toast.LENGTH_SHORT).show();
            	  db.execSQL(consulta);
                }
            }
    	} 
    }
	
    public void bajarVideos(ArrayList<String> bajarVideos){
    	for (int i = 0; i < bajarVideos.size();i++){
  			String webPath = "http://adminvideos.tk/videos/";
  			String sinId = bajarVideos.get(i).split("\\|")[1];
  	        new DownloadTask(context,bajarVideos.get(i), obtenerVideos.this).execute(webPath+sinId, bajarVideos.get(i));
    	}

    }

	@Override
	public void onTaskCompleted() {
		// TODO Auto-generated method stub
		count++;
		if (count == cantaDescargar){
			listener.onTaskCompleted();
		}
	}
}
