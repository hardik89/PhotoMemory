package com.photometry.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.photomemory.R;

public class PhotometryAlertDialogFragment extends DialogFragment{

	public static final String FRAGMENT_MANAGER_TAG = "PhotometryAlertDialogFragment";

	public static final int UNUSED_FEATURE_ID_VAL = -1;

	private static final String DIALOG_ID_KEY 			= "com.photometry.dialog_id";
	private static final String HEADLINE_RES_ID_KEY 	= "headlineResId";
	private static final String DETAILS_RES_ID_KEY  	= "detailsResId";
	private static final String LEFT_BUTTON_RES_ID_KEY  = "leftButtonResId";
	private static final String RIGHT_BUTTON_RES_ID_KEY = "rightButtonResId";

	public interface OnDismissListener {
		public void photometryAlertLeftButtonClicked(int dialogId);
		public void photometryAlertRightButtonClicked(int dialogId);
	}

	/**
	 * Creates new Photometry Alert Dialog with included resource id's. Any text that isn't used should be set to UNUSED_FEATURE_ID_VAL.
	 * @param dialogId
	 * @param headlineResId
	 * @param detailsResId
	 * @param leftButtonResId
	 * @param rightButtonResId
	 * @return instance of alert dialog fragment
	 */
	public static PhotometryAlertDialogFragment newInstance(int dialogId, int headlineResId, int detailsResId, int leftButtonResId, int rightButtonResId) {
		PhotometryAlertDialogFragment f = new PhotometryAlertDialogFragment();

		Bundle args = new Bundle();

		args.putInt(DIALOG_ID_KEY, dialogId);
		args.putInt(HEADLINE_RES_ID_KEY, headlineResId);
		args.putInt(DETAILS_RES_ID_KEY, detailsResId);
		args.putInt(LEFT_BUTTON_RES_ID_KEY, leftButtonResId);
		args.putInt(RIGHT_BUTTON_RES_ID_KEY, rightButtonResId);

		f.setArguments(args);

		return f;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_fragment_photometry_alert, null);

		Bundle args = getArguments();

		int headlineTextId 	= args.getInt(HEADLINE_RES_ID_KEY);
		int detailsTextId 	= args.getInt(DETAILS_RES_ID_KEY);
		int leftButtonTextId  = args.getInt(LEFT_BUTTON_RES_ID_KEY);
		int rightButtonTextId = args.getInt(RIGHT_BUTTON_RES_ID_KEY);

		TextView leftButton = (TextView) view.findViewById(R.id.photometry_alert_dialog_left_button);
		if(leftButtonTextId != UNUSED_FEATURE_ID_VAL) {
			leftButton.setText(leftButtonTextId);
			leftButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					takeDismissAction(true);
				}
			});
		} else {
			leftButton.setVisibility(View.GONE);
		}

		TextView rightButton = (TextView) view.findViewById(R.id.photometry_alert_dialog_right_button);
		if(rightButtonTextId != UNUSED_FEATURE_ID_VAL) {
			rightButton.setText(rightButtonTextId);
			rightButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					takeDismissAction(false);
				}
			});
		} else {
			rightButton.setVisibility(View.GONE);
		}

		if(headlineTextId != UNUSED_FEATURE_ID_VAL) {
			TextView headline = (TextView) view.findViewById(R.id.photometry_alert_dialog_headline);
			headline.setText(headlineTextId);
		}

		if(detailsTextId != UNUSED_FEATURE_ID_VAL) {
			TextView detail = (TextView) view.findViewById(R.id.photometry_alert_dialog_detail);
			detail.setText(detailsTextId);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		return builder.create();
	}

	protected void takeDismissAction(boolean isLeftButton) {
		dismiss();

		OnDismissListener listener;

		if(getTargetFragment() instanceof OnDismissListener) {

			listener = (OnDismissListener) getTargetFragment();
		} else if(getParentFragment() instanceof OnDismissListener) {

			listener = (OnDismissListener) getParentFragment();
		} else if(getActivity() instanceof OnDismissListener) {

			listener = (OnDismissListener) getActivity();
		} else {

			return;
		}

		int dialogId = getArguments().getInt(DIALOG_ID_KEY, -1);
		if (isLeftButton) {
			listener.photometryAlertLeftButtonClicked(dialogId);
		} else {
			listener.photometryAlertRightButtonClicked(dialogId);
		}
	}
}
