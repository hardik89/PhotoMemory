package com.photomemory;

import java.util.Date;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class ExifBitmap {

	public String fileName;
	public Bitmap bitmap;
	public Date date;
	public LatLng latlng;

	public ExifBitmap(String f, Bitmap b) {
		this(f, b, null, null);
	}

	public ExifBitmap(String f, Bitmap b, Date d, LatLng l){
		this.fileName = f;
		this.bitmap = b;
		this.date = d;
		this.latlng = l;
	}

}
