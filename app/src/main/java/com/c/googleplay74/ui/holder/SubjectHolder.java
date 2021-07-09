package com.c.googleplay74.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.c.googleplay74.R;
import com.c.googleplay74.domain.SubjectInfo;
import com.c.googleplay74.http.HttpHelper;
import com.c.googleplay74.utils.BitmapHelper;
import com.c.googleplay74.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 专题holder
 */
public class SubjectHolder extends BaseHolder<SubjectInfo> {

	private ImageView ivPic;
	private TextView tvTitle;

	private BitmapUtils mBitmapUtils;

	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.list_item_subject);
		ivPic = (ImageView) view.findViewById(R.id.iv_pic);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	@Override
	public void refreshView(SubjectInfo data) {
		tvTitle.setText(data.des);
		mBitmapUtils.display(ivPic, HttpHelper.URL + "image?name=" + data.url);
	}

}
