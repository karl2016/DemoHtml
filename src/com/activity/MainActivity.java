package com.activity;

import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.karl.demohtml.R;
import com.tool.Const;
import com.tool.HttpTool;
import com.tool.UpdateInfo;

public class MainActivity extends Activity {
	public final String TAG = "MainActivity";

	public Handler mHandler;
	public UpdateInfo mUpdateInfo;
	private WebView mWebView;
	private MainActivity mMainActivity;
	DownloadCompleteReceiver mReceiver; 
	private UpdateDialog mConnectingDialog;
	private UpdateDialog mDownloadingDialog;
	private String mHtmlVersion = "0.0.0";
	List<String> mDownloadList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mMainActivity = this;
		mHandler = new MainHandler();
		mReceiver = new DownloadCompleteReceiver();
		mWebView = (WebView) findViewById(R.id.webView1);

		mWebView.setWebViewClient(new MyWebViewClient());

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
		// new HttpOperation().HttpGetUpdateInfo();
		// new HttpOperation().downloadFiles(this,
		// "http://www.langjingyuan.com/padweb/Demo.v1.1.0(demo).zip",
		// "test.rar");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			Log.v(TAG, "goback");
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyWebViewClient extends WebViewClient {
		public final String FLAG_APP = "app://";
		public final String FLAG_PPT = "app://playppt";
		public final String FLAG_PDF = "app://playpdf";
		public final String FLAG_VIDEO = "app://playvideo";
		public final String FLAG_MP3 = "app://playmp3";
		public final String FLAG_AD = "app://playad"; // Advertisement
		public final String FLAG_UPDATE = "app://update"; 

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.v(TAG, "shouldOverrideUrlLoading:" + url);
			if (url.startsWith(FLAG_APP)) {
				if (url.startsWith(FLAG_AD)) {
					jumpToAd(view, url);
				} else if (url.startsWith(FLAG_PPT) || url.startsWith(FLAG_PDF)) {
					jumpToOffice(view, url);
				} else if (url.startsWith(FLAG_VIDEO)) {
					jumpToVideo(view, url);
				} else if (url.startsWith(FLAG_MP3)) {
					jumpToMp3(view, url);
				} else if (url.startsWith(FLAG_UPDATE)) {
					jumpToUpdate(view, url);
				}

			} else {
				view.loadUrl(url);
				return true;
			}
			return true;
		}

		void jumpToAd(WebView view, String url) {
			String path = url.substring(url.indexOf("(") + 1,
					url.lastIndexOf(")"));
			path = Const.getHtmlDirPath() + path;
			Log.v(TAG, "jumpToAd:" + path);
			Intent intent = new Intent().setClassName(view.getContext(),
					"com.activity.VideoPlayerActivity");
			Bundle bundle = new Bundle();
			bundle.putString(VideoPlayerActivity.KEY_VIDEO_PATH, path);
			bundle.putInt(VideoPlayerActivity.KEY_VIDEO_TYPE,
					VideoPlayerActivity.TYPE_VIDEO_ADVERTISEMENT);
			intent.putExtras(bundle);
			view.getContext().startActivity((intent));
		}

