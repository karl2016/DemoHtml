package com.activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Message;

import com.karl.demohtml.R;
import com.tool.Const;
import com.tool.HttpTool;


public class UpdateDialog { 
	private final String TAG = "UpdateDialog";
	ProgressDialog mProgressDialog = null;
	AlertDialog mDialog = null;
	MainActivity mainActivity;
	HttpTool mHttpTool;
	int mDownloadType;
	static public final int DIALOG_TYPE_CONNECTING_NET = 0;
	static public final int DIALOG_TYPE_DOWNLOAD_CONFIRM = 1;
	static public final int DIALOG_TYPE_DOWNLOADING_HTML = 2;
	static public final int DIALOG_TYPE_DOWNLOADING_APP = 3;
	static public final int DIALOG_TYPE_CANCEL_CONFIRM = 4;

	public UpdateDialog(Context context, int dialog_type, MainActivity activity, String msg) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mainActivity = activity;

		switch(dialog_type)
		{
		case DIALOG_TYPE_CONNECTING_NET:
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mainActivity.getResources().getString(R.string.connecting_net));
			mProgressDialog.setCanceledOnTouchOutside(true);
			mProgressDialog.setCancelable(true);
			getUpdateInfo();
			break;
		case DIALOG_TYPE_DOWNLOADING_HTML:
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle(mainActivity.getResources().getString(R.string.downloading));
			mProgressDialog.setMessage("html:"+ mainActivity.mUpdateInfo.getHtmlUpdateVersion());
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setButton(Dialog.BUTTON_POSITIVE, mainActivity.getResources().getString(R.string.cancel), new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					cancelDownload();
				}
			});
			
			String htmlName = mainActivity.mUpdateInfo.getHtmlUpdateUri().substring(mainActivity.mUpdateInfo.getHtmlUpdateUri().lastIndexOf("/") + 1);
			downloadFile(mainActivity.mUpdateInfo.getHtmlUpdateUri(), htmlName);
			break;
		case DIALOG_TYPE_DOWNLOADING_APP:
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle(mainActivity.getResources().getString(R.string.downloading));
			mProgressDialog.setMessage("app:"+ mainActivity.mUpdateInfo.getApkUpdateVersion());
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setButton(Dialog.BUTTON_POSITIVE, mainActivity.getResources().getString(R.string.cancel), new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					cancelDownload();
				}
			});
			String appName = mainActivity.mUpdateInfo.getApkUpdateUri().substring(mainActivity.mUpdateInfo.getApkUpdateUri().lastIndexOf("/") + 1);
			downloadFile(mainActivity.mUpdateInfo.getApkUpdateUri(), appName);
			break;
		case DIALOG_TYPE_CANCEL_CONFIRM:
//			mDialog = new Dialog(context);
			break;
		case DIALOG_TYPE_DOWNLOAD_CONFIRM:

			mDialog = new AlertDialog.Builder(context).create();
			mDialog.setTitle(mainActivity.getResources().getString(R.string.checked_newest));
			mDialog.setMessage(msg);
			mDialog.setButton(AlertDialog.BUTTON_POSITIVE, mainActivity.getResources().getString(R.string.download), new OnClickListener() {	
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Message msg = mainActivity.mHandler.obtainMessage();
					Bundle b = new Bundle();
			        b.putString(Const.HANDLER_KEY_TYPE, Const.HANDLER_VALUE_DOWNLOAD);
			        msg.setData(b);
					mainActivity.mHandler.sendMessage(msg);
				}
			});
			mDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mainActivity.getResources().getString(R.string.cancel), new OnClickListener() {			
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mDialog.dismiss();
				}
			});
			break;
			}
	}
	
	void getUpdateInfo()
	{
		new HttpTool().getUpdateInfoByHttp(mainActivity);
	}
	
	void downloadFile(String uri, String name)
	{
		mHttpTool = new HttpTool();
		mHttpTool.downloadFiles(mainActivity, uri, name);
	}
	void cancelDownload(){
		mHttpTool.cancelDownload(mainActivity);
	}
	String getDownloadFilePath(){
		return mHttpTool.getDownloadFilePath();
	}
	void show(){
		if (mDialog != null)
			mDialog.show();
		if (mProgressDialog != null)
			mProgressDialog.show();
	}
	void dismiss()
	{
		if (mDialog != null)
			mDialog.dismiss();
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}
}

