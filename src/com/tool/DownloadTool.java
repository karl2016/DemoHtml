package com.tool;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class DownloadTool {
	public final String TAG = "DownloadTool";
	long mDownloadId;

	public void downloadFiles(Context context, String uri, String name)
	{
		Log.v(TAG, uri + " " +  Environment.DIRECTORY_DOWNLOADS +"  " + name);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		request.setTitle("demohtml");
	    request.setDescription("apk downloading");
	    request.setVisibleInDownloadsUi(true);
	    // request.setAllowedOverRoaming(false); 
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
		Log.v(TAG, Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS).getPath());
//		request.setDestinationInExternalPublicDir(Const.getHtmlDirPath(), name);
		DownloadManager downManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		mDownloadId = downManager.enqueue(request);
		
	}

	public void cancelDownload(Context context) {
		DownloadManager downManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		downManager.remove(mDownloadId);
	}
}
