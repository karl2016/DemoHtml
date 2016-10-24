package com.ui;

import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.karl.demohtml.R;
import com.tool.Const;
import com.tool.UpdateInfo;

public class MainActivity extends Activity {
	public final String TAG = "MainActivity";


	public UpdateInfo mUpdateInfo;
	public UpdateDialog mConnectingDialog;
	public UpdateDialog mDownloadingDialog;
	public String mExceptionString;

	private MainHandler mHandler;
	private WebView mWebView;
	private MainActivity mMainActivity;
	private DownloadCompleteReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// “˛≤ÿ±ÍÃ‚¿∏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mMainActivity = this;
		mHandler = new MainHandler(mMainActivity);
		mReceiver = new DownloadCompleteReceiver();
		mWebView = (WebView) findViewById(R.id.webView1);

		mWebView.setWebViewClient(new MainWebViewClient(mMainActivity));

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// mWebView.getSettings().setLoadWithOverviewMode(true);

		// load html file in app
		// mWebView.loadUrl("file:///android_asset/zhenhua/index.html");

		// load html file in sdcard
		String filePath = Const.getIndexUri();
		// mWebView.loadUrl("file:///storage/sdcard0/demohtml/index.html");
		mWebView.loadUrl(filePath);
		Log.v(TAG, filePath);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			Log.v(TAG, "goback");
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

				long id = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (isDownloadSuccess(id)) {
					mMainActivity
							.sendMessage(MainHandler.HANDLER_MSG_DOWNLOAD_FINISH);
				} else {

					mMainActivity
							.sendMessage(MainHandler.HANDLER_MSG_EXCEPTION);
				}
			}
		}

		public boolean isDownloadSuccess(long id) {
			boolean ret = false;
			String serviceString = Context.DOWNLOAD_SERVICE;
			DownloadManager downloadManager;
			downloadManager = (DownloadManager) getSystemService(serviceString);
			Query statusQuery = new Query();
			statusQuery.setFilterById(id);

			Cursor cursor = downloadManager.query(statusQuery);
			if (cursor.moveToFirst()) {

				int status = cursor.getInt(cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
				switch (status) {
				case DownloadManager.STATUS_SUCCESSFUL:
					ret = true;
					break;
				case DownloadManager.STATUS_FAILED:
					ret = false;
					break;
				}
				String uri = cursor.getString(cursor
						.getColumnIndex(DownloadManager.COLUMN_URI));
				String errorStatus = cursor.getString(cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
				String exception = "œ¬‘ÿ" + uri + " ß∞‹\n" + "¥ÌŒÛ¬Î:" + errorStatus;
				mMainActivity.mExceptionString = exception;
			}
			cursor.close();
			return ret;
		}
	}

	@Override
	protected void onResume() {
		registerReceiver(mReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void sendMessage(int type) {
		Message message = mMainActivity.mHandler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt(MainHandler.HANDLER_KEY_TYPE, type);
		message.setData(bundle);
		mMainActivity.mHandler.sendMessage(message);
	}
}
