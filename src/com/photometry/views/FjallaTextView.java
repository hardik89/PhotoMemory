package com.photometry.views;

import com.photometry.utils.DesignUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FjallaTextView extends TextView {

	public FjallaTextView(Context context) {
		this(context, null, 0);
	}

	public FjallaTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FjallaTextView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	private void init(Context context) {
		if(!isInEditMode()) {
			correctTypeface(context);
		}
	}

	private void correctTypeface(Context context) {
		DesignUtils.correctFjallaTypeface(this, context);
	}



}
