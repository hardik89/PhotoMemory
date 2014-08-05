package com.photometry.utils;

import android.location.LocationManager;

public class LocationUtils {

	public static boolean isProviderEnabled(LocationManager manager) {

		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return true;
		}
		return false;
	}
}
