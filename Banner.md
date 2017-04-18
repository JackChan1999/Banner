```java
	private class LunBoTask extends Handler implements Runnable{

		public void stopLunbo(){
			//移除当前所有的任务
			removeCallbacksAndMessages(null);
		}
		public void startLunbo(){
			stopLunbo();
			postDelayed(this, 2000);
		}
		@Override
		public void run() {
			//控制轮播图的显示
			vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1) % vp_lunbo.getAdapter().getCount());
			postDelayed(this, 2000);
		}
		
	}
```

```java
private class LunBoAdapter extends PagerAdapter{

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv_lunbo_pic = new ImageView(mainActivity);
			iv_lunbo_pic.setScaleType(ScaleType.FIT_XY);
			
			//设备默认的图片,网络缓慢
			iv_lunbo_pic.setImageResource(R.drawable.home_scroll_default);
			
			
			//给图片添加数据
			TPINewsData_Data_LunBoData tpiNewsData_Data_LunBoData = lunboDatas.get(position);
			
			//图片的url
			String topimageUrl = tpiNewsData_Data_LunBoData.topimage;
			
			
			//把url的图片给iv_lunbo_pic
			//异步加载图片，并且显示到组件中
			bitmapUtils.display(iv_lunbo_pic, topimageUrl);
			
			//给图片添加触摸事件
			iv_lunbo_pic.setOnTouchListener(new OnTouchListener() {
				
				private float	downX;
				private float	downY;
				private long	downTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN://按下停止轮播
						System.out.println("按下");
						downX = event.getX();
						downY = event.getY();
						downTime = System.currentTimeMillis();
						lunboTask.stopLunbo();
						break;
					case MotionEvent.ACTION_CANCEL://事件取消
						lunboTask.startLunbo();
						break;
					case MotionEvent.ACTION_UP://松开
						float upX = event.getX();
						float upY = event.getY();
						
						if (upX == downX && upY == downY) {
							long upTime = System.currentTimeMillis();
							if (upTime - downTime < 500) {
								//点击
								lunboPicClick("被单击了。。。。。");
							}
						}
						System.out.println("松开");
						lunboTask.startLunbo();//开始轮播
						break;
					default:
						break;
					}
					return true;
				}

				private void lunboPicClick(Object data) {
					//处理图片的点击事件
					System.out.println(data);
					
				}
			});
			container.addView(iv_lunbo_pic);
			
			return iv_lunbo_pic;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return lunboDatas.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
```