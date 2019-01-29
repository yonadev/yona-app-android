/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DashboardFragment extends BaseFragment implements EventChangeListener
{

	private TabLayout tabLayout;
	private YonaHeaderTheme mYonaHeaderTheme;
	private YonaBuddy yonaBuddy;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private String pageTitle;
	private PerDayFragment perDayFragment;
	private PerWeekFragment perWeekFragment;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
			pageTitle = mYonaHeaderTheme.getHeader_title();
			yonaBuddy = (YonaBuddy) getArguments().getSerializable(AppConstant.YONA_BUDDY_OBJ);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.viewpager_fragment, null);
		YonaApplication.getEventChangeManager().registerListener(this);
		resetData();
		setupToolbar(view);
		if (mYonaHeaderTheme != null)
		{
			mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
		}
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		tabLayout = (TabLayout) view.findViewById(R.id.tabs);
		setupViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_DASHBOARD));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_NOTIFICATION_COUNT, null);
		setTitleAndIcon();
		if (viewPager != null)
		{
			updateView(viewPager.getCurrentItem());
		}
	}

	private void resetData()
	{
		if (mYonaHeaderTheme.isBuddyFlow())
		{
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		resetData();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	private void setupViewPager(ViewPager viewPager)
	{
		setTabs();
		adapter = new ViewPagerAdapter(getChildFragmentManager());
		perDayFragment = new PerDayFragment();
		perDayFragment.setArguments(getArguments());
		perWeekFragment = new PerWeekFragment();
		perWeekFragment.setArguments(getArguments());
		adapter.addFragment(perDayFragment, getString(R.string.perday));
		adapter.addFragment(perWeekFragment, getString(R.string.perweek));
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

	private void setTabs()
	{
		ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
		mParams.height = getResources().getDimensionPixelSize(R.dimen.topTabBarHeight);
		tabLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.ten), 0, 0);
		if (mYonaHeaderTheme != null)
		{
			tabLayout.setBackgroundResource(mYonaHeaderTheme.getHeadercolor());
			if (mYonaHeaderTheme.isBuddyFlow())
			{
				tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
			}
			else
			{
				tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.dashboard_deselected_tab), ContextCompat.getColor(getActivity(), R.color.dashboard_selected_tab));
			}
		}
		tabLayout.setLayoutParams(mParams);
	}

	private void setTitleAndIcon()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				User user = YonaApplication.getEventChangeManager().getDataState().getUser();
				if (user != null && !TextUtils.isEmpty(user.getFirstName()))
				{
					if (mYonaHeaderTheme != null)
					{
						if (mYonaHeaderTheme.isBuddyFlow())
						{
							profileCircleImageView.setVisibility(View.GONE);
							rightIcon.setVisibility(View.GONE);
							rightIconProfile.setVisibility(View.VISIBLE);
							txtNotificationCounter.setVisibility(View.GONE);
							rightIconProfile.setVisibility(View.VISIBLE);
							profileIconTxt.setVisibility(View.VISIBLE);
							profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
							profileIconTxt.setText(yonaBuddy.getNickname().substring(0, 1).toUpperCase());
							profileClickEvent(profileIconTxt);
						}
						else
						{
							final Href userPhoto = user.getLinks().getUserPhoto();
							if (userPhoto != null)
							{
								Picasso.with(getContext()).load(userPhoto.getHref()).noFade().into(profileCircleImageView);
								profileCircleImageView.setVisibility(View.VISIBLE);
								initialsImageView.setVisibility(View.GONE);
								profileClickEvent(profileCircleImageView);
							}
							else
							{
								profileCircleImageView.setVisibility(View.GONE);
								initialsImageView.setVisibility(View.VISIBLE);
								initialsImageView.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_self_round));
								initialsImageView.setText(user.getNickname().substring(0, 1).toUpperCase());
								profileClickEvent(initialsImageView);
							}

							rightIcon.setVisibility(View.VISIBLE);
							rightIconProfile.setVisibility(View.GONE);
							int notificaitonCount = YonaApplication.getEventChangeManager().getDataState().getNotificationCount();
							if (notificaitonCount > 0)
							{
								txtNotificationCounter.setText("" + notificaitonCount);
								if (notificaitonCount > 99)
								{
									txtNotificationCounter.setTextSize(10);
								}
								txtNotificationCounter.setVisibility(View.VISIBLE);
							}
							else
							{
								txtNotificationCounter.setVisibility(View.GONE);
							}
							rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_reminder));

							rightIconClickEvent(rightIcon);
						}
					}
				}
				toolbarTitle.setText(pageTitle);
				tabLayout.setVisibility(View.VISIBLE);
			}
		}, AppConstant.TIMER_DELAY_HUNDRED);

	}

	/**
	 * Pass the view of profile icon for Me and buddies Profile
	 *
	 * @param profileView
	 */
	private void profileClickEvent(View profileView)
	{
		profileView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DASHBOARD_SCREEN, AnalyticsConstant.SCREEN_PROFILE);
				Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
				intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
				if (yonaBuddy != null)
				{
					intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
				}
				else
				{
					intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, R.drawable.icn_reminder, getString(R.string.dashboard), R.color.grape, R.drawable.triangle_shadow_grape));
					intent.putExtra(AppConstant.USER, YonaApplication.getEventChangeManager().getDataState().getUser());
				}
				YonaActivity.getActivity().replaceFragment(intent);
			}
		});
	}

	/**
	 * To Show the Message Notification list and redirect to that view by click on notification icon
	 *
	 * @param rightIconView
	 */
	private void rightIconClickEvent(View rightIconView)
	{
		txtNotificationCounter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent friendIntent = new Intent(IntentEnum.ACTION_MESSAGE.getActionString());
				YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DASHBOARD_SCREEN, AnalyticsConstant.NOTIFICATION);
				YonaActivity.getActivity().replaceFragment(friendIntent);
			}
		});
		rightIconView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent friendIntent = new Intent(IntentEnum.ACTION_MESSAGE.getActionString());
				YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DASHBOARD_SCREEN, AnalyticsConstant.NOTIFICATION);
				YonaActivity.getActivity().replaceFragment(friendIntent);
			}
		});
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_UPDATE_NOTIFICATION_COUNT:
				setTitleAndIcon();
				break;
			default:
				break;
		}
	}

	private void updateView(int position)
	{
		if (position == 0)
		{
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.DASHBOARD_SCREEN, getString(R.string.perday));
		}
		else
		{
			YonaAnalytics.createTrackEventWithCategory(AnalyticsConstant.DASHBOARD_SCREEN, getString(R.string.perweek));
		}
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_BASE_FRAGMENT;
	}
}
