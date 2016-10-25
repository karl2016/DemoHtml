package com.tool;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public abstract class HttpTool {
	public final String TAG = "HttpTool";
	static public final int MSG_EXCEPTION = 0;
	static public final int MSG_GET_SUCCESS = 1;
	abstract public void messageNotify(int msg, Object obj);

	public void getUpdateInfoByHttp() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String serverXml;
				try {
					serverXml = getHttpTextByUri(Const.getNetUri());

					String url = XmlTool.readRealUrl(serverXml);
					Log.v(TAG, url);
					String updateInfoXml = getHttpTextByUri(url);
					UpdateInfo info = XmlTool.readUpdateInfo(updateInfoXml);
					info.sort();
					messageNotify(MSG_GET_SUCCESS, (Object) info);

				} catch (Exception e) {
					e.printStackTrace();
					Log.v(TAG, e.getMessage());
					messageNotify(MSG_EXCEPTION, (Object) e.getMessage());

				}

			}
		}).start();

	}

	private String getHttpTextByUri(String serverURL) throws Exception {
		if (serverURL == null)
			return null;

		HttpGet httpRequest = new HttpGet(serverURL);
		HttpResponse httpResponse = new DefaultHttpClient()
				.execute(httpRequest);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResponse.getEntity());
			Log.v(TAG, result);
			return result;
		} else {
			String exception = "Á¬½Ó"
					+ serverURL
					+ "Ê§°Ü \n"
					+ "´íÎóÂë:"
					+ Integer.toString(httpResponse.getStatusLine()
							.getStatusCode());
			Log.v(TAG, exception);
			throw new Exception(exception);
		}
	}

}
