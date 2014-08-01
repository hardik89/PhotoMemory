package com.photometry.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.photomemory.R;
import com.photometry.utils.Constants;

public class HomeActivity extends Activity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private final int IMAGE_CAPTURE_REQUEST = 1000;
	private String mCurrentPhotoPath = null;
	private Uri photoUri = null;
	public static final int LOCATION_ACCURACY = 200;

	private LocationClient locationClient;
	public Location location = null;
	public LocationRequest locationRequest = null;
	public String currentLocation = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		findViewById(R.id.camera_image_view).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createCameraIntent();
			}
		});

		locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void createCameraIntent() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(cameraIntent.resolveActivity(getPackageManager()) != null) {

			// Create file where the photo should reside
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch(IOException ex) {
				Log.e("HomeActivity", "IO Exception");
			}

			if(photoFile != null) {
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {

			saveImageToSDCard();
			setPic();
		}

	}

	private void setPic() {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		// Dimensions of the view

		int targetW = size.x;
		int targetH = size.y;

		// Get dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.max(photoW/ targetW, photoH/targetH);

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		View imagesView = findViewById(R.id.location_images_layout);
		findViewById(R.id.location_images_scroll_view).setVisibility(View.VISIBLE);
		findViewById(R.id.no_pictures_layout).setVisibility(View.GONE);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(bitmap);
		((GridLayout)imagesView).addView(iv);



	}

	private void saveImageToSDCard() {
		if(null != photoUri) {
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(photoUri);
			this.sendBroadcast(mediaScanIntent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_take_photo :
			createCameraIntent();
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.take_photo, menu);
		final MenuItem item = menu.findItem(R.id.action_take_photo);
		if(item.getActionView() != null) {
			item.getActionView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onOptionsItemSelected(item);
				}
			});
		}
		return true;

	}

	private File createImageFile() throws IOException {

		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "PhotoMemory_" + timeStamp;
		File image = File.createTempFile(
				imageFileName,   /* prefix */
				".jpg",			 /* suffix */
				getAlbumDirectory());     /* directory */

		photoUri = Uri.fromFile(image);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private static File getAlbumDirectory() {
		File storageDir = null;

		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = getPublicStorageDirectory();

			if(null != storageDir) {
				if( !storageDir.mkdirs()) {
					if( !storageDir.exists()) {
						Log.d("HomeActivity", "Failed to create Directory");
						return null;
					}
				}
			}
		} else {
			Log.d("HomeActivity", "External directory is unavailable");
		}
		return storageDir;
	}

	private static File getPublicStorageDirectory(){
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.PHOTO_ALBUM_NAME);
		return file;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

}
