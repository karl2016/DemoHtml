package com.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.tool.Const;
import com.tool.ZipTool;

public class UnzipDialog extends ProgressDialog {

	private final String TAG = "UpdateDialog";
	ZipTool mZipTool;

	String mUnzipFileName = "";
	MainActivity mActivity;

	public UnzipDialog(Context context, String msg) {
		super(context);
		mActivity = (MainActivity) context;
		setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setTitle("安装数据文件");
		setMessage("清空数据文件夹");
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		mUnzipFileName = msg;

		// clear files in html
		// String dirPath = Environment.getExternalStorageDirectory().toString()
		// + "/demohtml/";

	}
	public void startUnzipFile() {
		String dirPath = Const.getHtmlDirPath();
		unzipFile(mUnzipFileName, dirPath);
	}
	void unzipFile(String zipFilePath, String dir) {

		mZipTool = new ZipTool() {
			@Override
			public void onUnzipingFile(String filename) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setMessage("正在解压：" + mZipTool.getUnzippingFile());
					}
				});

			}

			@Override
			public void onUnzipFinish(String zipFilePath) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity,
								mZipTool.getZipFileName() + "解压成功",
								Toast.LENGTH_SHORT).show();
						mActivity
								.sendMessage(MainHandler.HANDLER_MSG_UNZIP_FINISH);
					}
				});
			}

			@Override
			public void onUnzipException(String exception) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mActivity.mExceptionString = "解压异常："
								+ mZipTool.getException();
						mActivity
								.sendMessage(MainHandler.HANDLER_MSG_EXCEPTION);
						mActivity.mUnzipDialog.dismiss();
					} 
				});

			}
		};
		mZipTool.setClearOutputDirBeforeUnzip(true);
		mZipTool.unzip(zipFilePath, dir);
	}

}
