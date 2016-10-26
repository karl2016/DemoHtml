package com.ui;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.karl.demohtml.R;

public class VideoPlayerActivity extends Activity {
	private final String WARNING_NO_AD_MP4 = " do not have video files";
	private final String TAG = "VideoPlayerActivity";
	static public final int TYPE_VIDEO_ADVERTISEMENT = 1;
	static public final int TYPE_VIDEO_FILE = 2;
	static public final String KEY_VIDEO_TYPE = "type";
	static public final String KEY_VIDEO_PATH = "path";
	String mPath;
	Activity mActivity;
	VideoView mVideoView;
	Vector<String> mVideoFilesVector;
	int mAdIndex;
	int mVideoType;
	MediaController mMediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();

		mPath = bundle.getString(KEY_VIDEO_PATH);
		Log.v(TAG, "ad path:" + mPath);
		mVideoType = bundle.getInt(KEY_VIDEO_TYPE);
		mActivity = this;

		mAdIndex = 0;

		setContentView(R.layout.activity_video);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mVideoView = (VideoView) findViewById(R.id.videoView);

		if (mVideoType == TYPE_VIDEO_ADVERTISEMENT) {
			mVideoFilesVector = GetVideoFileNameByDir(mPath);
			mMediaController = new MediaController(this);
			mMediaController.setPrevNextListeners(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					{
						mAdIndex = (mAdIndex + mVideoFilesVector.size() - 1)
								% (mVideoFilesVector.size());
						String filePath = (String) mVideoFilesVector
								.get(mAdIndex);
						mVideoView.setVideoURI(Uri.parse(filePath));
						mVideoView.start();
					}
				}
			}, new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mVideoFilesVector.size() > 0) {
						mAdIndex = (mAdIndex + 1) % (mVideoFilesVector.size());
						String filePath = (String) mVideoFilesVector
								.get(mAdIndex);
						mVideoView.setVideoURI(Uri.parse(filePath));
						mVideoView.start();
					}

				}
			});
		} else if (mVideoType == TYPE_VIDEO_FILE) {
			mVideoFilesVector = GetVideoFileNameByPath(mPath);
			mMediaController = new MediaController(this);
		}

		mMediaController.setMediaPlayer(mVideoView);
		mVideoView.setMediaController(mMediaController);
		mVideoView.requestFocus();
		mMediaController.setAnchorView(mVideoView);

		mVideoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (mVideoType == TYPE_VIDEO_ADVERTISEMENT) {
							// loop play video files
							if (mVideoFilesVector.size() > 0) {
								mAdIndex = (mAdIndex + 1)
										% (mVideoFilesVector.size());
								String filePath = (String) mVideoFilesVector
										.get(mAdIndex);
								mVideoView.setVideoURI(Uri.parse(filePath));
								mVideoView.start();
							}
						} else if (mVideoType == TYPE_VIDEO_FILE) {
							// close activity end of the play
							mActivity.finish();
						}
					}
				});

		if (mVideoFilesVector.size() > 0) {
			mVideoView.setVisibility(View.VISIBLE);
			String filePath = (String) mVideoFilesVector.get(mAdIndex);
			mVideoView.setVideoPath(filePath);

			mVideoView.start();
		} else {
			mActivity.finish();
			Toast.makeText(mActivity, mPath + WARNING_NO_AD_MP4,
					Toast.LENGTH_SHORT).show();
		}
	}

	public Vector<String> GetVideoFileNameByDir(String fileAbsolutePath) {

		Log.v(TAG, fileAbsolutePath);
		Vector<String> vecFile = new Vector<String>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();

		for (int i = 0; subFile != null && i < subFile.length; i++) {
			if (!subFile[i].isDirectory()) {
				String filename = subFile[i].getName();
				// vector only add files which suffix is mp4 or mkv
				if (filename.trim().toLowerCase().endsWith(".mp4")
						|| filename.trim().toLowerCase().endsWith(".mkv")) {
					vecFile.add(fileAbsolutePath + "/" + filename);
					Log.v(TAG, "video index=" + i + " file name=" + filename);
				}
			}
		}
		return vecFile;
	}

	public Vector<String> GetVideoFileNameByPath(String fileAbsolutePath) {
		Vector<String> vecFile = new Vector<String>();
		File file = new File(fileAbsolutePath);
		if (file.exists()) {
			vecFile.add(fileAbsolutePath);
			Log.v(TAG, "video file name=" + fileAbsolutePath);
		}

		return vecFile;
	}
}