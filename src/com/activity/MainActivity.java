package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.karl.demohtml.R;

public class MainActivity extends Activity {
  
	private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر�����
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webView1);  

        myWebView.setWebViewClient(new MyWebViewClient());

        myWebView.getSettings().setJavaScriptEnabled(true);  
        // ���ÿ���֧������ 
        myWebView.getSettings().setSupportZoom(true);  
        myWebView.getSettings().setDisplayZoomControls(false);
        // ���ó������Ź��� 
        myWebView.getSettings().setBuiltInZoomControls(true);
       //�������������
        myWebView.getSettings().setUseWideViewPort(true);
        //����Ӧ��Ļ
        myWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
  //      myWebView.getSettings().setLoadWithOverviewMode(true);

        // load html file in app 
        //myWebView.loadUrl("file:///android_asset/zhenhua/index.html"); 

        // load html file in sdcard 
        myWebView.loadUrl("file:///storage/sdcard0/demohtml/index.html");
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) { 
            Log.v("demohtml", "goback");
            myWebView.goBack(); 
    		return true; 
    	} 
    		return	super.onKeyDown(keyCode, event); 
    	} 

    class MyWebViewClient extends WebViewClient{   	
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

        	if (url.endsWith(".html"))
            {
        		view.loadUrl(url);
                return true;
            }
        	
        	boolean isMp4= url.endsWith(".mp4");
        	if (url.endsWith(".mp4"))
            {

                view.getContext().startActivity((new Intent().setClassName(view.getContext(), "com.example.DemoHtml.ADVideoPlayerActivity"))); 
//        		new ADVideoPlayerDialog(view.getContext()).show();
                return true;
            }
        	
        	if (url.endsWith(".ppt") || url.endsWith(".xsl") || url.endsWith(".doc") || url.endsWith(".docx"))
            {
        		Log.v("demohtml", "ppt:"+url);
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); 
                return true;
            }
            return true;
       }
    }

}
