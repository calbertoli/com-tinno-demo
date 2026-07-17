package com.tinno.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tinno.demo.sellingpoint.TabPagerAdapter;
import com.tinno.demo.util.ActivityUtils;
import com.tinno.demo.widget.DotIndicator;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SellingPointActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int INDEX_SELLING_POINT_1 = 0;
    public static final int INDEX_SELLING_POINT_2 = 1;
    public static final int INDEX_SELLING_POINT_3 = 2;
    public static final int INDEX_SELLING_POINT_4 = 3;
    public static final int INDEX_SELLING_POINT_5 = 4;
    public static final int INDEX_SELLING_POINT_FIRST = 0;
    public static final int INDEX_SELLING_POINT_LAST = 4;
    public static final String KEY_SELLING_POINT = "key_selling_point";
    private final String TAG = "SpecificationActivity";
    private ImageView mArrowBack;
    private ImageView mArrowForward;
    private ImageView mBackView;
    private int mCurrentPosition;
    private DotIndicator mDotIndicator;
    private TabLayout mTabLayout;
    private List<String> mTabTitles;
    private ViewPager2 mViewPager;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityUtils.keepScreenOn(this);
        setContentView(R.layout.activity_selling_point);
        ActivityUtils.setTransparentStatusBar(this);
        ActivityUtils.adjustContentForSystemBars(this, findViewById(R.id.rl_content), 10);
        init();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_arrow_back /* 2131362083 */:
                int i = this.mCurrentPosition;
                if (i > 0) {
                    this.mViewPager.setCurrentItem(i - 1);
                }
                break;
            case R.id.iv_arrow_forward /* 2131362084 */:
                int i2 = this.mCurrentPosition;
                if (i2 < 4) {
                    this.mViewPager.setCurrentItem(i2 + 1);
                }
                break;
            case R.id.iv_back /* 2131362085 */:
                finish();
                break;
        }
    }

    private void init() {
        this.mTabTitles = Arrays.asList(getString(R.string.selling_point_1), getString(R.string.selling_point_2), getString(R.string.selling_point_3), getString(R.string.selling_point_4), getString(R.string.selling_point_5));
        this.mViewPager = (ViewPager2) findViewById(R.id.vp_selling_point);
        this.mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ImageView imageView = (ImageView) findViewById(R.id.iv_back);
        this.mBackView = imageView;
        imageView.setOnClickListener(this);
        ImageView imageView2 = (ImageView) findViewById(R.id.iv_arrow_back);
        this.mArrowBack = imageView2;
        imageView2.setOnClickListener(this);
        ImageView imageView3 = (ImageView) findViewById(R.id.iv_arrow_forward);
        this.mArrowForward = imageView3;
        imageView3.setOnClickListener(this);
        this.mViewPager.setAdapter(new TabPagerAdapter(this));
        this.mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.tinno.demo.SellingPointActivity.1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                SellingPointActivity.this.mArrowForward.setVisibility(i == 4 ? 4 : 0);
                SellingPointActivity.this.mArrowBack.setVisibility(i == 0 ? 4 : 0);
                SellingPointActivity.this.mCurrentPosition = i;
                SellingPointActivity.this.changeAccentColor(i);
            }
        });
        new TabLayoutMediator(this.mTabLayout, this.mViewPager, new TabLayoutMediator.TabConfigurationStrategy() { // from class: com.tinno.demo.SellingPointActivity$$ExternalSyntheticLambda0
            @Override // com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
            public final void onConfigureTab(TabLayout.Tab tab, int i) {
                this.f$0.m299lambda$init$0$comtinnodemoSellingPointActivity(tab, i);
            }
        }).attach();
        setupIndicator();
        int intExtra = getIntent().getIntExtra(KEY_SELLING_POINT, 0);
        this.mCurrentPosition = intExtra;
        this.mViewPager.setCurrentItem(intExtra);
    }

    /* JADX INFO: renamed from: lambda$init$0$com-tinno-demo-SellingPointActivity, reason: not valid java name */
    /* synthetic */ void m299lambda$init$0$comtinnodemoSellingPointActivity(TabLayout.Tab tab, int i) {
        tab.setText(this.mTabTitles.get(i));
    }

    private void setupIndicator() {
        DotIndicator dotIndicator = (DotIndicator) findViewById(R.id.dt_selling_point);
        this.mDotIndicator = dotIndicator;
        dotIndicator.setupWithViewPager(this.mViewPager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeAccentColor(int i) {
        boolean z = true;
        if (i != 1 && i != 4) {
            z = false;
        }
        this.mArrowBack.setActivated(z);
        this.mArrowForward.setActivated(z);
        this.mBackView.setActivated(z);
        this.mTabLayout.setActivated(z);
        ActivityUtils.setStatusBarIconVisibility(this, !z);
        if (z) {
            this.mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.white));
            this.mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        } else {
            this.mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.font_desc), ContextCompat.getColor(this, R.color.font_content));
            this.mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.black));
        }
    }
}
