package com.ui;

import android.app.AlertDialog;
import android.content.Context;

public class ExceptionDialog extends AlertDialog {

	private final String TAG = "UpdateDialog";

	MainActivity mActivity;

	public ExceptionDialog(Context context) {
		super(context);
		mActivity = (MainActivity) context;
		setTitle("“Ï≥£");
		setMessage(mActivity.mExceptionString);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
	}

}
