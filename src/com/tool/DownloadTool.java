package com.tool;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public abstract class DownloadTool {
	public final String TAG = "DownloadTool";
	public static final int DOWNLOAD_SUCCESSFUL = 0;
	public static final int DOWNLOAD_FAILED = 1;
	long mDownloadId;
	Handler mHandler = null;

	Context mContext = null;
	DownloadCompleteReceiver mReceiver = null;

	ContentObserver mDownloadObserver = null;

	public abstract void onProgressChange(int downloadSize, int totalSize);

	public abstract void onDownloadFinish(int status, String statusString);

	public DownloadTool(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;

		registerDownloadStateListener();
	}

	public void downloadFiles(String uri, String name) {

		Log.v(TAG, uri + " " + Environment.DIRECTORY_DOWNLOADS + "  " + name);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(uri));
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		request.setTitle("demohtml");
		request.setDescription("apk downloading");
		request.setVisibleInDownloadsUi(true);
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, name);
		Log.v(TAG,
				Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DOWNLOADS).getPath());
		DownloadManager downManager = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		mDownloadId = downManager.enqueue(request);

	}

	public void cancelDownload() {

		DownloadManager downManager = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		downManager.remove(mDownloadId);

		unregisterDownloadStateListener();
	}

	private void registerDownloadStateListener() {

		// listen download finish event
		mReceiver = new DownloadCompleteReceiver();
		mContext.registerReceiver(mReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// listen download progress event
		mDownloadObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				int[] size = getProgress();
				if (size[0] > 0 && size[1] > 0) {
					onProgressChange(size[0], size[1]);
				}
			}
		};
		mContext.getContentResolver().registerContentObserver(
				Uri.parse("content://downloads/my_downloads"), true,
				mDownloadObserver);
	}

	private void unregisterDownloadStateListener() {
		if (mReceiver != null) {
			mContext.unregisterReceiver(mReceiver);
		}
		if (mDownloadObserver != null) {
			mContext.getContentResolver().unregisterContentObserver(
					mDownloadObserver);
			mDownloadObserver = null;
		}
	}

	public int[] getProgress() {
		DownloadManager.Query query = new DownloadManager.Query()
				.setFilterById(mDownloadId);
		int[] size = { 0, 0 };
		Cursor c = null;
		try {
			DownloadManager downManager = (DownloadManager) mContext
					.getSystemService(Context.DOWNLOAD_SERVICE);
			c = downManager.query(query);
			if (c != null && c.moveToFirst()) {
				int downloadSize = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				int totalSize = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

				size[0] = downloadSize;
				size[1] = totalSize;
				Log.v(TAG, downloadSize + " " + totalSize);

			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return size;
	}

	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

				long id = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				String serviceString = Context.DOWNLOAD_SERVICE;
				DownloadManager downloadManager;
				downloadManager = (DownloadManager) mContext
						.getSystemService(serviceString);
				Query statusQuery = new Query();
				statusQuery.setFilterById(id);

				Cursor cursor = downloadManager.query(statusQuery);
				if (cursor.moveToFirst()) {

					int status = cursor.getInt(cursor
							.getColumnIndex(DownloadManager.COLUMN_STATUS));
					int event = DOWNLOAD_FAILED;
					switch (status) {
					case DownloadManager.STATUS_SUCCESSFUL:
						event = DOWNLOAD_SUCCESSFUL;
						break;
					case DownloadManager.STATUS_FAILED:
						event = DOWNLOAD_FAILED;
						break;
					}
					String uri = cursor.getString(cursor
							.getColumnIndex(DownloadManager.COLUMN_URI));
					String errorStatus = cursor.getString(cursor
							.getColumnIndex(DownloadManager.COLUMN_STATUS));

					// cancel listen after download finish
					unregisterDownloadStateListener();
					
					onDownloadFinish(event, errorStatus);

				}
				cursor.close();
			}
		}

	}
}
