package com.photometry.fragments.dialog;

import android.support.v4.app.FragmentManager;

import com.photomemory.R;

public class PhotometryAlertDialogFactory {

	public static enum PhotometryAlertDialogTypes {

		LOCATION_SERVICES_ERROR (R.string.location_services_disabled_title, R.string.location_services_disabled_details, R.string.okay, R.string.location_services_enable_button);

		int headlineId;
		int detailsId;
		int leftButtonId;
		int rightButtonId;

		private PhotometryAlertDialogTypes(int headlineId, int detailsId, int leftButtonId, int rightButtonId) {
			this.headlineId = headlineId;
			this.detailsId = detailsId;
			this.leftButtonId = leftButtonId;
			this.rightButtonId = rightButtonId;
		}
	}

	public static PhotometryAlertDialogFragment build(PhotometryAlertDialogTypes types, int dialogId) {
		return PhotometryAlertDialogFragment.newInstance(dialogId,types.headlineId, types.detailsId, types.leftButtonId, types.rightButtonId);
	}

	public static void showPhotometryAlertDialog(PhotometryAlertDialogFragment dialog, FragmentManager manager) {
		try {
			dialog.show(manager, PhotometryAlertDialogFragment.FRAGMENT_MANAGER_TAG);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
