/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaBuddies;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.recyclerViewDecor.DividerDecoration;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class OverviewFragment extends BaseFragment implements EventChangeListener
{

	private List<YonaBuddy> mListBuddy;
	private OverViewAdapter mOverViewAdapter;
	private RecyclerView mFriendsRecyclerView;
	private boolean isCurrentTabInView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.friends_overview_fragment, null);

		mListBuddy = new ArrayList<>();
		mFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		mFriendsRecyclerView.setLayoutManager(mLayoutManager);
		mOverViewAdapter = new OverViewAdapter(mListBuddy, YonaActivity.getActivity(), new OnFriendsItemClickListener()
		{
			@Override
			public void onFriendsItemClick(View v)
			{
				YonaBuddy yonaBuddy = (YonaBuddy) v.getTag();
				if (yonaBuddy != null && !yonaBuddy.getSendingStatus().equals(StatusEnum.REQUESTED.getStatus()))
				{
					Intent friendIntent = new Intent(IntentEnum.ACTION_DASHBOARD.getActionString());
					Bundle bundle = new Bundle();
					YonaAnalytics.createTapEventWithCategory(getString(R.string.overiview), AnalyticsConstant.FRIEND_TIMELINE);
					if (yonaBuddy.getLinks() != null)
					{
						bundle.putSerializable(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, yonaBuddy.getLinks().getYonaDailyActivityReports(), yonaBuddy.getLinks().getYonaWeeklyActivityReports(), 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
					}
					else
					{
						bundle.putSerializable(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
					}
					friendIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
					friendIntent.putExtras(bundle);
					YonaActivity.getActivity().replaceFragment(friendIntent);
				}

			}

			@Override
			public void onFriendsItemDeleteClick(View v)
			{

			}

			@Override
			public void onItemClick(View v)
			{

			}
		});
		mFriendsRecyclerView.setAdapter(mOverViewAdapter);
		setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(mOverViewAdapter));
		YonaApplication.getEventChangeManager().registerListener(this);
		getBuddies();
		return view;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	public void setIsInView(boolean isInView)
	{
		isCurrentTabInView = isInView;
	}

	/**
	 * Get Buddies
	 */
	private void getBuddies()
	{
		YonaActivity.getActivity().showLoadingView(true, null);
		APIManager.getInstance().getBuddyManager().getBuddies(new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				if (result instanceof YonaBuddies)
				{
					YonaBuddies buddies = (YonaBuddies) result;
					mListBuddy.clear();
					if (buddies != null && buddies.getEmbedded() != null && buddies.getEmbedded().getYonaBuddies() != null)
					{
						mListBuddy = buddies.getEmbedded().getYonaBuddies();
						mOverViewAdapter.notifyDataSetChange(mListBuddy);
					}
				}
				YonaActivity.getActivity().showLoadingView(false, null);
			}

			@Override
			public void onError(Object errorMessage)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
			}
		});
	}

	/**
	 * update RecyclerView item header for grouping section
	 *
	 * @param headerDecor
	 */
	private void setRecyclerHeaderAdapterUpdate(StickyRecyclerHeadersDecoration headerDecor)
	{
		mFriendsRecyclerView.addItemDecoration(headerDecor);

		// Add decoration for dividers between list items
		mFriendsRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

		// Add touch listeners
		StickyRecyclerHeadersTouchListener touchListener =
				new StickyRecyclerHeadersTouchListener(mFriendsRecyclerView, headerDecor);
		touchListener.setOnHeaderClickListener(
				new StickyRecyclerHeadersTouchListener.OnHeaderClickListener()
				{
					@Override
					public void onHeaderClick(View header, int position, long headerId)
					{
					}
				});
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_UPDATE_FRIEND_OVERVIEW:
				getBuddies();
				break;
			default:
				break;

		}
	}
}
