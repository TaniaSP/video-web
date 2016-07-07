package descarga;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.RosasSoto.proyectofinalvideo.OnTaskCompleted;
import mx.RosasSoto.proyectofinalvideo.VideosBD;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Integer, String>{
    private NotificationHelper mNotificationHelper;
	   String nombre;
	   String id;
	   String direccion;
	    private OnTaskCompleted listener;

		SQLiteDatabase db;
	   Context context;
	 public static final int DIALOG_DOWNLOAD_PROGRESS = 1;

    public DownloadTask(Context context, String video, OnTaskCompleted listener){
    	this.context = context;
    	String sindId = video.split("\\|")[1];
    	id = video.split("\\|")[0];
        this.listener=listener;

        mNotificationHelper = new NotificationHelper(context, sindId);
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        //This is where we would do the actual download stuff
        //for now I'm just going to loop for 10 seconds
        // publishing progress every second
        VideosBD VideosBD = new VideosBD(context, "dbVideos", null, 1);
        db = VideosBD.getWritableDatabase();
    	InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(sUrl[0]);
            String name = URLUtil.guessFileName(sUrl[0], null, fileExtenstion);
            nombre =name;
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()+ " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream("/storage/sdcard0/videos/"+name);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
             /*  if (isCancelled()) {
                    input.close();
                    return null;
                }*/
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
    @Override
    protected void onPostExecute(String result)    {
        //The task is complete, tell the status bar about it
        if (result != null)
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else
        {
        	try{
            Toast.makeText(context, nombre+ " descargado", Toast.LENGTH_SHORT).show();
            String consulta = "INSERT INTO Video (id, direccion, nombre) VALUES ('"+id+"', '/storage/sdcard0/videos/"+ nombre+"', '"+nombre+ "')";
        	db.execSQL(consulta);
        	}
        	catch(Exception e){}
      	  
        }
    	listener.onTaskCompleted();
        mNotificationHelper.completed();
    }
}
