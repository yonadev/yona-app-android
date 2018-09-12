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
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

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
	private boolean isCurrentTabInView;

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
					EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
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
				AppUtils.reportException(TimelineFragment.class.getSimpleName(), e, Thread.currentThread());
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.dashboard_perday_fragment, null);
		listView = (RecyclerView) view.findViewById(R.id.listView);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());

		mDayTimelineStickyAdapter = new TimelineStickyAdapter(new ArrayList<DayActivity>(), new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (v.getTag() instanceof DayActivity)
				{
					openDetailPage((DayActivity) v.getTag());
				}
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
				new StickyRecyclerHeadersTouchListener.OnHeaderClickListener()
				{
					@Override
					public void onHeaderClick(View header, int position, long headerId)
					{
					}
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

	private void loadMoreItems()
	{
		mIsLoading = true;
		getDayActivity(true);
	}

	private void openDetailPage(DayActivity activity)
	{
		if (activity.getYonaGoal() != null && activity.getYonaGoal().getActivityCategoryName() != null)
		{
			YonaAnalytics.createTapEventWithCategory(getString(R.string.timeline), activity.getYonaGoal().getActivityCategoryName());
		}
		Intent intent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		intent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, activity.getLinks().getYonaDayDetails().getHref());
		if (activity.getLinks().getYonaBuddy() != null)
		{
			intent.putExtra(AppConstant.YONA_BUDDY_OBJ, activity.getLinks().getYonaBuddy());
			intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()) ? activity.getYonaGoal().getActivityCategoryName().toUpperCase() : getString(R.string.blank), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
		}
		else
		{
			intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()) ? activity.getYonaGoal().getActivityCategoryName().toUpperCase() : getString(R.string.blank), R.color.grape, R.drawable.triangle_shadow_grape));
		}
		YonaActivity.getActivity().replaceFragment(intent);
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
		mDayTimelineStickyAdapter.clear();
		getDayActivity(false);
	}

	public void setIsInView(boolean isInView)
	{
		isCurrentTabInView = isInView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	private void getDayActivity(boolean loadMore)
	{
		final EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity();
		if ((embeddedYonaActivity == null || embeddedYonaActivity.getPage() == null)
				|| (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null && embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()))
		{
			YonaActivity.getActivity().showLoadingView(true, null);
			APIManager.getInstance().getActivityManager().getWithBuddyActivity(loadMore, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					isDataLoading = false;
					showData();
					mIsLoading = false;
				}

				@Override
				public void onError(Object errorMessage)
				{
					isDataLoading = false;
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
		if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList() != null
				&& YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList().size() > 0)
		{
			mDayTimelineStickyAdapter.notifyDataSetChange(setHeaderListView());
			YonaActivity.getActivity().showLoadingView(false, null);
		}
		else
		{
			if (isAdded())
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_friend_text)));
			}
		}
	}

	private List<DayActivity> setHeaderListView()
	{
		List<DayActivity> dayActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
		List<DayActivity> newDayActivityList = new ArrayList<>();
		int index = 0;

		for (int i = 0; i < dayActivityList.size(); i++)
		{
			DayActivity currentDayActivity = dayActivityList.get(i);
			if (i == 0)
			{
				newDayActivityList.add(getNewDayActivity(index, currentDayActivity, ChartTypeEnum.TITLE));
				currentDayActivity.setStickyHeaderId(index++);
				newDayActivityList.add(dayActivityList.get(i));
			}
			else
			{
				DayActivity previousDayActivity = dayActivityList.get(i - 1);
				if (currentDayActivity.getStickyTitle().equals(previousDayActivity.getStickyTitle()))
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
				else
				{
					index++;
					newDayActivityList.add(getNewDayActivity(index, currentDayActivity, ChartTypeEnum.TITLE));
					currentDayActivity.setStickyHeaderId(index);
					newDayActivityList.add(currentDayActivity);
				}
			}
		}

		return newDayActivityList;
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
		switch (eventType)
		{
			case EventChangeManager.EVENT_UPDATE_FRIEND_TIMELINE:
				refreshAdapter();
				break;
			default:
				break;
		}
	}
}
