package com.ui;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.karl.demohtml.R;
import com.tool.Const;
import com.tool.DownloadTool;
import com.tool.HttpTool;
import com.tool.UpdateInfo;
import com.tool.ZipTool;

public class DownloadingDialog extends ProgressDialog {
	private final String TAG = "UpdateDialog";
	MainActivity mActivity;
	DownloadTool mDownloadTool;
	int mDownloadType;
	String mUnzipFileName = "";

	String mDownloadFileName;
	static public final int DIALOG_TYPE_DOWNLOADING_HTML = 2;
	static public final int DIALOG_TYPE_DOWNLOADING_APP = 3;

	public DownloadingDialog(Context context, int type) {
		super(context);
		mActivity = (MainActivity) context;
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		setTitle(mActivity.getResources().getString(R.string.downloading));
		mDownloadType = type;
		setMax(100);
		setProgress(0);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		setMessage("正在连接");
		setButton(Dialog.BUTTON_POSITIVE,
				mActivity.getResources().getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						cancelDownload();
					}
				});

		if (DIALOG_TYPE_DOWNLOADING_HTML == type) {
			setTitle("下载数据: 版本" + mActivity.mUpdateInfo.getHtmlUpdateVersion());
		} else if (DIALOG_TYPE_DOWNLOADING_APP == type) {
			setTitle("下载app：版本" + mActivity.mUpdateInfo.getApkUpdateVersion());
		}

	}

	public void startDownload() {
		if (DIALOG_TYPE_DOWNLOADING_HTML == mDownloadType) {
			String htmlName = mActivity.mUpdateInfo.getHtmlUpdateUri()
					.substring(
							mActivity.mUpdateInfo.getHtmlUpdateUri()
									.lastIndexOf("/") + 1);

			downloadFile(mActivity.mUpdateInfo.getHtmlUpdateUri(), htmlName);
		} else if (DIALOG_TYPE_DOWNLOADING_APP == mDownloadType) {
			String appName = mActivity.mUpdateInfo.getApkUpdateUri()
					.substring(
							mActivity.mUpdateInfo.getApkUpdateUri()
									.lastIndexOf("/") + 1);
			downloadFile(mActivity.mUpdateInfo.getApkUpdateUri(), appName);

		}
	}

	void downloadFile(String uri, String name) {
		mDownloadFileName = name;

		if (fileIsExists(Const.getDownloadDirPath() + "/" + name)) {
			if (mDownloadType == DIALOG_TYPE_DOWNLOADING_APP)
				mActivity
						.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_APP_FINISH);
			else if (mDownloadType == DIALOG_TYPE_DOWNLOADING_HTML) {
				mActivity
						.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_HTML_FINISH);
			}
		} else 
		{
			mDownloadTool = new DownloadTool(mActivity, mActivity.mHandler) {

				@Override
				public void onProgressChange(long downloadSize, long totalSize,
						String info) {
					setMax((int) totalSize);
					setProgress((int) downloadSize);
					setMessage(info);
				}

				@Override
				public void onDownloadFinish(int status, String statusString) {
					if (status == DownloadTool.DOWNLOAD_SUCCESSFUL) {

						if (mDownloadType == DIALOG_TYPE_DOWNLOADING_APP)
							mActivity
									.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_APP_FINISH);
						else if (mDownloadType == DIALOG_TYPE_DOWNLOADING_HTML) {
							mActivity
									.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_HTML_FINISH);
						}
					} else if (status == DownloadTool.DOWNLOAD_FAILED) {
						mActivity.mExceptionString = "下载" + mDownloadFileName
								+ "失败\n" + "错误码:" + statusString;
						mActivity
								.sendMessage(MainHandler.HANDLER_MSG_EXCEPTION);
					}

				}
			};
			mDownloadTool.downloadFiles(uri, name);

		}
	}

	void cancelDownload() {
		if (mDownloadTool != null) {
			mDownloadTool.cancelDownload();
			mDownloadTool = null;
		}

	}

	String getDownloadFilePath() {
		return Const.getDownloadDirPath() + "/" + mDownloadFileName;
	}

	public boolean fileIsExists(String strFile) {
		try {
			File f = new File(strFile);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
