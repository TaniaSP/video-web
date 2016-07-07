package mx.RosasSoto.proyectofinalvideo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class VideosBD extends SQLiteOpenHelper {
	
	private String sqlCreate = "CREATE TABLE Dispositivo (id TEXT, descripcion TEXT,contra TEXT, latitud TEXT, longitud TEXT)";
	private String sqlCreate2 = "CREATE TABLE Playlist (id_video TEXT, id_dispositivo TEXT, orden TEXT)";
	private String sqlCreate3 = "CREATE TABLE Reproduccion (id_video TEXT, id_dispositivo TEXT, Hora TEXT, Fecha TEXT)";
	private String sqlCreate4 = "CREATE TABLE Video (id TEXT, direccion TEXT, nombre TEXT)";
	private String sqlDispositivo = "INSERT into Playlist (iid_videod, id_dispositivo, orden, longitud) VALUES ('1','2','3')";
	public VideosBD(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
		db.execSQL(sqlCreate3);
		db.execSQL(sqlCreate4);
		//db.execSQL(sqlDispositivo);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Dispositivo");
        db.execSQL("DROP TABLE IF EXISTS Playlist");
        db.execSQL("DROP TABLE IF EXISTS Video");
        db.execSQL("DROP TABLE IF EXISTS Reproduccion");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
        db.execSQL(sqlCreate4);
        //db.execSQL(sqlDispositivo);
        
	}
}
