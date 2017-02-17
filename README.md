# Banner 轮播图，广告栏控件

##一、项目概述
这里，我们使用自定义组合控件实现一个自动轮播的广告条，也叫轮播图，完整版的效果图如下图所示。其实，这就是我们经常见到的滚动广告，默认情况下每隔N 秒会自动滚动，用手指左右滑动时也会切换到上一张或者下一张。当界面切换时，对应广告图片的标题也会随着改变，并且还有对应图片索引的点也会被选中变为红色。此处，实现的核心控件是ViewPager，它是Android3.0 版本加入的新控件，为了向下兼容，谷歌给我们提供了android-support-v4.jar 包。

![这里写图片描述](http://img.blog.csdn.net/20161014212221565)

##二、轮播图UI布局
布局整体采用RelativeLayout，android.support.v4.view.ViewPager，TextView，LinearLayout 配合使用布局文件activity_main.xml 的代码如下文件所示：

```java
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <android.support.v4.view.ViewPager
            android:id="@+id/vp_pager"
            android:layout_width="match_parent"
            android:layout_height="160dp"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="#a000"
            android:orientation="vertical"
            android:padding="5dp">

            <TextView
                android:id="@+id/tv_title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="新闻标题"
                android:textColor="#fff"
                android:textSize="16sp"/>

            <LinearLayout
                android:id="@+id/ll_container"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_horizontal"
                android:orientation="horizontal">
            </LinearLayout>
        </LinearLayout>
    </FrameLayout>
</RelativeLayout>
```
##轮播图的代码逻辑实现

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <android.support.v4.view.ViewPager
            android:id="@id/viewpager"
            android:layout_width="match_parent"
            android:layout_height="180dp"/>
        <!--android:background="#a000"-->
        <LinearLayout
            android:id="@id/content"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:orientation="vertical"
            android:padding="5dp">

            <TextView
                android:id="@id/tvTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:textColor="#fff"
                android:textSize="16sp"/>

            <LinearLayout
                android:id="@id/llcontainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="3dp"
                android:gravity="center_horizontal"
                android:orientation="horizontal"/>

        </LinearLayout>
    </FrameLayout>

    <android.support.v7.widget.RecyclerView
        android:id="@id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</LinearLayout>

```


```java
public class BannerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    @Bind(R.id.viewpager)
    public ViewPager mViewPager;
    @Bind(R.id.tvTitle)
    public TextView mTextView;
    @Bind(R.id.llcontainer)
    public LinearLayout mContainer;
    @Bind(R.id.content)
    public LinearLayout mContent;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private int mPreviousPos;
    private int[] imgs = new int[]{R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e};
    private String[] title;
    private ArrayList<String> transformerList = new ArrayList<>();
    private RvAdapter mRvAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(++currentItem);
            mHandler.sendEmptyMessageDelayed(0, 5000);
        }
    };
    private ViewPagerScroller mScroller;
    private ViewPagerScroller scroller;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initRecyclerView();
        initListener();
        initTransformer();
        initViewPagerScroll();
    }

    private void initView() {
        setContentView(R.layout.activity_banner);
        ButterKnife.bind(this);
        title = UIUtil.getStringArray(R.array.title);

        SpannableString actionBarTitle = new SpannableString("大图轮播");
        actionBarTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, actionBarTitle.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(actionBarTitle);

        mViewPager.setAdapter(new Adapter());
        int middle = Integer.MAX_VALUE / 2;
        int extra = middle % imgs.length;
        int currentItem = middle - extra;
        mViewPager.setCurrentItem(currentItem);
        mHandler.sendEmptyMessageDelayed(0, 5000);

        for (int i = 0; i < imgs.length; i++) {
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                params.leftMargin = 10;
                img.setEnabled(false);
            }
            img.setLayoutParams(params);
            mContainer.addView(img);
        }
        mTextView.setText(title[0]);

       mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BannerActivity.this.onPageSelected(0);
                mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRvAdapter = new RvAdapter(this, R.layout.list_item, transformerList);
        mRecyclerView.setAdapter(mRvAdapter);
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(this);
        /*new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int pos = position % imgs.length;
                mTextView.setText(title[pos]);
                mContainer.getChildAt(pos).setEnabled(true);
                mContainer.getChildAt(mPreviousPos).setEnabled(false);
                mPreviousPos = pos;
                colorChange(pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        }*/

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.sendEmptyMessageDelayed(0, 5000);
                        break;
                }
                return false;
            }
        });

        mRvAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                String transforemerName = transformerList.get(position);
                try {
                    Class clazz = Class.forName("com.ToxicBakery.viewpager.transforms." +
                            transforemerName);
                    ABaseTransformer transformer = (ABaseTransformer) clazz.newInstance();
                    mViewPager.setPageTransformer(true, transformer);
                    if (transforemerName.equals("StackTransformer")) {
                        scroller.setScrollDuration(1200);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        Field mScroller = null;
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(mViewPager.getContext());
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initTransformer() {
        //各种翻页效果
        transformerList.add(DefaultTransformer.class.getSimpleName());
        transformerList.add(AccordionTransformer.class.getSimpleName());
        transformerList.add(BackgroundToForegroundTransformer.class.getSimpleName());
        transformerList.add(CubeInTransformer.class.getSimpleName());
        transformerList.add(CubeOutTransformer.class.getSimpleName());
        transformerList.add(DepthPageTransformer.class.getSimpleName());
        transformerList.add(FlipHorizontalTransformer.class.getSimpleName());
        transformerList.add(FlipVerticalTransformer.class.getSimpleName());
        transformerList.add(ForegroundToBackgroundTransformer.class.getSimpleName());
        transformerList.add(RotateDownTransformer.class.getSimpleName());
        transformerList.add(RotateUpTransformer.class.getSimpleName());
        transformerList.add(StackTransformer.class.getSimpleName());
        transformerList.add(ZoomInTransformer.class.getSimpleName());
        transformerList.add(ZoomOutTranformer.class.getSimpleName());

        mRvAdapter.notifyDataSetChanged();
    }

    private void colorChange(int pos) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs[pos]);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                mContent.setBackgroundColor(toARGB(vibrant.getRgb()));
                mTextView.setTextColor(vibrant.getBodyTextColor());
                mActionBar.setBackgroundDrawable(new ColorDrawable(vibrant.getRgb()));

                if (Build.VERSION.SDK_INT >=21){
                    Window window = getWindow();
                    window.setStatusBarColor(colorBurn(vibrant.getRgb()));
                    //window.setNavigationBarColor(Color.RED);
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int pos = position % imgs.length;
        mTextView.setText(title[pos]);
        mContainer.getChildAt(pos).setEnabled(true);
        if (pos != mPreviousPos){
            mContainer.getChildAt(mPreviousPos).setEnabled(false);
        }
        mPreviousPos = pos;
        colorChange(pos);
    }

    /**
     * 颜色添加透明度
     * @param rgb
     * @return
     */
    protected  static  int toARGB(int rgb){
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return Color.argb(180 , red , green , blue );
    }

    /**
     * 颜色加深处理
     * @param RGBValues
     * @return
     */
    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class Adapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (imgs.length != 0) {
                return Integer.MAX_VALUE;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position % imgs.length;
            ImageView img = new ImageView(BannerActivity.this);
            img.setBackgroundResource(imgs[pos]);
            container.addView(img);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class RvAdapter extends CommonAdapter<String> {

        public RvAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            holder.setText(R.id.tv_item, s);
        }
    }
}
```

# 开源项目

## [Android-ConvenientBanner](https://github.com/JackChen1999/Android-ConvenientBanner)

![](https://github.com/saiwu-bigkoo/Android-ConvenientBanner/raw/master/preview/convenientbannerdemo.gif)

## [DecentBanner](https://github.com/JackChen1999/DecentBanner)

![](https://github.com/JackChen1999/DecentBanner/raw/master/images/decent_sample.gif)

## [AndroidImageSlider](https://github.com/JackChen1999/AndroidImageSlider)

![](https://camo.githubusercontent.com/f64413139bbaa918131384d3597c33e39333aa7f/687474703a2f2f7777332e73696e61696d672e636e2f6d773639302f36313064633033346a773165677a6f7236366f6a64673230393530666b6e70652e676966)

https://github.com/JackChen1999/Banner

# 开源项目

## [Android-ConvenientBanner](https://github.com/JackChen1999/Android-ConvenientBanner)

![](https://github.com/saiwu-bigkoo/Android-ConvenientBanner/raw/master/preview/convenientbannerdemo.gif)

## [DecentBanner](https://github.com/JackChen1999/DecentBanner)

![](https://github.com/JackChen1999/DecentBanner/raw/master/images/decent_sample.gif)

## [AndroidImageSlider](https://github.com/JackChen1999/AndroidImageSlider)

![](https://camo.githubusercontent.com/f64413139bbaa918131384d3597c33e39333aa7f/687474703a2f2f7777332e73696e61696d672e636e2f6d773639302f36313064633033346a773165677a6f7236366f6a64673230393530666b6e70652e676966)
