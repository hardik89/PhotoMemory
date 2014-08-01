package com.photometry.views;

import com.photometry.utils.DesignUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CabinTextView extends TextView {

	public CabinTextView(Context context) {
		this(context, null, 0);
	}

	public CabinTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}

	public CabinTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void init(Context context) {
		if(!isInEditMode()) {
			correctTypeface(context);
		}
	}

	private void correctTypeface(Context context) {
		DesignUtils.correctCabinTypeface(this, context);
	}

}
