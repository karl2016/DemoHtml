package com.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.karl.demohtml.R;

public class MyMediaController extends android.widget.MediaController {

	private Activity mActivity;

	private View mView;
	private Button mButton;
	public MyMediaController(Activity activity) {
		super(activity);
		mActivity = activity;
	}

	@Override
	public void setAnchorView(View view) {

		super.setAnchorView(view);
		if (mView == null) {
			mView = LayoutInflater.from(getContext()).inflate(
					R.layout.mediacontroler_button, null);
			mButton = (Button) mView.findViewById(R.id.button_close);
			mView.setVisibility(View.INVISIBLE);
			((ViewGroup) mActivity.findViewById(android.R.id.content))
			.addView(mView);
			mButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mActivity.finish();
				}
			});
		}
	}

	@Override
	public void show(int timeout) {
		super.show(timeout);
		mView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		super.hide();
		mView.setVisibility(View.INVISIBLE);
	}

}
