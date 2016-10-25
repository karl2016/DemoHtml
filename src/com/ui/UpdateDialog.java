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

public class UpdateDialog {
	private final String TAG = "UpdateDialog";
	Dialog mDialog = null;
	MainActivity mActivity;

	HttpTool mHttpTool;
	DownloadTool mDownloadTool;
	ZipTool mZipTool;
	String mDownloadFileName;
	int mDownloadType;
	String mUnzipFileName = "";
	static public final int DIALOG_TYPE_CONNECTING_NET = 0;
	static public final int DIALOG_TYPE_DOWNLOAD_CONFIRM = 1;
	static public final int DIALOG_TYPE_DOWNLOADING_HTML = 2;
	static public final int DIALOG_TYPE_DOWNLOADING_APP = 3;
	static public final int DIALOG_TYPE_INFO_EXCEPTION = 4;
	static public final int DIALOG_TYPE_UNZIP_HTML = 5;

	public UpdateDialog(Context context, int dialog_type,
			MainActivity activity, String msg) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mActivity = activity;

		switch (dialog_type) {
		case DIALOG_TYPE_CONNECTING_NET:
			mDialog = new ProgressDialog(context);
			((ProgressDialog) mDialog)
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			((ProgressDialog) mDialog).setMessage(mActivity.getResources()
					.getString(R.string.connecting_net));
			mDialog.setCanceledOnTouchOutside(true);
			mDialog.setCancelable(true);
			getUpdateInfo();
			break;
		case DIALOG_TYPE_INFO_EXCEPTION:
			mDialog = new AlertDialog.Builder(context).create();
			mDialog.setTitle("异常");
			((AlertDialog) mDialog).setMessage(mActivity.mExceptionString);
			mDialog.setCanceledOnTouchOutside(true);
			mDialog.setCancelable(true);
			break;
		case DIALOG_TYPE_DOWNLOADING_HTML:
			mDialog = new ProgressDialog(context);
			((ProgressDialog) mDialog)
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setTitle(mActivity.getResources().getString(
					R.string.downloading));
			((ProgressDialog) mDialog).setMax(100);
			((ProgressDialog) mDialog).setProgress(0);
			((ProgressDialog) mDialog).setMessage("下载html: 版本"
					+ mActivity.mUpdateInfo.getHtmlUpdateVersion());
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			((ProgressDialog) mDialog).setButton(Dialog.BUTTON_POSITIVE,
					mActivity.getResources().getString(R.string.cancel),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							cancelDownload();
						}
					});

			String htmlName = mActivity.mUpdateInfo.getHtmlUpdateUri()
					.substring(
							mActivity.mUpdateInfo.getHtmlUpdateUri()
									.lastIndexOf("/") + 1);
			mDownloadType = DIALOG_TYPE_DOWNLOADING_HTML;
			downloadFile(mActivity.mUpdateInfo.getHtmlUpdateUri(), htmlName);
			break;
		case DIALOG_TYPE_DOWNLOADING_APP:
			mDialog = new ProgressDialog(context);
			((ProgressDialog) mDialog)
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setTitle(mActivity.getResources().getString(
					R.string.downloading));
			((ProgressDialog) mDialog).setMax(100);
			((ProgressDialog) mDialog).setProgress(0);
			((ProgressDialog) mDialog).setMessage("下载app：版本"
					+ mActivity.mUpdateInfo.getApkUpdateVersion());
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			((ProgressDialog) mDialog).setButton(Dialog.BUTTON_POSITIVE,
					mActivity.getResources().getString(R.string.cancel),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							cancelDownload();
						}
					});
			String appName = mActivity.mUpdateInfo.getApkUpdateUri()
					.substring(
							mActivity.mUpdateInfo.getApkUpdateUri()
									.lastIndexOf("/") + 1);
			mDownloadType = DIALOG_TYPE_DOWNLOADING_APP;
			downloadFile(mActivity.mUpdateInfo.getApkUpdateUri(), appName);
			break;
		case DIALOG_TYPE_DOWNLOAD_CONFIRM:

			mDialog = new AlertDialog.Builder(context).create();
			mDialog.setTitle(mActivity.getResources().getString(
					R.string.checked_newest));
			((AlertDialog) mDialog).setMessage(msg);
			((AlertDialog) mDialog).setButton(AlertDialog.BUTTON_POSITIVE,
					mActivity.getResources().getString(R.string.download),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mActivity
									.sendMessage(MainHandler.HANDLER_MSG_START_DOWNLOAD);
						}
					});
			((AlertDialog) mDialog).setButton(AlertDialog.BUTTON_NEGATIVE,
					mActivity.getResources().getString(R.string.cancel),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
			break;
		case DIALOG_TYPE_UNZIP_HTML:
			mDialog = new ProgressDialog(context);
			((ProgressDialog) mDialog)
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setTitle("安装Html文件");
			((ProgressDialog) mDialog).setMessage("清空html文件夹");
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			String filePath = msg;
			
			// clear files in html
//			String dirPath = Environment.getExternalStorageDirectory().toString()
//			+ "/demohtml/";
			String dirPath = Const.getHtmlDirPath();
			unzipFile(filePath, dirPath);
			break;
		}
	}

	void getUpdateInfo() {
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

	void unzipFile(String zipFilePath, String dir) {
		
		mZipTool = new ZipTool() {
			@Override
			public void onUnzipingFile(String filename) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((AlertDialog) mDialog).setMessage("正在解压："
								+ mZipTool.getUnzippingFile());
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
						mActivity.sendMessage(MainHandler.HANDLER_MSG_UNZIP_FINISH);
					}
				});
			}

			@Override
			public void onUnzipException(String exception) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mActivity.mExceptionString = "解压异常：" + mZipTool.getException();
						mActivity.sendMessage(MainHandler.HANDLER_MSG_EXCEPTION);
					}
				});

			}
		};
		mZipTool.setClearOutputDirBeforeUnzip(true);
		mZipTool.unzip(zipFilePath, dir);
	}

  
	void downloadFile(String uri, String name) {
		mDownloadFileName = name;

		if (fileIsExists(Const.getDownloadDirPath() + "/" + name)) {
			if (mDownloadType == DIALOG_TYPE_DOWNLOADING_APP)
				mActivity.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_APP_FINISH);
			else if(mDownloadType == DIALOG_TYPE_DOWNLOADING_HTML){
				mActivity.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_HTML_FINISH);
			}
		} else {
			mDownloadTool = new DownloadTool(mActivity, mActivity.mHandler) {

				@Override
				public void onProgressChange(int downloadSize, int totalSize) {
					((ProgressDialog) mDialog).setMax(totalSize);
					((ProgressDialog) mDialog).setProgress(downloadSize);
				}

				@Override
				public void onDownloadFinish(int status, String statusString) {
					if (status == DownloadTool.DOWNLOAD_SUCCESSFUL){
						
						if (mDownloadType == DIALOG_TYPE_DOWNLOADING_APP)
							mActivity.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_APP_FINISH);
						else if(mDownloadType == DIALOG_TYPE_DOWNLOADING_HTML){
							mActivity.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_HTML_FINISH);
						}
					}
					else if (status == DownloadTool.DOWNLOAD_FAILED) {
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

	int getDownloadType() {
		return mDownloadType;
	}

	void show() {
		if (mDialog != null)
			mDialog.show();
	}

	void dismiss() {
		if (mDialog != null)
			mDialog.dismiss();

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
