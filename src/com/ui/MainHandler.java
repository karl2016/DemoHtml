package com.ui;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tool.AppState;

class MainHandler extends Handler {
	public static final String TAG = "MainHandler";
	public static final String HANDLER_KEY_TYPE = "handler_key_type";
	public static final int HANDLER_MSG_EXCEPTION = 0;
	public static final int HANDLER_MSG_UPDATEINFO = 1;
	public static final int HANDLER_MSG_START_DOWNLOAD = 2;
	public static final int HANDLER_MSG_START_DOWNLOAD_HTML = 3;
	public static final int HANDLER_MSG_START_DOWNLOAD_APP = 4;
	public static final int HANDLER_MSG_DOWNLOAD_HTML_FINISH = 5;
	public static final int HANDLER_MSG_DOWNLOAD_APP_FINISH = 6;
	public static final int HANDLER_MSG_UNZIP_FINISH = 7;

	MainActivity mActivity;

	public MainHandler(MainActivity activity) {
		mActivity = activity;
	}

	@Override
	public void handleMessage(Message msg) {
		Bundle b = msg.getData();
		int type = b.getInt(HANDLER_KEY_TYPE);
		// Log.v(TAG, type);
		switch (type) {
		case HANDLER_MSG_EXCEPTION: {
			if (mActivity.mConnectingDialog != null) {
				mActivity.mConnectingDialog.dismiss();
				mActivity.mConnectingDialog = null;
			}
			if (mActivity.mDownloadingDialog != null) {
				mActivity.mDownloadingDialog.dismiss();
				mActivity.mDownloadingDialog = null;
			}
			if (mActivity.mUnzipDialog != null) {
				mActivity.mUnzipDialog.dismiss();
				mActivity.mUnzipDialog = null;
			}
			new ExceptionDialog(mActivity).show();
			break;
		}
		case HANDLER_MSG_UPDATEINFO: {
			String info = "";
			boolean apkHaveUpdate = false;
			boolean htmlHaveUpdate = false;
			// if (mActivity.mUpdateInfo.canApkUpdate(
			// AppState.getLocalApkVersion(mActivity))) {
			// apkHaveUpdate = true;
			// info += "当前app版本：" + AppState.getLocalApkVersion(mActivity)
			// + ",可更新版本："
			// + mActivity.mUpdateInfo.getApkUpdateVersion();
			// }
			if (mActivity.mUpdateInfo.canHtmlUpdate(AppState
					.getLocalHtmlVersion(mActivity))) {
				htmlHaveUpdate = true;
				info += "当前数据版本：" + AppState.getLocalHtmlVersion(mActivity)
						+ ",可更新版本："
						+ mActivity.mUpdateInfo.getHtmlUpdateVersion();
			}
			if (mActivity.mConnectingDialog != null) {
				mActivity.mConnectingDialog.dismiss();
				mActivity.mConnectingDialog = null;
			}
			if (apkHaveUpdate || htmlHaveUpdate) {
				new DownloadConfirmDialog(mActivity, info).show();
			} else {
				info += "已经是最新版本。app版本："
						+ AppState.getLocalApkVersion(mActivity) + " 数据版本："
						+ AppState.getLocalHtmlVersion(mActivity);
				Toast.makeText(mActivity, info, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD: {
			// start download html or apk, html priority
			if (mActivity.mUpdateInfo.canHtmlUpdate(AppState
					.getLocalHtmlVersion(mActivity))) {
				mActivity.sendMessage(HANDLER_MSG_START_DOWNLOAD_HTML);
			} else if (mActivity.mUpdateInfo.canApkUpdate(AppState
					.getLocalApkVersion(mActivity))) {
				mActivity.sendMessage(HANDLER_MSG_START_DOWNLOAD_APP);
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD_APP: {
			if (mActivity.mUpdateInfo.canApkUpdate(AppState
					.getLocalApkVersion(mActivity))) {
				mActivity.mDownloadingDialog = new DownloadingDialog(mActivity,
						DownloadingDialog.DIALOG_TYPE_DOWNLOADING_APP);
				mActivity.mDownloadingDialog.startDownload();
				mActivity.mDownloadingDialog.show();
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD_HTML: {
			if (mActivity.mUpdateInfo.canHtmlUpdate(AppState
					.getLocalHtmlVersion(mActivity))) {
				mActivity.mDownloadingDialog = new DownloadingDialog(mActivity,
						DownloadingDialog.DIALOG_TYPE_DOWNLOADING_HTML);
				mActivity.mDownloadingDialog.startDownload();
				mActivity.mDownloadingDialog.show();
			}
			break;
		}
		case HANDLER_MSG_DOWNLOAD_HTML_FINISH: {
			if (mActivity.mUnzipDialog != null)
			{
				break ;
			}
			
			String file = mActivity.mDownloadingDialog.getDownloadFilePath();
			Log.v(TAG, " download " + file + " complete");

			Toast.makeText(mActivity, file + " 下载完成", Toast.LENGTH_SHORT)
					.show();

			

			mActivity.mUnzipDialog = new UnzipDialog(mActivity,
					mActivity.mDownloadingDialog.getDownloadFilePath());
			mActivity.mUnzipDialog.startUnzipFile();
			mActivity.mUnzipDialog.show();

			mActivity.mDownloadingDialog.dismiss();
			break;
		}
		case HANDLER_MSG_DOWNLOAD_APP_FINISH: {
			String file = mActivity.mDownloadingDialog.getDownloadFilePath();
			Log.v(TAG, " download " + file + " complete");

			Toast.makeText(mActivity, file + " 下载完成", Toast.LENGTH_SHORT)
					.show();
			mActivity.mDownloadingDialog.dismiss();

			String fileName = file;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileName)),
					"application/vnd.android.package-archive");
			mActivity.startActivity(intent);
			break;
		}
		case HANDLER_MSG_UNZIP_FINISH: {
			mActivity.mUnzipDialog.dismiss();
			// start download apk after unzip html finish
			if (mActivity.mUpdateInfo.canApkUpdate(AppState
					.getLocalApkVersion(mActivity))) {
				mActivity
						.sendMessage(MainHandler.HANDLER_MSG_START_DOWNLOAD_APP);
			}
			break;
		}
		}
	}

	private void installAPK(String path) {

		// 通过Intent安装APK文件

		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse(path),
				"application/vnd.android.package-archive");
		// 不加下面这句话是可以的，查考的里面说如果不加上这句的话在apk安装完成之后点击单开会崩溃
		// android.os.Process.killProcess(android.os.Process.myPid());
		mActivity.startActivity(intent);

	}

}