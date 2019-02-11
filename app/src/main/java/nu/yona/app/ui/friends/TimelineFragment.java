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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getAppUser;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class TimelineFragment extends BaseFragment implements EventChangeListener
{

	private RecyclerView listView;
	private TimelineStickyAdapter mDayTimelineStickyAdapter;
	private LinearLayoutManager mLayoutManager;
	private boolean mIsLoading;
	private boolean isDataLoading;

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
					loadGoalsOnScroll();
				}
			}
			catch (Exception e)
			{
				AppUtils.reportException(TimelineFragment.class.getSimpleName(), e, Thread.currentThread());
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.dashboard_perday_fragment, null);
		listView = view.findViewById(R.id.listView);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		mDayTimelineStickyAdapter = new TimelineStickyAdapter(new ArrayList<>(), v -> {
			if (v.getTag() instanceof DayActivity)
			{
				openDetailPage((DayActivity) v.getTag());
			}
		});
		listView.setLayoutManager(mLayoutManager);
		listView.setAdapter(mDayTimelineStickyAdapter);
		listView.addOnScrollListener(mRecyclerViewOnScrollListener);
		setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(mDayTimelineStickyAdapter));
		YonaApplication.getEventChangeManager().registerListener(this);
		return view;
	}

	private void setRecyclerHeaderAdapterUpdate(final StickyRecyclerHeadersDecoration headerDecor)
	{
		listView.addItemDecoration(headerDecor);
		// Add touch listeners
		StickyRecyclerHeadersTouchListener touchListener =
				new StickyRecyclerHeadersTouchListener(listView, headerDecor);
		touchListener.setOnHeaderClickListener(
				(header, position, headerId) -> {
				});
		mDayTimelineStickyAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
		{
			@Override
			public void onChanged()
			{
				headerDecor.invalidateHeaders();
			}
		});
	}

	private void loadGoalsOnScroll()
	{
		int visibleItemCount = mLayoutManager.getChildCount();
		int totalItemCount = mLayoutManager.getItemCount();
		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
		if (!mIsLoading &&
				embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
				&& embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()
				&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
		{
			mIsLoading = true;
			getDayActivity(true);
		}
	}

	private void openDetailPage(DayActivity activity)
	{
		Intent intent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		addYonaDayDetailsHrefToIntent(activity, intent);
		addYonaBuddyToIntent(activity, intent);
		intent.putExtra(AppConstant.OBJECT, activity);
		YonaActivity.getActivity().replaceFragment(intent);
	}

	private void addYonaDayDetailsHrefToIntent(DayActivity activity, Intent intent)
	{
		if (activity.getYonaGoal() != null && activity.getYonaGoal().getActivityCategoryName() != null)
		{
			YonaAnalytics.createTapEventWithCategory(getString(R.string.timeline), activity.getYonaGoal().getActivityCategoryName());
		}
		intent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, activity.getLinks().getYonaDayDetails().getHref());
	}

	private void addYonaBuddyToIntent(DayActivity activity, Intent intent)
	{
		if (activity.getLinks().getYonaBuddy() != null)
		{
			intent.putExtra(AppConstant.YONA_BUDDY_OBJ, activity.getLinks().getYonaBuddy());
			intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()) ? activity.getYonaGoal().getActivityCategoryName().toUpperCase() : getString(R.string.blank), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
		}
		else
		{
			intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()) ? activity.getYonaGoal().getActivityCategoryName().toUpperCase() : getString(R.string.blank), R.color.grape, R.drawable.triangle_shadow_grape));
		}
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
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

	private void refreshAdapter()
	{
		getUserFromServer(getAppUser().getLinks().getSelf().getHref());
		mDayTimelineStickyAdapter.clear();
	}

	private void getUserFromServer(String url)
	{
		YonaActivity.getActivity().displayLoadingView();
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleUserFetchSuccess(result), (result) -> handleUserFetchFailure(result), null);
		APIManager.getInstance().getAuthenticateManager().getUserFromServer(url, dataLoadListenerImpl);
	}

	private Object handleUserFetchSuccess(Object result)
	{
		getDayActivity(false);
		return null;// Dummy return value, to allow use as data load handler
	}

	private Object handleUserFetchFailure(Object error)
	{
		YonaActivity.getActivity().dismissLoadingView();
		if (error instanceof ErrorMessage)
		{
			AppUtils.displayErrorAlert(YonaActivity.getActivity(), (ErrorMessage) error);
		}
		else
		{
			AppUtils.displayErrorAlert(YonaActivity.getActivity(), new ErrorMessage((String) error));
		}
		return null;// Dummy return value, to allow use as data load handler
	}


	private void getDayActivity(boolean loadMore)
	{
		final EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity();
		if (embeddedYonaActivity == null || embeddedYonaActivity.getPage() == null || embeddedYonaActivity.getPage() != null && embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages())
		{
			YonaActivity.getActivity().displayLoadingView();
			DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result -> handleGetWithBuddyActivityFetchSuccess()), (error -> handleGetWithBuddyActivityFetchFailure(error)), null);
			APIManager.getInstance().getActivityManager().getWithBuddyActivity(loadMore, dataLoadListener);
		}
		else
		{
			showData();
		}
	}

	private Object handleGetWithBuddyActivityFetchSuccess()
	{
		isDataLoading = false;
		showData();
		mIsLoading = false;
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object handleGetWithBuddyActivityFetchFailure(Object error)
	{
		isDataLoading = false;
		YonaActivity.getActivity().dismissLoadingView();
		if (error instanceof ErrorMessage)
		{
			YonaActivity.getActivity().showError((ErrorMessage) error);
		}
		else if (error != null)
		{
			YonaActivity.getActivity().showError(new ErrorMessage(error.toString()));
		}
		return null; // Dummy return value, to allow use as data error handler
	}

	private void showData()
	{
		if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList().size() > 0)
		{
			mDayTimelineStickyAdapter.notifyDataSetChange(setHeaderListView());
			YonaActivity.getActivity().dismissLoadingView();
		}
		else
		{
			if (isAdded())
			{
				YonaActivity.getActivity().dismissLoadingView();
				YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_friend_text)));
			}
		}
	}

	private List<DayActivity> setHeaderListView()
	{
		List<DayActivity> dayActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
		List<DayActivity> newDayActivityList = new ArrayList<>();
		processDayActivityListForTimeLineView(dayActivityList, newDayActivityList);
		return newDayActivityList;
	}

	private void processDayActivityListForTimeLineView(List<DayActivity> dayActivityList, List<DayActivity> newDayActivityList)
	{
		int index = 0;
		for (int i = 0; i < dayActivityList.size(); i++)
		{
			DayActivity currentDayActivity = dayActivityList.get(i);
			if (i == 0)
			{
				setFirstDayActivityProperties(currentDayActivity, newDayActivityList, index++, i, dayActivityList);
			}
			else
			{
				DayActivity previousDayActivity = dayActivityList.get(i - 1);
				if (currentDayActivity.getStickyTitle().equals(previousDayActivity.getStickyTitle()))
				{
					setCurrentDayActivityProperties(previousDayActivity, currentDayActivity, newDayActivityList);
				}
				else
				{
					index++;
					setNextDayActivityProperties(index, currentDayActivity, newDayActivityList);
				}
			}
		}
	}

	private void setFirstDayActivityProperties(DayActivity currentDayActivity, List<DayActivity> newDayActivityList, int index, int i, List<DayActivity> dayActivityList)
	{
		newDayActivityList.add(getNewDayActivity(index--, currentDayActivity, ChartTypeEnum.TITLE));
		currentDayActivity.setStickyHeaderId(index);
		newDayActivityList.add(dayActivityList.get(i));
	}


	private void setCurrentDayActivityProperties(DayActivity previousDayActivity, DayActivity currentDayActivity, List<DayActivity> newDayActivityList)
	{
		currentDayActivity.setStickyHeaderId(previousDayActivity.getStickyHeaderId());
		if (!currentDayActivity.getYonaGoal().getActivityCategoryName().equals(previousDayActivity.getYonaGoal().getActivityCategoryName()))
		{
			newDayActivityList.add(getNewDayActivity(previousDayActivity.getStickyHeaderId(), currentDayActivity, ChartTypeEnum.TITLE));
		}
		else if (!currentDayActivity.getYonaGoal().getType().equals(previousDayActivity.getYonaGoal().getType()))
		{
			newDayActivityList.add(getNewDayActivity(previousDayActivity.getStickyHeaderId(), currentDayActivity, ChartTypeEnum.LINE));
		}
		newDayActivityList.add(currentDayActivity);
	}

	private void setNextDayActivityProperties(int index, DayActivity currentDayActivity, List<DayActivity> newDayActivityList)
	{
		newDayActivityList.add(getNewDayActivity(index, currentDayActivity, ChartTypeEnum.TITLE));
		currentDayActivity.setStickyHeaderId(index);
		newDayActivityList.add(currentDayActivity);
	}

	private DayActivity getNewDayActivity(int index, DayActivity dayActivity, ChartTypeEnum chartTypeEnum)
	{
		DayActivity activity = new DayActivity();
		activity.setStickyHeaderId(index);
		activity.setStickyTitle(dayActivity.getStickyTitle());
		activity.setChartTypeEnum(chartTypeEnum);
		YonaGoal yonaGoal = new YonaGoal();
		yonaGoal.setActivityCategoryName(dayActivity.getYonaGoal().getActivityCategoryName());
		activity.setYonaGoal(yonaGoal);
		return activity;
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		if (eventType == EventChangeManager.EVENT_UPDATE_FRIEND_TIMELINE)
		{
			refreshAdapter();
		}
	}
}
