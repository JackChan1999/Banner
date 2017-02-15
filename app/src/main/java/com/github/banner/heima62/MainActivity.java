package com.github.banner.heima62;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.github.banner.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnPageChangeListener {

	private static final String TAG = "MainActivity";
	private ViewPager mPager;
	private LinearLayout mPointContainer;
	private TextView mTvTitle;

	private List<ImageView> mListDatas;

	int[] imgs = { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e };
	String[] titles = { "为梦想坚持", "我相信我是黑马", "黑马公开课", "google IO", "轻松1w+" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPointContainer = (LinearLayout) findViewById(R.id.point_container);
		mTvTitle = (TextView) findViewById(R.id.tv_title);

		// ListView --> setAdapter() --》 adapter --->集合List<数据>

		// 初始化数据

		mListDatas = new ArrayList<ImageView>();
		for (int i = 0; i < imgs.length; i++) {
			// 给集合添加ImageView
			ImageView iv = new ImageView(this);
			iv.setImageResource(imgs[i]);
			iv.setScaleType(ScaleType.FIT_XY);

			mListDatas.add(iv);

			// 添加点
			View point = new View(this);
			point.setBackgroundResource(R.drawable.point_normal);
			LayoutParams params = new LayoutParams(10, 10);
			if (i != 0) {
				params.leftMargin = 10;
			} else {
				point.setBackgroundResource(R.drawable.point_selected);

				mTvTitle.setText(titles[i]);
			}
			mPointContainer.addView(point, params);
		}

		// 设置数据的方式
		mPager.setAdapter(new MyAdapter());

		// 设置监听器
		mPager.setOnPageChangeListener(this);

		// 设置默认选中中间的item
		int middle = Integer.MAX_VALUE / 2;
		int extra = middle % mListDatas.size();
		int item = middle - extra;
		mPager.setCurrentItem(item);

	}

	class MyAdapter extends PagerAdapter {

		// 页面的数量
		@Override
		public int getCount() {
			if (mListDatas != null) {
				// return mListDatas.size();
				return Integer.MAX_VALUE;
			}
			return 0;
		}

		// 标记方法，用来判断缓存标记
		@Override
		public boolean isViewFromObject(View view, Object object) {
			// view:显示的view
			// object: 标记
			return view == object;
		}

		// 初始化item
		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			position = position % mListDatas.size();

			// position： 要加载的位置
			ImageView iv = mListDatas.get(position);

			// 用来添加要显示的View的
			mPager.addView(iv);

			// 记录缓存标记--return 标记
			return iv;
		}

		// 销毁item条目
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 销毁移除item
			// object:标记

			position = position % mListDatas.size();

			ImageView iv = mListDatas.get(position);
			mPager.removeView(iv);
		}
	}

	// 回调方法,当viewpager滚动时的回调
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

		// position: 当前选中的位置
		// positionOffset: 滑动的百分比
		// positionOffsetPixels: 偏移的距离,滑动的像素

		// Log.d(TAG, "onPageScrolled : " + positionOffsetPixels + "  "
		// + positionOffset);
	}

	// 回调方法,当viewpager的某个页面选中时的回调
	@Override
	public void onPageSelected(int position) {
		// Log.d(TAG, "onPageSelected : " + position);

		position = position % mListDatas.size();

		// 设置选中的点的样式
		int count = mPointContainer.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mPointContainer.getChildAt(i);

			// if (position == i) {
			// view.setBackgroundResource(R.drawable.point_selected);
			// } else {
			// view.setBackgroundResource(R.drawable.point_normal);
			// }
			view.setBackgroundResource(position == i ? R.drawable.point_selected
					: R.drawable.point_normal);
		}

		mTvTitle.setText(titles[position]);

	}

	// 回调方法,当viewpager的滑动状态改变时的回调
	// * @see ViewPager#SCROLL_STATE_IDLE : 闲置状态
	// * @see ViewPager#SCROLL_STATE_DRAGGING :拖动状态
	// * @see ViewPager#SCROLL_STATE_SETTLING: 固定状态
	@Override
	public void onPageScrollStateChanged(int state) {
		// Log.d(TAG, "onPageScrollStateChanged : " + state);
	}

}
