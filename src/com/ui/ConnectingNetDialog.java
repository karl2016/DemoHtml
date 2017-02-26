package com.ui;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.karl.demohtml.R;
import com.tool.Const;
import com.tool.DownloadTool;
import com.tool.HttpTool;
import com.tool.UpdateInfo;
import com.tool.ZipTool;

public class ConnectingNetDialog extends ProgressDialog {

	private final String TAG = "UpdateDialog";

	MainActivity mActivity;

	public ConnectingNetDialog(Context context) {
		super(context);
		mActivity = (MainActivity)context;
		setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setMessage(mActivity.getResources().getString(R.string.connecting_net));
		setCanceledOnTouchOutside(true);
		setCancelable(true);
	}

	public void getUpdateInfo() {
		new HttpTool() {
			@Override
			public void messageNotify(int message, Object obj) {
				switch (message) {
				case HttpTool.MSG_EXCEPTION: {
					String exceptionString = (String) obj;
					mActivity.mExceptionString = exceptionString;
					mActivity.sendMessage(MainHandler.HANDLER_MSG_EXCEPTION);
					break;
				}
				case HttpTool.MSG_GET_SUCCESS: {
					UpdateInfo info = (UpdateInfo) obj;
					mActivity.mUpdateInfo = info;
					mActivity.sendMessage(MainHandler.HANDLER_MSG_UPDATEINFO);
					break;
				}
				}
			}
		}.getUpdateInfoByHttp();
	}
}
