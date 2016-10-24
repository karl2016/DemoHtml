package com.ui;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
	public static final int HANDLER_MSG_DOWNLOAD_FINISH = 5;

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
//			Toast.makeText(mActivity, mActivity.mExceptionString,
//					Toast.LENGTH_SHORT).show();
			new UpdateDialog(mActivity,
					UpdateDialog.DIALOG_TYPE_INFO_EXCEPTION, mActivity,
					null).show();
			break;
		}
		case HANDLER_MSG_UPDATEINFO: {
			String info = "";
			boolean apkHaveUpdate = false;
			boolean htmlHaveUpdate = false;
			if (mActivity.mUpdateInfo.getApkUpdateVersion().compareTo(
					AppState.getLocalApkVersion(mActivity)) > 0) {
				apkHaveUpdate = true;
				info += "��ǰapp�汾��" + AppState.getLocalApkVersion(mActivity)
						+ ",�ɸ��°汾��"
						+ mActivity.mUpdateInfo.getApkUpdateVersion();
			}
			if (mActivity.mUpdateInfo.getHtmlUpdateVersion().compareTo(
					AppState.getLocalHtmlVersion(mActivity)) > 0) {
				htmlHaveUpdate = true;
				info += "��ǰhtml�汾��" + AppState.getLocalHtmlVersion(mActivity)
						+ ",�ɸ��°汾��"
						+ mActivity.mUpdateInfo.getHtmlUpdateVersion();
			}
			if (mActivity.mConnectingDialog != null) {
				mActivity.mConnectingDialog.dismiss();
				mActivity.mConnectingDialog = null;
			}
			if (apkHaveUpdate || htmlHaveUpdate) {
				new UpdateDialog(mActivity,
						UpdateDialog.DIALOG_TYPE_DOWNLOAD_CONFIRM, mActivity,
						info).show();
			} else {
				info += "�Ѿ������°汾��app�汾��"
						+ AppState.getLocalApkVersion(mActivity) + " html�汾��"
						+ AppState.getLocalHtmlVersion(mActivity);
				Toast.makeText(mActivity, info, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD: {
			// start download html or apk, html priority
			if (mActivity.mUpdateInfo.getHtmlUpdateVersion().compareTo(
					AppState.getLocalHtmlVersion(mActivity)) > 0) {
				mActivity.sendMessage(HANDLER_MSG_START_DOWNLOAD_HTML);
			} else if (mActivity.mUpdateInfo.getApkUpdateVersion().compareTo(
					AppState.getLocalApkVersion(mActivity)) > 0) {
				mActivity.sendMessage(HANDLER_MSG_START_DOWNLOAD_APP);
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD_APP: {
			if (mActivity.mUpdateInfo.getApkUpdateVersion().compareTo(
					AppState.getLocalApkVersion(mActivity)) > 0) {
				mActivity.mDownloadingDialog = new UpdateDialog(mActivity,
						UpdateDialog.DIALOG_TYPE_DOWNLOADING_APP, mActivity,
						null);
				mActivity.mDownloadingDialog.show();
			}
			break;
		}
		case HANDLER_MSG_START_DOWNLOAD_HTML: {
			if (mActivity.mUpdateInfo.getHtmlUpdateVersion().compareTo(
					AppState.getLocalHtmlVersion(mActivity)) > 0) {
				mActivity.mDownloadingDialog = new UpdateDialog(mActivity,
						UpdateDialog.DIALOG_TYPE_DOWNLOADING_HTML, mActivity,
						null);
				mActivity.mDownloadingDialog.show();
			}
			break;
		}
		case HANDLER_MSG_DOWNLOAD_FINISH: {
			String file = mActivity.mDownloadingDialog.getDownloadFilePath();
			Log.v(TAG, " download " + file + " complete");

			Toast.makeText(mActivity, file + " �������", Toast.LENGTH_SHORT)
					.show();

			boolean isDownloadHtml = false;
			if (mActivity.mDownloadingDialog.getDownloadType() == UpdateDialog.DIALOG_TYPE_DOWNLOADING_HTML) {
				isDownloadHtml = true;
			} else if (mActivity.mDownloadingDialog.getDownloadType() == UpdateDialog.DIALOG_TYPE_DOWNLOADING_APP) {
				// installAPK(mActivity.mDownloadingDialog.getDownloadFilePath());
			}
			mActivity.mDownloadingDialog.dismiss();
			mActivity.mDownloadingDialog = null;
			// start download apk after download html finish
			if (isDownloadHtml
					&& mActivity.mUpdateInfo.getApkUpdateVersion().compareTo(
							AppState.getLocalApkVersion(mActivity)) > 0) {
				mActivity
						.sendMessage(MainHandler.HANDLER_MSG_START_DOWNLOAD_APP);
			}
		}
		}
	}

	private void installAPK(String path) {

		// ͨ��Intent��װAPK�ļ�

		Intent intent = new Intent();
		// ִ�ж���
		intent.setAction(Intent.ACTION_VIEW);
		// ִ�е���������
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse(path),
				"application/vnd.android.package-archive");
		// ����������仰�ǿ��Եģ��鿼������˵������������Ļ���apk��װ���֮�������������
		// android.os.Process.killProcess(android.os.Process.myPid());
		mActivity.startActivity(intent);

	}

}