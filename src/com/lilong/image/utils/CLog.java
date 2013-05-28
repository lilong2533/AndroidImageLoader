package com.lilong.image.utils;

import android.util.Log;

public class CLog {
	private static final String TAG = "lilong";
	
	public static void i(String log){
		if(log == null) return;
		Log.i(TAG, log);
	}
}
