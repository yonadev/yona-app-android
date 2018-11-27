/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class FriendsFragment extends BaseFragment
{
	private final int TIMELINE = 0, OVERVIEW = 1;
	private ViewPager viewPager;
	private TabLayout tabLayout;
	private TimelineFragment timelineFragment;
	private OverviewFragment overviewFragment;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.viewpager_fragment, null);

		setupToolbar(view);

		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		tabLayout = (TabLayout) view.findViewById(R.id.tabs);
		setupViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
		rightIcon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.FRIENDS_SCREEN, AnalyticsConstant.ADD_FRIEND);
				addFriend();
			}
		});
		return view;
	}

	private void setupViewPager(ViewPager viewPager)
	{
		ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
		timelineFragment = new TimelineFragment();
		timelineFragment.setArguments(getArguments());
		overviewFragment = new OverviewFragment();
		overviewFragment.setArguments(getArguments());
		adapter.addFragment(timelineFragment, getString(R.string.timeline));
		adapter.addFragment(overviewFragment, getString(R.string.overiview));
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{

			}

			@Override
			public void onPageSelected(int position)
			{
				updateView(position);
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{

			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setTitleAndIcon();
		if (viewPager != null)
		{
			updateView(viewPager.getCurrentItem());
		}
	}

	private void updateView(int position)
	{
		if (position == 0)
		{
			timelineFragment.setIsInView(false);
			overviewFragment.setIsInView(true);
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.FRIENDS_SCREEN, getString(R.string.timeline));
		}
		else
		{
			timelineFragment.setIsInView(true);
			overviewFragment.setIsInView(false);
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.FRIENDS_SCREEN, getString(R.string.overiview));
		}
	}

	private void setTitleAndIcon()
	{
		setTabs();
		((YonaActivity) getActivity()).updateTabIcon(true);
		profileCircleImageView.setVisibility(View.GONE);
		toolbarTitle.setText(R.string.friends);
		rightIcon.setVisibility(View.VISIBLE);
		rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_add));
	}

	private void setTabs()
	{
		ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
		mParams.height = getResources().getDimensionPixelSize(R.dimen.topTabBarHeight);
		tabLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.ten), 0, 0);
		tabLayout.setLayoutParams(mParams);
		tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
		tabLayout.setBackgroundResource(R.color.mid_blue_two);
	}

	private void addFriend()
	{
		Intent friendIntent = new Intent(IntentEnum.ACTION_ADD_FRIEND.getActionString());
		YonaActivity.getActivity().replaceFragment(friendIntent);
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_BASE_FRAGMENT;
	}
}
