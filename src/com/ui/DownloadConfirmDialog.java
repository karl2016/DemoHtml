package com.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.karl.demohtml.R;

public class DownloadConfirmDialog extends AlertDialog {

	private final String TAG = "UpdateDialog";

	MainActivity mActivity;

	public DownloadConfirmDialog(Context context, String msg) {
		super(context);
		mActivity = (MainActivity) context;
		setTitle(mActivity.getResources().getString(R.string.checked_newest));
		setMessage(msg);
		setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getResources()
				.getString(R.string.download), new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mActivity.sendMessage(MainHandler.HANDLER_MSG_START_DOWNLOAD);
			}
		});
		setButton(AlertDialog.BUTTON_NEGATIVE, mActivity.getResources()
				.getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
	}

}
