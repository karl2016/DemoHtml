package com.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.karl.demohtml.R;
import com.tool.AppState;
import com.tool.Const;
import com.tool.UpdateInfo;

public class MainActivity extends Activity {
	public final String TAG = "MainActivity";


	public UpdateInfo mUpdateInfo;
	public UpdateDialog mConnectingDialog;
	public UpdateDialog mDownloadingDialog;
	public UpdateDialog mUnzipDialog;
	public String mExceptionString;

	public MainHandler mHandler;
	private WebView mWebView;
	private MainActivity mMainActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// Òþ²Ø±êÌâÀ¸
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mMainActivity = this;
		mHandler = new MainHandler(mMainActivity);
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
		
		
		if (AppState.isNetworkConnected(mMainActivity) == true) {
			mMainActivity.mConnectingDialog = new UpdateDialog(mMainActivity,
					UpdateDialog.DIALOG_TYPE_CONNECTING_NET, mMainActivity, null);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			Log.v(TAG, "goback");
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
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
