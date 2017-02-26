package com.tool;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

public abstract class DownloadTool {
	public final String TAG = "DownloadTool";
	public static final int DOWNLOAD_SUCCESSFUL = 0;
	public static final int DOWNLOAD_FAILED = 1;
	Handler mHandler = null;
	Context mContext = null;
	String mUrl = null;
	String mName = null;
	String mTempName = null;

	public abstract void onProgressChange(long downloadSize, long totalSize,
			String info);

	public abstract void onDownloadFinish(int status, String statusString);

	public DownloadTool(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;

		DownloadConfiguration configuration = new DownloadConfiguration();
		configuration.setMaxThreadNum(10);
		configuration.setThreadNum(3);
		DownloadManager.getInstance().init(mContext, configuration);
	}

	public void downloadFiles(String url, String name) {

		Log.v(TAG, url + " " + Environment.DIRECTORY_DOWNLOADS + "  " + name);
		mUrl = url;
		mName = name;
		mTempName = mName + "_temp";
		final DownloadRequest request = new DownloadRequest.Builder()
				.setName(mName).setUri(url)
				.setFolder(new File(Const.getDownloadDirPath())).build();

		// download:
		// the tag here, you can simply use download uri as your tag;
		DownloadManager.getInstance().download(request, mUrl, new CallBack() {
			@Override
			public void onStarted() {

			}

			@Override
			public void onConnecting() {

			}

			@Override
			public void onConnected(long total, boolean isRangeSupport) {

			}

			@Override
			public void onProgress(long finished, long total, int progress) {
				long downloadSize = total * progress / 100 / 1024;
				long totalSize = total / 1024;
				onProgressChange(progress, 100, "ÕýÔÚÏÂÔØ£º" + downloadSize + "KB/"
						+ totalSize + "KB");
			}

			@Override
			public void onCompleted() {

				onDownloadFinish(DOWNLOAD_SUCCESSFUL, null);
			}

			@Override
			public void onDownloadPaused() {

			}

			@Override
			public void onDownloadCanceled() {

			}

			@Override
			public void onFailed(DownloadException e) {

				onDownloadFinish(DOWNLOAD_FAILED, e.getMessage());
			}
		});
	}

	public void cancelDownload() {

		DownloadManager.getInstance().pause(mUrl);
	}

	private void registerDownloadStateListener() {

	}

	private void unregisterDownloadStateListener() {
	}

}
