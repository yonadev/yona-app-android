/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;


import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.Categorizable;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.challenges.ChallengesFragment;
import nu.yona.app.ui.challenges.ChallengesGoalDetailFragment;
import nu.yona.app.ui.dashboard.DashboardFragment;
import nu.yona.app.ui.friends.AddFriendFragment;
import nu.yona.app.ui.friends.FriendsFragment;
import nu.yona.app.ui.friends.FriendsRequestFragment;
import nu.yona.app.ui.message.NotificationFragment;
import nu.yona.app.ui.settings.PrivacyFragment;
import nu.yona.app.ui.settings.SettingsFragment;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class BaseFragment extends Fragment implements Categorizable
{
	/**
	 * The M tool bar.
	 */
	protected Toolbar mToolBar;
	/**
	 * The Toolbar title.
	 */
	protected YonaFontTextView toolbarTitle,
			txtNotificationCounter;
	/**
	 * The Left icon.
	 */
	protected ImageView profileCircleImageView, /**
 * The Right icon.
 */
rightIcon,

	rightIconProfile;

	protected YonaFontTextView profileIconTxt, initialsImageView;

	private PauseResumeHook hook;

	@Override
	public void onPause()
	{
		super.onPause();
		if (hook != null)
		{
			hook.onPause(this);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		YonaAnalytics.updateScreen(this);
		if (hook != null)
		{
			hook.onResume(this);
		}
	}

	/**
	 * Sets toolbar.
	 *
	 * @param view the view
	 */
	protected void setupToolbar(View view)
	{
		mToolBar = view.findViewById(R.id.main_toolbar);
		toolbarTitle = mToolBar.findViewById(R.id.toolbar_title);
		profileCircleImageView = mToolBar.findViewById(R.id.leftIcon);
		rightIcon = mToolBar.findViewById(R.id.rightIcon);
		rightIconProfile = mToolBar.findViewById(R.id.rightIconProfile);
		profileIconTxt = mToolBar.findViewById(R.id.profileToolbarIcon);
		initialsImageView = mToolBar.findViewById(R.id.leftIconTxt);
		txtNotificationCounter = mToolBar.findViewById(R.id.txtNotificationCounter);
		if (!(this instanceof DashboardFragment))
		{
			mToolBar.removeView(profileCircleImageView);
		}
		updateToolBarBackground();
		YonaActivity.getActivity().setSupportActionBar(mToolBar);
	}

	private void updateToolBarBackground()
	{
		if (this instanceof ChallengesFragment || this instanceof ChallengesGoalDetailFragment)
		{
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_green);
		}
		else if (this instanceof NotificationFragment)
		{
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape);
		}
		else if (this instanceof SettingsFragment || this instanceof PrivacyFragment)
		{
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
		}
		else if (this instanceof FriendsFragment || this instanceof FriendsRequestFragment || this instanceof AddFriendFragment)
		{
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_blue);
		}
	}

	public void udpateBottomTabVisibility(final View view)
	{
		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				if (isAdded())
				{
					if (AppUtils.checkKeyboardOpen(view))
					{
						((YonaActivity) getActivity()).changeBottomTabVisibility(false);
					}
					else
					{
						((YonaActivity) getActivity()).changeBottomTabVisibility(true);
					}
				}
			}
		});
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_BASE_FRAGMENT;
	}

	public void setHook(PauseResumeHook hook)
	{
		this.hook = hook;
	}
}
