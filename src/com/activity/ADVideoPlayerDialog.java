package com.activity;

import java.io.File;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.karl.demohtml.R;


public class ADVideoPlayerDialog extends Dialog { 
	private final String WARNING_NO_AD_MP4=" do not have mp4 files";
	private final String TAG = "demohtml";
	String AD_FILE_PATH;
	Dialog mDialog;
    VideoView mVideoView;
    Vector mAdFiles;
    int mAdIndex;
	public ADVideoPlayerDialog(Context context) {
		super(context, R.style.fullscreen_dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        
		mDialog = this; 
		
		mAdIndex = 0;
		AD_FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/demohtml";
		mAdFiles = GetVideoFileName(AD_FILE_PATH);
		
        setContentView(R.layout.activity_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mVideoView = (VideoView)findViewById(R.id.videoView);
 
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				Button button = (Button) findViewById(R.id.button);
				button.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mDialog.dismiss();
					}
				});
			}
		});
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  

            @Override  
            public void onCompletion(MediaPlayer mp) {  
            	if (mAdFiles.size() > 0)
            	{
            		mAdIndex = (mAdIndex + 1) %(mAdFiles.size());
            		String filePath = (String)mAdFiles.get(mAdIndex);
                	mVideoView.setVideoURI(Uri.parse(filePath));  
                	mVideoView.start();
            	}

            }  
        });  

    	if (mAdFiles.size() > 0)
    	{
            mVideoView.setVisibility(View.VISIBLE);
    		String filePath = (String)mAdFiles.get(mAdIndex);
        	mVideoView.setVideoURI(Uri.parse(filePath)); 
    		mVideoView.start();
    	}
    	else
    	{
			mDialog.dismiss();
			Toast.makeText(context, AD_FILE_PATH+ WARNING_NO_AD_MP4, Toast.LENGTH_SHORT);
    	}
	}

	public Vector GetVideoFileName(String fileAbsolutePath) {

		Log.v(TAG, fileAbsolutePath);
		Vector vecFile = new Vector();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();

		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				String filename = subFile[iFileLength].getName();
				// 判断是否为MP4结尾
				if (filename.trim().toLowerCase().endsWith(".mp4")) {
					vecFile.add(fileAbsolutePath + "/" + filename);
					Log.v(TAG, filename);
				}
			}
		}

		return vecFile;
	}
	
}

