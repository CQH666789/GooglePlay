package com.c.googleplay74.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.c.googleplay74.R;
import com.c.googleplay74.domain.AppInfo;
import com.c.googleplay74.domain.DownloadInfo;
import com.c.googleplay74.http.HttpHelper;
import com.c.googleplay74.manager.DownloadManager;
import com.c.googleplay74.manager.DownloadManager.DownloadObserver;
import com.c.googleplay74.ui.view.ProgressArc;
import com.c.googleplay74.utils.BitmapHelper;
import com.c.googleplay74.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 首页holder
 */
public class HomeHolder extends BaseHolder<AppInfo> implements DownloadObserver, OnClickListener {

	private DownloadManager mDM;

	private TextView tvName, tvSize, tvDes;
	private ImageView ivIcon;
	private RatingBar rbStar;

	private BitmapUtils mBitmapUtils;

	private ProgressArc pbProgress;

	private int mCurrentState;
	private float mProgress;

	private TextView tvDownload;

	@Override
	public View initView() {
		// 1. 加载布局
		View view = UIUtils.inflate(R.layout.list_item_home);
		// 2. 初始化控件
		tvName = (TextView) view.findViewById(R.id.tv_name);
		tvSize = (TextView) view.findViewById(R.id.tv_size);
		tvDes = (TextView) view.findViewById(R.id.tv_des);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		rbStar = (RatingBar) view.findViewById(R.id.rb_star);

		tvDownload = (TextView) view.findViewById(R.id.tv_download);

		// mBitmapUtils = new BitmapUtils(UIUtils.getContext());
		mBitmapUtils = BitmapHelper.getBitmapUtils();

		// 初始化进度条
		FrameLayout flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
		flProgress.setOnClickListener(this);

		pbProgress = new ProgressArc(UIUtils.getContext());
		// 设置圆形进度条直径
		pbProgress.setArcDiameter(UIUtils.dip2px(26));
		// 设置进度条颜色
		pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
		// 设置进度条宽高布局参数
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(UIUtils.dip2px(27), UIUtils.dip2px(27));
		flProgress.addView(pbProgress, params);

		// pbProgress.setOnClickListener(this);

		mDM = DownloadManager.getInstance();
		mDM.registerObserver(this);// 注册观察者, 监听状态和进度变化

		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
		tvName.setText(data.name);
		tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
		tvDes.setText(data.des);
		rbStar.setRating(data.stars);

		mBitmapUtils.display(ivIcon, HttpHelper.URL + "image?name="
				+ data.iconUrl);

		// 判断当前应用是否下载过
		DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
		if (downloadInfo != null) {
			// 之前下载过
			mCurrentState = downloadInfo.currentState;
			mProgress = downloadInfo.getProgress();
		} else {
			// 没有下载过
			mCurrentState = DownloadManager.STATE_UNDO;
			mProgress = 0;
		}

		refreshUI(mCurrentState, mProgress, data.id);
	}

	/**
	 * 刷新界面
	 * 
	 * @param progress
	 * @param state
	 */
	private void refreshUI(int state, float progress, String id) {
		// 由于listview重用机制, 要确保刷新之前, 确实是同一个应用
		if (!getData().id.equals(id)) {
			return;
		}

		mCurrentState = state;
		mProgress = progress;
		switch (state) {
		case DownloadManager.STATE_UNDO:
			// 自定义进度条背景
			pbProgress.setBackgroundResource(R.drawable.ic_download);
			// 没有进度
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload.setText("下载");
			break;
		case DownloadManager.STATE_WAITING:
			pbProgress.setBackgroundResource(R.drawable.ic_download);
			// 等待模式
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);
			tvDownload.setText("等待");
			break;
		case DownloadManager.STATE_DOWNLOADING:
			pbProgress.setBackgroundResource(R.drawable.ic_pause);
			// 下载中模式
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
			pbProgress.setProgress(progress, true);
			tvDownload.setText((int) (progress * 100) + "%");
			break;
		case DownloadManager.STATE_PAUSE:
			pbProgress.setBackgroundResource(R.drawable.ic_resume);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			break;
		case DownloadManager.STATE_ERROR:
			pbProgress.setBackgroundResource(R.drawable.ic_redownload);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload.setText("下载失败");
			break;
		case DownloadManager.STATE_SUCCESS:
			pbProgress.setBackgroundResource(R.drawable.ic_install);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload.setText("安装");
			break;

		default:
			break;
		}
	}

	// 主线程更新ui 3-4
	private void refreshUIOnMainThread(final DownloadInfo info) {
		// 判断下载对象是否是当前应用
		AppInfo appInfo = getData();
		if (appInfo.id.equals(info.id)) {
			UIUtils.runOnUIThread(new Runnable() {

				@Override
				public void run() {
					refreshUI(info.currentState, info.getProgress(), info.id);
				}
			});
		}
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo info) {
		refreshUIOnMainThread(info);
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo info) {
		refreshUIOnMainThread(info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_progress:
			// 根据当前状态来决定下一步操作
			if (mCurrentState == DownloadManager.STATE_UNDO
					|| mCurrentState == DownloadManager.STATE_ERROR
					|| mCurrentState == DownloadManager.STATE_PAUSE) {
				mDM.download(getData());// 开始下载
			} else if (mCurrentState == DownloadManager.STATE_DOWNLOADING
					|| mCurrentState == DownloadManager.STATE_WAITING) {
				mDM.pause(getData());// 暂停下载
			} else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
				mDM.install(getData());// 开始安装
			}

			break;

		default:
			break;
		}
	}

}
