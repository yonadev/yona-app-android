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

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class PerDayFragment extends BaseFragment
{

	private RecyclerView listView;
	private PerDayStickyAdapter perDayStickyAdapter;
	private LinearLayoutManager mLayoutManager;
	private boolean mIsLoading = false;
	private boolean isDataLoading = false;
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
				EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
				if (mIsLoading || embeddedYonaActivity == null ||
						embeddedYonaActivity.getPage().getNumber() >= embeddedYonaActivity.getPage().getTotalPages())
				{
					return; // this happens when the view loads even before the api call returns the response.
				}
				loadItemsOnScroll(dy);
			}
			catch (Exception e)
			{
				AppUtils.reportException(PerDayFragment.class, e, Thread.currentThread());
			}
		}
	};

	private void loadItemsOnScroll(int dy)
	{
		int visibleItemCount = mLayoutManager.getChildCount();
		int totalItemCount = mLayoutManager.getItemCount();
		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		if (dy > 0 && ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount))
		{
			loadMoreItems();
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) instanceof YonaBuddy)
		{
			yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
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
		View view = inflater.inflate(R.layout.dashboard_perday_fragment, null);
		listView = view.findViewById(R.id.listView);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		perDayStickyAdapter = new PerDayStickyAdapter(new ArrayList<>(), v -> {
			if (v.getTag() instanceof DayActivity)
			{
				openDetailPage((DayActivity) v.getTag());
			}
		});
		perDayStickyAdapter.setOnBottomReachedListener((position) -> loadMoreItems());
		listView.setLayoutManager(mLayoutManager);
		listView.setAdapter(perDayStickyAdapter);
		listView.addOnScrollListener(mRecyclerViewOnScrollListener);
		setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(perDayStickyAdapter));
		return view;
	}


	private void openDetailPage(DayActivity activity)
	{
		isDataLoading = false;
		showData();
		mIsLoading = false;
		YonaAnalytics.createTapEventWithCategory(getString(R.string.perday), activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		Intent intent = new Intent(IntentEnum.ACTION_ACTIVITY_DETAIL_VIEW.getActionString());
		intent.putExtra(AppConstant.OBJECT, activity);
		intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
		intent.putExtra(AppConstant.BOOLEAN, true);
		mYonaHeaderTheme.setHeader_title(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
		YonaActivity.getActivity().replaceFragment(intent);
	}

	/**
	 * update RecyclerView item header for grouping section
	 *
	 * @param headerDecor
	 */
	private void setRecyclerHeaderAdapterUpdate(final StickyRecyclerHeadersDecoration headerDecor)
	{
		listView.addItemDecoration(headerDecor);
		perDayStickyAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
		{
			@Override
			public void onChanged()
			{
				headerDecor.invalidateHeaders();
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (!isDataLoading)
		{
			isDataLoading = true;
			refreshAdapter();
		}
		else if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity() != null)
		{
			showData();
		}
	}

	/**
	 * Refresh recyclerview's adapter
	 */
	private void refreshAdapter()
	{
		perDayStickyAdapter.clear();
		getDayActivity(false);
	}


	/**
	 * load more items
	 */
	private void loadMoreItems()
	{
		mIsLoading = true;
		getDayActivity(true);
	}


	private Href getURLToFetchDayActivityOverViews(EmbeddedYonaActivity embeddedYonaActivity, boolean loadMore)
	{
		if (embeddedYonaActivity != null && embeddedYonaActivity.getLinks() != null && embeddedYonaActivity.getLinks().getNext() != null && loadMore)
		{
			return embeddedYonaActivity.getLinks().getNext();
		}
		if (embeddedYonaActivity != null && embeddedYonaActivity.getLinks() != null && embeddedYonaActivity.getLinks().getFirst() != null)
		{
			if (perDayStickyAdapter.getItemCount() == 0)
			{ //Loading from beginnig after perDayStickyAdapter is cleared in onResume.
				return embeddedYonaActivity.getLinks().getFirst();
			}
			return embeddedYonaActivity.getLinks().getSelf();
		}
		return mYonaHeaderTheme.getDayActivityUrl();
	}


	/**
	 * to get the list of user's messages
	 */
	private void getDayActivity(boolean loadMore)
	{
		final EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
		if (embeddedYonaActivity == null || embeddedYonaActivity.getPage() == null || embeddedYonaActivity.getPage() != null && embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages())
		{
			loadDaysActivity(embeddedYonaActivity, loadMore);
		}
		else
		{
			showData();
		}
	}

	private void loadDaysActivity(EmbeddedYonaActivity embeddedYonaActivity, boolean loadMore)
	{
		YonaActivity.getActivity().displayLoadingView();
		Href urlToFetchDayActivityOverviews = getURLToFetchDayActivityOverViews(embeddedYonaActivity, loadMore);
		DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl(((result) -> handleDaysActivityRetrieveOnSuccess(result)),
				((result) -> handleDaysActivityRetrieveOnFailure(result)), null);
		APIManager.getInstance().getActivityManager().getDaysActivity(loadMore, mYonaHeaderTheme.isBuddyFlow(), urlToFetchDayActivityOverviews, dataLoadListener);
	}

	private Object handleDaysActivityRetrieveOnSuccess(Object result)
	{
		isDataLoading = false;
		showData();
		mIsLoading = false;
		return null;
	}

	private Object handleDaysActivityRetrieveOnFailure(Object errorMessage)
	{
		isDataLoading = false;
		YonaActivity.getActivity().dismissLoadingView();
		YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		return null;
	}

	private void showData()
	{
		EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
		if (embeddedYonaActivity != null
				&& embeddedYonaActivity.getDayActivityList() != null
				&& embeddedYonaActivity.getDayActivityList().size() > 0)
		{
			perDayStickyAdapter.notifyDataSetChange(setHeaderListView(embeddedYonaActivity));
			mIsLoading = false;
			YonaActivity.getActivity().dismissLoadingView();
		}
		else
		{
			YonaActivity.getActivity().dismissLoadingView();
			YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
		}
	}

	private List<DayActivity> setHeaderListView(EmbeddedYonaActivity embeddedYonaActivity)
	{
		List<DayActivity> dayActivityList = embeddedYonaActivity.getDayActivityList();
		int index = 0;
		dayActivityList.get(0).setStickyHeaderId(index++);
		for (int i = 1; i < dayActivityList.size(); i++)
		{
			DayActivity currentDayActivity = dayActivityList.get(i);
			DayActivity previousDayActivity = dayActivityList.get(i - 1);
			if (currentDayActivity.getStickyTitle().equals(previousDayActivity.getStickyTitle()))
			{
				currentDayActivity.setStickyHeaderId(previousDayActivity.getStickyHeaderId());
				continue;
			}
			currentDayActivity.setStickyHeaderId(index++);
		}
		return dayActivityList;
	}
}
