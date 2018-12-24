/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.friends;

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
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class AddFriendFragment extends BaseFragment
{

	private final int ADD_FRIEND_MANUALLY = 0, ADD_FRIENT_CONTACT = 1;
	private ViewPager viewPager;
	private TabLayout tabLayout;

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
		return view;
	}

	private void setupViewPager(ViewPager viewPager)
	{
		ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
		adapter.addFragment(new AddFriendManuallyFragment(), getString(R.string.addfriendmanually));
		adapter.addFragment(new AddFriendContacts(), getString(R.string.addfriendcontacts));
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
				if (position == ADD_FRIENT_CONTACT)
				{
					YonaActivity.getActivity().checkContactPermission();
					updateView(position);
				}
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
		viewPager.setCurrentItem(ADD_FRIEND_MANUALLY, true);
		setTitleAndIcon();
		if (viewPager != null)
		{
			updateView(viewPager.getCurrentItem());
		}
	}

	private void setTitleAndIcon()
	{
		setTabs();
		profileCircleImageView.setVisibility(View.GONE);
		toolbarTitle.setText(getString(R.string.add_friend));
		rightIcon.setVisibility(View.GONE);

	}

	private void setTabs()
	{
		tabLayout.setVisibility(View.VISIBLE);
		ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
		mParams.height = getResources().getDimensionPixelSize(R.dimen.topTabBarHeight);
		tabLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.ten), 0, 0);
		tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
		tabLayout.setLayoutParams(mParams);
		tabLayout.setBackgroundResource(R.color.mid_blue_two);
	}

	private void updateView(int position)
	{
		if (position == 0)
		{
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.ADD_FRIEND, getString(R.string.addfriendmanually));
		}
		else
		{
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.ADD_FRIEND, getString(R.string.addfriendcontacts));
		}
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_BASE_FRAGMENT;
	}
}