		void jumpToOffice(WebView view, String url) {
			String path = url.substring(url.indexOf("(") + 1,
					url.lastIndexOf(")"));
			path = Const.getHtmlDirUri() + path;
			Log.v(TAG, "jumpToOffice:" + path);
			view.getContext().startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(path)));
		}

		void jumpToVideo(WebView view, String url) {
			// "app://playvideo(res/demo.mkv, 1)"
			String path = url.substring(url.indexOf("(") + 1,
					url.lastIndexOf(")"));
			path = Const.getHtmlDirPath() + path;

			if (path.contains(",")) {
				path = path.substring(0, path.indexOf(","));
			}
			Log.v(TAG, "jumpToAd:" + path);
			Intent intent = new Intent().setClassName(view.getContext(),
					"com.activity.VideoPlayerActivity");
			Bundle bundle = new Bundle();
			bundle.putString(VideoPlayerActivity.KEY_VIDEO_PATH, path);
			bundle.putInt(VideoPlayerActivity.KEY_VIDEO_TYPE,
					VideoPlayerActivity.TYPE_VIDEO_FILE);
			intent.putExtras(bundle);
			view.getContext().startActivity((intent));
		}

		void jumpToMp3(WebView view, String url) {

		}

		void jumpToUpdate(WebView view, String url) {
			if (HttpTool.isNetworkConnected(view.getContext()) == false) {
				Log.v(TAG, "jumpToUpdate:net is not valid");
				Toast.makeText(view.getContext(), "net is not valid",
						Toast.LENGTH_SHORT).show();
				return;
			}
			mHtmlVersion = url.substring(url.indexOf("(") + 1,
					url.lastIndexOf(")"));
			
			mConnectingDialog = new UpdateDialog(view.getContext(),
					UpdateDialog.DIALOG_TYPE_CONNECTING_NET, mMainActivity, null);
			mConnectingDialog.show();
			
		}
	}
	class DownloadCompleteReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){  
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
                Log.v(TAG," download complete! id : "+downId);
                
                String file = mDownloadingDialog.getDownloadFilePath();
                mDownloadingDialog.dismiss();
                Toast.makeText(context, file + " 下载完成", Toast.LENGTH_SHORT).show();
                
                //download apk after download html
                if (file.endsWith(".apk") == false ){
    				if (mUpdateInfo.getApkUpdateVersion().compareTo(getLocalApkVersion()) > 0) {
    					mDownloadingDialog = new UpdateDialog(mMainActivity, UpdateDialog.DIALOG_TYPE_DOWNLOADING_APP, mMainActivity, null);
    					mDownloadingDialog.show();
    				}
                }
            }  
        }  
    }  
      
    @Override  
    protected void onResume() {  
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));  
        super.onResume();  
    }  
      
    @Override  
    protected void onDestroy() {  
        if(mReceiver != null)
        	unregisterReceiver(mReceiver);  
        super.onDestroy();  
    }
    
	class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String type = b.getString(Const.HANDLER_KEY_TYPE);
			Log.v(TAG, type);
			if (type.equalsIgnoreCase(Const.HANDLER_VALUE_EXCEPTION)) {
				if (mConnectingDialog != null) {
					mConnectingDialog.dismiss();
					mConnectingDialog = null;
				}
				String exception = b.getString(Const.HANDLER_KEY_EXCEPTION);
				Toast.makeText(mMainActivity, exception, Toast.LENGTH_SHORT)
						.show();
			} else if (type.equalsIgnoreCase(Const.HANDLER_VALUE_UPDATEINFO)) {

/*				String apkUri = b.getString(Const.HANDLER_KEY_APK_UPDATE_URI);
				String apkVersion = b.getString(Const.HANDLER_KEY_APK_UPDATE_VERSION);
				String htmlUri = b.getString(Const.HANDLER_KEY_HTML_UPDATE_URI);
				String htmlVersion = b.getString(Const.HANDLER_KEY_HTML_UPDATE_VERSION);*/
				
				String info = "";
				boolean apkHaveUpdate = false;
				boolean htmlHaveUpdate = false;
				if (mUpdateInfo.getApkUpdateVersion().compareTo(getLocalApkVersion()) > 0) {
					apkHaveUpdate = true;
					info += "当前app版本：" + getLocalApkVersion() + ",可更新版本："
							+ mUpdateInfo.getApkUpdateVersion();
				}
				if (mUpdateInfo.getHtmlUpdateVersion().compareTo(getLocalHtmlVersion()) > 0) {
					htmlHaveUpdate = true;
					info += "当前html版本：" + getLocalHtmlVersion() + ",可更新版本："
							+ mUpdateInfo.getHtmlUpdateVersion();
				}
				if (mConnectingDialog != null) {
					mConnectingDialog.dismiss();
					mConnectingDialog = null;
				}
				if (apkHaveUpdate || htmlHaveUpdate) {
					new UpdateDialog(mMainActivity,
							UpdateDialog.DIALOG_TYPE_DOWNLOAD_CONFIRM,
							mMainActivity, info).show();
				} else {
					info += "已经是最新版本。app版本：" + getLocalApkVersion() + " html版本："
							+ getLocalHtmlVersion();
					Toast.makeText(mMainActivity, info, Toast.LENGTH_SHORT)
							.show();
				}

			}
			if (type.equalsIgnoreCase(Const.HANDLER_VALUE_DOWNLOAD)) {
				if (mUpdateInfo.getHtmlUpdateVersion().compareTo(getLocalHtmlVersion()) > 0) {
					mDownloadingDialog = new UpdateDialog(mMainActivity, UpdateDialog.DIALOG_TYPE_DOWNLOADING_HTML, mMainActivity, null);
					mDownloadingDialog.show();
				}
				else if (mUpdateInfo.getApkUpdateVersion().compareTo(getLocalApkVersion()) > 0) {
					mDownloadingDialog = new UpdateDialog(mMainActivity, UpdateDialog.DIALOG_TYPE_DOWNLOADING_APP, mMainActivity, null);
					mDownloadingDialog.show();
				}
				 
			}
		}
	}
	private String getLocalApkVersion() {
		PackageManager packageManager = getPackageManager();
		// 0 means get version info
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "0.0.0";
		}
	}
	private String getLocalHtmlVersion() {
		return mHtmlVersion;
	}

}
