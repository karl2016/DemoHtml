package com.tool;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.activity.MainActivity;

public class HttpTool {
	public final String TAG = "HttpTool";
	MainActivity mainActivity;
	long mDownloadId;
	String mDownloadFileName;
	public void getUpdateInfoByHttp(MainActivity activity) {
		mainActivity = activity;
		new Thread(new Runnable() {
			@Override
			public void run() {

				String serverXml;
				try {
					serverXml = getHttpTextByUri(Const.getNetUri());
	
					String url = XmlParser.readRealUrl(serverXml);
					Log.v(TAG, url);
					String updateInfoXml = getHttpTextByUri(url);
					UpdateInfo info = XmlParser.readUpdateInfo(updateInfoXml);
					info.sort();
					mainActivity.mUpdateInfo = info;
					Message msg = mainActivity.mHandler.obtainMessage();
					Bundle b = new Bundle();
			        b.putString(Const.HANDLER_KEY_TYPE, Const.HANDLER_VALUE_UPDATEINFO);
			        msg.setData(b);
					mainActivity.mHandler.sendMessage(msg);
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.v(TAG, e.getMessage());
					Message msg = mainActivity.mHandler.obtainMessage();
					Bundle b = new Bundle();
			        b.putString(Const.HANDLER_KEY_TYPE, Const.HANDLER_VALUE_EXCEPTION);
			        b.putString(Const.HANDLER_KEY_EXCEPTION, e.getMessage());
			        msg.setData(b);
					mainActivity.mHandler.sendMessage(msg);
				}

			}
		}).start();

	}

	private String getHttpTextByUri(String serverURL) throws Exception  {
		if (serverURL == null)
			return null;

		HttpGet httpRequest = new HttpGet(serverURL);
		HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResponse.getEntity());
			Log.v(TAG, result);
			return result;
		}
		else
		{
			String exception = "HttpGet:"+ serverURL + " failed,response=" + Integer.toString(httpResponse.getStatusLine().getStatusCode());
			Log.v(TAG, exception);
			throw new Exception(exception);
		}
	}
	

	public void downloadFiles(Context context, String uri, String name)
	{
		Log.v(TAG, uri + " " +  Environment.DIRECTORY_DOWNLOADS +"  " + name);
		mDownloadFileName = name;
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		request.setTitle("demohtml");
	    request.setDescription("apk downloading");
	   // request.setAllowedOverRoaming(false); 
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
		Log.v(TAG, Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS).getPath());
//		request.setDestinationInExternalPublicDir(Const.getHtmlDirPath(), name);
		DownloadManager downManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		mDownloadId = downManager.enqueue(request);
		
	}
	public void cancelDownload(Context context){
		DownloadManager downManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		downManager.remove(mDownloadId);
	}
	public String getDownloadFilePath(){
		return Const.getDownloadDirUri() + mDownloadFileName;
	}
	static public boolean isNetworkConnected(Context context) {    
	    if (context != null) {    
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context    
	                .getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();    
	        if (mNetworkInfo != null) {    
	            return mNetworkInfo.isAvailable();    
	        }    
	    }    
	    return false;    
	}  

}
