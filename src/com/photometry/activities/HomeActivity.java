package com.photometry.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.photomemory.R;
import com.photometry.fragments.dialog.PhotometryAlertDialogFactory;
import com.photometry.fragments.dialog.PhotometryAlertDialogFactory.PhotometryAlertDialogTypes;
import com.photometry.fragments.dialog.PhotometryAlertDialogFragment;
import com.photometry.fragments.dialog.PhotometryAlertDialogFragment.OnDismissListener;
import com.photometry.utils.Constants;
import com.photometry.utils.LocationUtils;
import com.photometry.utils.LocationUtils.GetAddressFromLatLngTask;
import com.photometry.utils.LocationUtils.GetAddressFromLatLngTask.AddressRetrievedCallback;

public class HomeActivity extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, OnDismissListener, AddressRetrievedCallback{

	private final int IMAGE_CAPTURE_REQUEST = 1000;
	private String mCurrentPhotoPath = null;
	private Uri photoUri = null;
	public static final int LOCATION_ACCURACY = 200;

	private LocationManager lm = null;
	private LocationClient locationClient;
	public Location currentLocation = null;
	public LocationRequest locationRequest = null;
	public String currentLocationAddress = "";

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

		if(lm == null) {
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if(!LocationUtils.isProviderEnabled(lm)) {
				PhotometryAlertDialogFragment fragment = PhotometryAlertDialogFactory.build(PhotometryAlertDialogTypes.LOCATION_SERVICES_ERROR, 0);
				PhotometryAlertDialogFactory.showPhotometryAlertDialog(fragment, getSupportFragmentManager());
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {

		if(locationClient.isConnected()) {
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();
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
		int scaleFactor = (int) (Math.max(photoW/ targetW, photoH/targetH) * 1.5);

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inDither = false;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		GridLayout imagesView = (GridLayout)findViewById(R.id.location_images_layout);
		findViewById(R.id.location_images_scroll_view).setVisibility(View.VISIBLE);
		findViewById(R.id.no_pictures_layout).setVisibility(View.GONE);
		ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.layout_image_capture_view, null);
		iv.setPadding(20, 0, 20, 20);
		iv.setImageBitmap(bitmap);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(currentLocation != null) {
					LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
					LocationUtils.GetAddressFromLatLngTask addressTask = new GetAddressFromLatLngTask(HomeActivity.this, HomeActivity.this);
					addressTask.execute(latlng);
				}

			}
		});
		imagesView.addView(iv);

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
		// Fail silently

	}

	@Override
	public void onConnected(Bundle data) {
		currentLocation = locationClient.getLastLocation();
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		if(currentLocation.getAccuracy() <= LOCATION_ACCURACY) {
			if(locationClient.isConnected()) {
				locationClient.removeLocationUpdates(this);
			}
			Log.v("HomeActivity", "Location is accurate to 200 meters");
			locationClient.disconnect();
		}
	}

	@Override
	public void photometryAlertLeftButtonClicked(int dialogId) {
		// DO NOTHING

	}

	@Override
	public void photometryAlertRightButtonClicked(int dialogId) {
		Intent destinationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(destinationIntent);

	}

	@Override
	public void onAddressRetrieveFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddressRetrieveSuccess(Address address) {
		Toast.makeText(this, address.getAddressLine(0), Toast.LENGTH_LONG).show();
		Log.d("Address", address.getAddressLine(0) + " " +address.getLocality());

	}

}
