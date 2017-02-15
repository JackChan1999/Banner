package com.github.banner.heima56;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.github.banner.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private ViewPager viewPager;
	private TextView tv_msg;
	private LinearLayout ll_point_group;
	//图片资源
	private int[] ids = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e };

	// 图片标题集合
	private final String[] imageDescriptions = {
			"巩俐不低俗，我就不能低俗",
			"朴树又回来啦！再唱经典老歌引万人大合唱",
			"揭秘北京电影如何升级",
			"乐视网TV版大派送",
			"热血屌丝的反杀" };

	private List<ImageView> imageViews;

	/**
	 * 上一次被高亮显示的点
	 */
	private int lastPointIndex;


	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			//自动跳转到下一个页面
			viewPager.setCurrentItem(viewPager.getCurrentItem()+1);

			if(!isDestroyed){
				handler.sendEmptyMessageDelayed(0, 2000);
			}


		};
	};

	/**
	 * 判断Activity是否已经销毁
	 * true:已经销毁
	 * false:没有销毁
	 */
	private boolean isDestroyed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		tv_msg = (TextView) findViewById(R.id.tv_msg);
		ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);

		//加载数据
		imageViews = new ArrayList<ImageView>();
		for(int i=0;i<ids.length;i++){
			ImageView iv = new ImageView(this);
			iv.setBackgroundResource(ids[i]);

			//加载图片
			imageViews.add(iv);


			//加指示点
			ImageView iv_point = new ImageView(this);
			//包裹类型
			//导入的包，一定要是改空间的父类的参数
			//设置大小可以在：LayoutParams里面设置和shape资源里面设置
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, -2);
			params.leftMargin = 15;//设置距离
			iv_point.setLayoutParams(params );
			iv_point.setBackgroundResource(R.drawable.point_selector);
			if(i==0){
				//设置高亮
				iv_point.setEnabled(true);
			}else{
				//设置默认
				iv_point.setEnabled(false);
			}

			//添加指示点
			ll_point_group.addView(iv_point);


		}

		// 设置适配器
		viewPager.setAdapter(new MyPagerAdapter());

		//设置中间位置
		int item = Integer.MAX_VALUE/2  - Integer.MAX_VALUE/2%imageViews.size();
		//11  -  1 = 10;

		//22 相除后Integer.MAX_VALUE/2 ==11   11%imageViews.size() ==1

		viewPager.setCurrentItem(item);

		tv_msg.setText(imageDescriptions[item%imageViews.size()]);




		//监听页面改变的方法
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			/**
			 * 当某个页面选中的时候回调
			 * 把当前选中的下标回传
			 */
			@Override
			public void onPageSelected(int position) {
				int newIndex = position % imageViews.size();
				// TODO Auto-generated method stub
				tv_msg.setText(imageDescriptions[newIndex]);

				//当前下标点高亮
				ll_point_group.getChildAt(newIndex).setEnabled(true);

				//上一次高亮显示的变成默认
				ll_point_group.getChildAt(lastPointIndex).setEnabled(false);
				lastPointIndex = newIndex;



			}

			/**
			 * 当某个页面已经滚动了的时候
			 * position ： 位置
			 * positionOffset：滑动页面的比值：0~1；
			 * positionOffsetPixels：当前滑动了多少个像素
			 *
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
									   int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			/**
			 * 当页面状态发生改变的时候回调
			 * 静止-滑动
			 * 滑动-进制
			 */
			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		isDestroyed = false;
		//发消息开始滚动
		handler.sendEmptyMessageDelayed(0, 2000);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroyed = true;
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {

			return Integer.MAX_VALUE;//imageViews.size();//5
		}

		/**
		 * 功能相当于getView();
		 * container:就是Viewpager,容器作用
		 * position：页面的位置
		 * 默认实例化3条
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = imageViews.get(position % imageViews.size());
			//把View添加到容器中
			container.addView(iv);
			return iv;
		}

		/**
		 * 当前页面和instantiateItem返回的值
		 * view:当前页面
		 * object：就是instantiateItem方法返回的值
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			//			if(view == object){
			//				return true;
			//			}else{
			//				return false;
			//			}
			return view == object;
		}

		/**
		 * container:Viewpager，容器
		 * position：那个页面改移除了
		 * object：要移除的页面
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//			super.destroyItem(container, position, object);
			container.removeView((View)object);

		}

	}

}
