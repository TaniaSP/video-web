package mx.RosasSoto.proyectofinalvideo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AppLocationService extends Service implements LocationListener {

	protected LocationManager locationManager;
	Location location;
	Location gpsAnt;

	private static final long MIN_DISTANCE_FOR_UPDATE = 0;
	private static final long MIN_TIME_FOR_UPDATE =0;

	public AppLocationService(Context context) {
		locationManager = (LocationManager) context
				.getSystemService(LOCATION_SERVICE);
	}

	public Location getLocation(String provider) {
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider,
					MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
			if (locationManager != null) {
	    		Criteria criteria = new Criteria();
	    		criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    		provider = locationManager.getBestProvider(criteria, true);
				location = locationManager.getLastKnownLocation(provider);
				return location;
			}
		}
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}