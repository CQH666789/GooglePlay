package com.c.googleplay74.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.c.googleplay74.ui.view.LoadingPage.ResultState;
import com.c.googleplay74.utils.UIUtils;

/**
 * 游戏
 * @author Kevin
 * @date 2015-10-27
 */
public class GameFragment extends BaseFragment {

	@Override
	public View onCreateSuccessView() {
		TextView view = new TextView(UIUtils.getContext());
		view.setText("GameFragment");
		return view;
	}

	@Override
	public ResultState onLoad() {
		return ResultState.STATE_SUCCESS;
	}

}
