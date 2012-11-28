package com.jernejovc.bohinjweatherinfo;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BohinjWeatherInfo extends SherlockFragmentActivity {

	public int selectedTab;
	public int orientation;
	Fragment dataFragment;
	Fragment camFragment;

	CustomViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new CustomViewPager(this);
		mViewPager.setId(R.id.fragment_container);

		setContentView(mViewPager);
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText(getResources().getString(R.string.dataTabName)).setIcon(R.drawable.ic_tab_data_selected),
				DataFragment.class, null);  
		mTabsAdapter.addTab(bar.newTab().setText(getResources().getString(R.string.camsTabName)).setIcon(R.drawable.ic_tab_webcam_selected), 
				WebcamFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText(getResources().getString(R.string.forecastTabName)).setIcon(R.drawable.ic_action_forecast), 
				ForecastFragment.class, null);
		
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(this.orientation != newConfig.orientation)
		{
			ActionBar bar = getSupportActionBar();
			bar.setSelectedNavigationItem(selectedTab);
			this.orientation = newConfig.orientation;
			System.out.println(orientation);
		}
	}

	public void setSelectedTab(int selectedTab)
	{
		this.selectedTab = selectedTab;
	}

	public class CustomViewPager extends ViewPager{

		public CustomViewPager(Context context){
			super(context);
		}

		public CustomViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev){
			return false;
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev){
			return false;
		}
	}

	public static class TabsAdapter extends FragmentPagerAdapter implements
	ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
					((BohinjWeatherInfo)mContext).setSelectedTab(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
