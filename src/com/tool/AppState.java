package com.tool;

import java.io.BufferedReader;
import java.io.FileReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppState {

	static public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	static public String getLocalApkVersion(Context context) {
		PackageManager packageManager = context.getPackageManager();
		// 0 means get version info
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "0.0.0";
		}
	}

	static public String getLocalHtmlVersion(Context context) {
		return readHtmlVersionFile(Const.getHtmlVersionFilePath());
	}

	static private String readHtmlVersionFile(String filePath) {
		BufferedReader br;

		String line = "0.0.0";
		try {
			br = new BufferedReader(new FileReader(filePath));
			line = br.readLine();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
}
