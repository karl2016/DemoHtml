package com.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tool.AppState;
import com.tool.Const;

class MainWebViewClient extends WebViewClient {
	MainActivity mActivity;

	public MainWebViewClient(MainActivity activity) {
		mActivity = activity;
	}

	public final String TAG = "MainWebViewClient";
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
		String path = url.substring(url.indexOf("(") + 1, url.lastIndexOf(")"));
		path = Const.getHtmlDirPath() + path;
		Log.v(TAG, "jumpToAd:" + path);
		Intent intent = new Intent().setClassName(view.getContext(),
				"com.ui.VideoPlayerActivity");
		Bundle bundle = new Bundle();
		bundle.putString(VideoPlayerActivity.KEY_VIDEO_PATH, path);
		bundle.putInt(VideoPlayerActivity.KEY_VIDEO_TYPE,
				VideoPlayerActivity.TYPE_VIDEO_ADVERTISEMENT);
		intent.putExtras(bundle);
		view.getContext().startActivity((intent));
	}

	void jumpToOffice(WebView view, String url) {
		String path = url.substring(url.indexOf("(") + 1, url.lastIndexOf(")"));
		path = Const.getHtmlDirUri() + path;
		Log.v(TAG, "jumpToOffice:" + path);
		view.getContext().startActivity(
				new Intent(Intent.ACTION_VIEW, Uri.parse(path)));
	}

	void jumpToVideo(WebView view, String url) {
		// "app://playvideo(res/demo.mkv, 1)"
		String path = url.substring(url.indexOf("(") + 1, url.lastIndexOf(")"));
		path = Const.getHtmlDirPath() + path;

		if (path.contains(",")) {
			path = path.substring(0, path.indexOf(","));
		}
		Log.v(TAG, "jumpToAd:" + path);
		Intent intent = new Intent().setClassName(view.getContext(),
				"com.ui.VideoPlayerActivity");
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
		// "app://update(1.0.0)"
		if (AppState.isNetworkConnected(view.getContext()) == false) {
			Log.v(TAG, "jumpToUpdate:net is not valid");
			Toast.makeText(view.getContext(), "net is not valid",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mActivity.mConnectingDialog = new ConnectingNetDialog(mActivity);
		mActivity.mConnectingDialog.getUpdateInfo();
		mActivity.mConnectingDialog.show();

	}
}