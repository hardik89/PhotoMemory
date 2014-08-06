package com.photometry.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {

	private static LruCache<LatLng, Address> addressCache = null;

	static LruCache<LatLng, Address> getAddressCache() {
		if(addressCache == null) {
			addressCache = new LruCache<LatLng, Address>(100);
		}
		return addressCache;
	}

	public static boolean isProviderEnabled(LocationManager manager) {

		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return true;
		}
		return false;
	}

	public static class GetAddressFromLatLngTask extends AsyncTask<LatLng, Void, Address> {
		Context context;
		AddressRetrievedCallback callback;

		public static interface AddressRetrievedCallback {
			public void onAddressRetrieveFailed();
			public void onAddressRetrieveSuccess(Address address);
		}

		public GetAddressFromLatLngTask(Context context, AddressRetrievedCallback callback) {
			super();
			this.context = context;
			this.callback = callback;
		}

		/**
		 * Get a geocoder instance, get the latitude and longitude
		 * lookup the address, and return it
		 */
		@Override
		protected Address doInBackground(LatLng... params) {
			if(params[0] == null) {
				return null;
			}

			Address address = getAddressCache().get(params[0]);
			if(null != address) {
				return address;
			}

			Geocoder geocoder = new Geocoder(context, Locale.getDefault());

			Location location = new Location("");
			location.setLatitude(params[0].latitude);
			location.setLongitude(params[0].longitude);

			List<Address> addresses = null;

			try {
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalStateException ex) {
				ex.printStackTrace();
				return null;
			}

			if(addresses != null && !addresses.isEmpty()) {
				address = addresses.get(0);
			}

			if(address != null) {
				getAddressCache().put(params[0], address);
			}
			return address;
		}

		@Override
		protected void onPostExecute(Address address) {
			if (callback == null || isCancelled()) {
				return;
			}

			if(address == null) {
				callback.onAddressRetrieveFailed();
			} else {
				callback.onAddressRetrieveSuccess(address);
			}
		}

	}


}
