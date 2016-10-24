package com.tool;

import android.os.Environment;

public class Const {
 
	
	public static String getNetUri() {
		return "http://www.langjingyuan.com/padweb/geturl.php";
	} 

	public static String getHtmlDirUri() {
		return "file://" + Environment.getExternalStorageDirectory().toString()
				+ "/demo/";
	}
	public static String getIndexUri() {
		return "file://" + Environment.getExternalStorageDirectory().toString()
				+ "/demo/index.html";
	}
	public static String getHtmlDirPath() {
		return  Environment.getExternalStorageDirectory().toString()
				+ "/demo/";
	}
	public static String getDownloadDirPath() {
		return Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS).getPath();
	}
}
