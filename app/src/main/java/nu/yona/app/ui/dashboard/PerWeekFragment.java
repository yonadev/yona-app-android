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
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class PerWeekFragment extends BaseFragment
{


	private RecyclerView listView;
	private PerWeekStickyAdapter perWeekStickyAdapter;
	private LinearLayoutManager mLayoutManager;
	private boolean mIsLoading = false;
	private YonaHeaderTheme mYonaHeaderTheme;
	private YonaBuddy yonaBuddy;

	/**
	 * Recyclerview's scroll listener when its getting end to load more data till the pages not reached
	 */
	private final RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener()
	{
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState)
		{
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			super.onScrolled(recyclerView, dx, dy);
			try
			{
				if (dy > 0)
				{
					int visibleItemCount = mLayoutManager.getChildCount();
					int totalItemCount = mLayoutManager.getItemCount();
					int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
					EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
					if (!mIsLoading &&
							embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
							&& embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()
							&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
					{
						loadMoreItems();
					}
				}
			}
			catch (Exception e)
			{
				AppUtils.reportException(PerWeekFragment.class.getSimpleName(), e, Thread.currentThread());
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null)
		{
			if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) instanceof YonaBuddy)
			{
				yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
			}
		}
		if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null)
		{
			mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.dashboard_perweek_fragment, null);

		listView = (RecyclerView) view.findViewById(R.id.listView);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());

		perWeekStickyAdapter = new PerWeekStickyAdapter(new ArrayList<WeekActivity>(), new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (v.getTag() instanceof WeekActivity)
				{
					openDetailPage((WeekActivity) v.getTag());
				}
			}
		});

		listView.setLayoutManager(mLayoutManager);
		listView.setAdapter(perWeekStickyAdapter);
		listView.addOnScrollListener(mRecyclerViewOnScrollListener);
		setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(perWeekStickyAdapter));
		return view;

	}

	@Override
	public void onResume()
	{
		super.onResume();
		refreshAdapter();
	}

	/**
	 * update RecyclerView item header for grouping section
	 *
	 * @param headerDecor
	 */
	private void setRecyclerHeaderAdapterUpdate(StickyRecyclerHeadersDecoration headerDecor)
	{
		listView.addItemDecoration(headerDecor);

		// Add touch listeners
		StickyRecyclerHeadersTouchListener touchListener =
				new StickyRecyclerHeadersTouchListener(listView, headerDecor);
		touchListener.setOnHeaderClickListener(
				new StickyRecyclerHeadersTouchListener.OnHeaderClickListener()
				{
					@Override
					public void onHeaderClick(View header, int position, long headerId)
					{
					}
				});
	}

	/**
	 * Refresh recyclerview's adapter
	 */
	private void refreshAdapter()
	{
		perWeekStickyAdapter.clear();
		getWeekActivity(false);
	}

	public void setIsInView(boolean isInView)
	{
	}

	/**
	 * load more items
	 */
	private void loadMoreItems()
	{
		mIsLoading = true;
		getWeekActivity(true);
	}


	private Href getURLToFetchWeekActivityOverViews(EmbeddedYonaActivity embeddedYonaActivity, boolean loadMore)
	{
		Href urlToFetchDayActivitiyOverviews = null;
		if (embeddedYonaActivity != null && embeddedYonaActivity.getLinks() != null && embeddedYonaActivity.getLinks().getNext() != null && loadMore)
		{
			urlToFetchDayActivitiyOverviews = embeddedYonaActivity.getLinks().getNext();
		}
		else if (embeddedYonaActivity != null && embeddedYonaActivity.getLinks() != null && embeddedYonaActivity.getLinks().getSelf() != null)
		{
			urlToFetchDayActivitiyOverviews = embeddedYonaActivity.getLinks().getSelf();
		}
		else
		{
			urlToFetchDayActivitiyOverviews = mYonaHeaderTheme.getWeekActivityUrl();
		}
		return urlToFetchDayActivitiyOverviews;
	}

	/**
	 * to get the list of user's messages
	 */
	private void getWeekActivity(boolean loadMore)
	{
		final EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
		if ((embeddedYonaActivity == null || embeddedYonaActivity.getPage() == null)
				|| (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null && embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()))
		{
			YonaActivity.getActivity().showLoadingView(true, null);
			Href urlToFetchWeekActivitiyOverviews = getURLToFetchWeekActivityOverViews(embeddedYonaActivity, loadMore);
			APIManager.getInstance().getActivityManager().getWeeksActivity(loadMore, mYonaHeaderTheme.isBuddyFlow(), urlToFetchWeekActivitiyOverviews, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					showData();
					mIsLoading = false;
				}

				@Override
				public void onError(Object errorMessage)
				{
					YonaActivity.getActivity().showLoadingView(false, null);
					YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
				}
			});
		}
		else
		{
			showData();
		}
	}

	private void showData()
	{
		if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList().size() > 0)
		{
			perWeekStickyAdapter.notifyDataSetChange(setHeaderListView());
			YonaActivity.getActivity().showLoadingView(false, null);
		}
		else
		{
			YonaActivity.getActivity().showLoadingView(false, null);
			YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
		}
	}

	private List<WeekActivity> setHeaderListView()
	{
		List<WeekActivity> weekActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList();
		int index = 0;
		for (int i = 0; i < weekActivityList.size(); i++)
		{
			if (i == 0)
			{
				weekActivityList.get(i).setStickyHeaderId(index++);
			}
			else
			{
				if (weekActivityList.get(i).getStickyTitle().equals(weekActivityList.get(i - 1).getStickyTitle()))
				{
					weekActivityList.get(i).setStickyHeaderId(weekActivityList.get(i - 1).getStickyHeaderId());
				}
				else
				{
					weekActivityList.get(i).setStickyHeaderId(index++);
				}
			}
		}
		return weekActivityList;
	}

	private void openDetailPage(WeekActivity activity)
	{
		showData();
		mIsLoading = false;
		YonaAnalytics.createTapEventWithCategory(getString(R.string.perweek), activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		Intent intent = new Intent(IntentEnum.ACTION_WEEK_DETAIL_VIEW.getActionString());
		intent.putExtra(AppConstant.OBJECT, activity);
		intent.putExtra(AppConstant.BOOLEAN, true);
		intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
		mYonaHeaderTheme.setHeader_title(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
		YonaActivity.getActivity().replaceFragment(intent);

	}
}
