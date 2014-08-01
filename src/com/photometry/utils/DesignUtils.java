package com.photometry.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class DesignUtils {

	public static Typeface cabinNormal = null;
	public static Typeface fjallaNormal = null;

	public static Typeface getCabinNormal(Context context) {
		if(null == cabinNormal) {
			cabinNormal = Typeface.createFromAsset(context.getAssets(), "fonts/cabin_reg.ttf");
		}
		return cabinNormal;
	}

	public static Typeface getFjallaNormal(Context context) {
		if(null == fjallaNormal) {
			fjallaNormal = Typeface.createFromAsset(context.getAssets(), "fonts/fjalla.ttf");
		}
		return fjallaNormal;
	}

	public static void correctCabinTypeface (TextView view, Context context) {

		if(view.isInEditMode()) {
			return;
		}
		view.setTypeface(DesignUtils.getCabinNormal(context));
	}

	public static void correctFjallaTypeface (TextView view, Context context) {

		if(view.isInEditMode()) {
			return;
		}
		view.setTypeface(DesignUtils.getFjallaNormal(context));
	}

}
