package com.tool;

import android.os.Environment;

public class Const {
	public static final String FILE_PREFIX = "demohtml_v";
	public static final String HANDLER_KEY_TYPE = "handler_type";
	public static final String HANDLER_KEY_EXCEPTION = "handler_key_exception";
	public static final String HANDLER_KEY_APK_UPDATE_URI = "handler_key_apk_update_uri";
	public static final String HANDLER_KEY_HTML_UPDATE_URI = "handler_key_html_update_uri";
	public static final String HANDLER_KEY_APK_UPDATE_VERSION = "handler_key_apk_update_version";
	public static final String HANDLER_KEY_HTML_UPDATE_VERSION = "handler_key_html_update_version";
	public static final String HANDLER_VALUE_EXCEPTION = "handler_value_exception";
	public static final String HANDLER_VALUE_DOWNLOAD = "handler_value_download";	
	public static final String HANDLER_VALUE_UPDATEINFO = "handler_value_updateinfo";
	
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
	public static String getDownloadDirUri() {
		return Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS).getPath();
	}
}
