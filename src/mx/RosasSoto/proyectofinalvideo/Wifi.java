package mx.RosasSoto.proyectofinalvideo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Wifi {
	Context cont;
	public Wifi(Context context)
	{
		cont=context;
	}
	
	public boolean wifi()
	{
		ConnectivityManager connManager = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    return true;
		}
		else
			return false;
	}

	
}
