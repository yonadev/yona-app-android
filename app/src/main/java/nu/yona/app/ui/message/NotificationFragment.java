/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.enums.NotificationEnum;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.frinends.OnFriendsItemClickListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class NotificationFragment extends BaseFragment
{

	private RecyclerView mMessageRecyclerView;
	private MessageStickyRecyclerAdapter mMessageStickyRecyclerAdapter;
	private YonaMessages mYonaMessages;
	private int currentPage = 0;
	private boolean mIsLoading = false;
	private LinearLayoutManager mLayoutManager;
	private Intent messageIntentClicked = null;
	private YonaMessage yonaMessageClicked = null;

	/**
	 * Click listener for item click and delete click of recycler view's item
	 */
	private final OnFriendsItemClickListener onFriendsItemClickListener = new OnFriendsItemClickListener()
	{
		@Override
		public void onFriendsItemClick(View view)
		{
			if (view.getTag() instanceof YonaMessage)
			{
				yonaMessageClicked = (YonaMessage) view.getTag();
				switch (yonaMessageClicked.getNotificationMessageEnum().getNotificationEnum())
				{
					case SYSTEMMESSAGE:
						handleSystemMessageClick();
						break;
					case BUDDYCONNECTREQUESTMESSAGE:
						handleBuddyConnectRequestMessageClick();
						break;
					case BUDDYCONNECTRESPONSEMESSAGE:
						handleBuddyConnectResponseMessageClick();
						break;
					case ACTIVITYCOMMENTMESSAGE:
						handleActivityCommentMessageClick();
						break;
					case GOALCONFLICTMESSAGE:
						handleGoalConflictMessageClick();
						break;
					case GOALCHANGEMESSAGE:
						handleGoalChangeMessageClick();
						break;
					case BUDDYINFOCHANGEMESSAGE:
						handleBuddyInfoChangeMessageClick();
						break;
					default:
						messageIntentClicked = null;
						yonaMessageClicked = null;
						break;
				}
				replaceFragment();
			}
		}

		private void replaceFragment()
		{
			updateStatusAsRead(yonaMessageClicked);
			if (messageIntentClicked != null)
			{
				messageIntentClicked.putExtra(AppConstant.YONA_MESSAGE, yonaMessageClicked);
				YonaActivity.getActivity().replaceFragment(messageIntentClicked);
			}
		}

		@Override
		public void onFriendsItemDeleteClick(View view)
		{
			if (view.getTag() instanceof YonaMessage)
			{
				YonaMessage yonaMessageClicked = (YonaMessage) view.getTag();
				updateStatusAsRead(yonaMessageClicked);
				if (yonaMessageClicked != null && yonaMessageClicked.getLinks() != null && yonaMessageClicked.getLinks().getEdit() != null && !TextUtils.isEmpty(yonaMessageClicked.getLinks().getEdit().getHref()))
				{
					YonaActivity.getActivity().showLoadingView(true, null);
					APIManager.getInstance().getNotificationManager().deleteMessage(yonaMessageClicked.getLinks().getEdit().getHref(), 0, 0, new DataLoadListener()
					{
						@Override
						public void onDataLoad(Object result)
						{
							YonaActivity.getActivity().showLoadingView(false, null);
							refreshAdapter();
						}

						@Override
						public void onError(Object errorMessage)
						{
							YonaActivity.getActivity().showLoadingView(false, null);
							YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
						}
					});
				}
			}
		}

		@Override
		public void onItemClick(View view)
		{
			if (view.getTag() instanceof YonaMessage)
			{
				YonaMessage yonaMessageClicked = (YonaMessage) view.getTag();
				updateStatusAsRead(yonaMessageClicked);
			}
		}
	};


	private void handleSystemMessageClick()
	{
		messageIntentClicked = new Intent(IntentEnum.ACTION_ADMIN_MESSAGE_DETAIL.getActionString());
		messageIntentClicked.putExtra(AppConstant.ADMIN_MESSAGE, yonaMessageClicked);
		messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape));
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.ADMIN_MESSAGE_SCREEN);
	}

	private void handleBuddyConnectResponseMessageClick()
	{
		if (yonaMessageClicked.getLinks() != null && yonaMessageClicked.getLinks().getEdit() != null && yonaMessageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.ACCEPTED)
		{
			messageIntentClicked = new Intent(IntentEnum.ACTION_FRIEND_PROFILE.getActionString());
			YonaHeaderTheme yonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue);
			messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, yonaHeaderTheme);
			messageIntentClicked.putExtra(AppConstant.YONAMESSAGE_OBJ, yonaMessageClicked);
			messageIntentClicked.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.friends));
		}
	}

	private void handleBuddyConnectRequestMessageClick()
	{
		if (yonaMessageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.REJECTED)
		{
			return;
		}
		if (yonaMessageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.ACCEPTED)
		{
			return;
		}
		else if (yonaMessageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.REQUESTED)
		{
			messageIntentClicked = new Intent(IntentEnum.ACTION_FRIEND_REQUEST.getActionString());
			messageIntentClicked.putExtra(AppConstant.YONAMESSAGE_OBJ, yonaMessageClicked);
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.status_friend_request));
		}
	}

	private void handleActivityCommentMessageClick()
	{
		if (yonaMessageClicked.getLinks() != null && yonaMessageClicked.getLinks().getYonaDayDetails() != null)
		{
			handleDayActivityCommentMessageClick();
		}
		else if (yonaMessageClicked.getLinks() != null && yonaMessageClicked.getLinks().getWeekDetails() != null)
		{
			handleWeekActivityCommentMessageClick();
		}
	}

	private void handleDayActivityCommentMessageClick()
	{
		messageIntentClicked = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		messageIntentClicked.putExtra(AppConstant.YONA_DAY_DEATIL_URL, yonaMessageClicked.getLinks().getYonaDayDetails().getHref());
		setIntentExtrasForActivityCommentMessageClick();
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN);
	}

	private void handleWeekActivityCommentMessageClick()
	{
		messageIntentClicked = new Intent(IntentEnum.ACTION_SINGLE_WEEK_DETAIL_VIEW.getActionString());
		messageIntentClicked.putExtra(AppConstant.YONA_WEEK_DETAIL_URL, yonaMessageClicked.getLinks().getWeekDetails().getHref());
		setIntentExtrasForActivityCommentMessageClick();
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN);
	}

	private void setIntentExtrasForActivityCommentMessageClick()
	{
		if (yonaMessageClicked.getLinks().getReplyComment() != null && !TextUtils.isEmpty(yonaMessageClicked.getLinks().getReplyComment().getHref()))
		{
			messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape));
		}
		else
		{
			messageIntentClicked.putExtra(AppConstant.YONA_BUDDY_OBJ, findBuddy(yonaMessageClicked.getLinks().getYonaBuddy()));
			messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue, R.drawable.triangle_shadow_blue));
		}
	}

	private void handleGoalConflictMessageClick()
	{
		if (yonaMessageClicked.getLinks().getYonaDayDetails() == null || (TextUtils.isEmpty(yonaMessageClicked.getLinks().getYonaDayDetails().getHref())))
		{
			return;
		}
		messageIntentClicked = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		messageIntentClicked.putExtra(AppConstant.YONA_DAY_DEATIL_URL, yonaMessageClicked.getLinks().getYonaDayDetails().getHref());
		messageIntentClicked.putExtra(AppConstant.URL, yonaMessageClicked.getUrl());
		setEventTimeForGoalConflictMessageIntent();
		if (yonaMessageClicked.getLinks() != null && yonaMessageClicked.getLinks().getYonaBuddy() != null && !TextUtils.isEmpty(yonaMessageClicked.getLinks().getYonaBuddy().getHref()))
		{
			messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			messageIntentClicked.putExtra(AppConstant.YONA_BUDDY_OBJ, findBuddy(yonaMessageClicked.getLinks().getYonaBuddy()));
		}
		else
		{
			messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape));
		}
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, NotificationEnum.GOALCONFLICTMESSAGE.getNotificationType());
	}

	private void setEventTimeForGoalConflictMessageIntent()
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_LONG_DATE_FORMAT, Locale.getDefault());
			Date date = sdf.parse(yonaMessageClicked.getActivityStartTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			messageIntentClicked.putExtra(AppConstant.EVENT_TIME, calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
		}
		catch (Exception e)
		{
			AppUtils.reportException(NotificationFragment.class.getSimpleName(), e, Thread.currentThread());
		}
	}

	private void handleGoalChangeMessageClick()
	{
		if (yonaMessageClicked.getLinks().getYonaBuddy() != null && !TextUtils.isEmpty(yonaMessageClicked.getLinks().getYonaBuddy().getHref()))
		{
			messageIntentClicked = new Intent(IntentEnum.ACTION_DASHBOARD.getActionString());
			YonaBuddy yonaBuddy = findBuddy(yonaMessageClicked.getLinks().getYonaBuddy());
			messageIntentClicked.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
			if (yonaBuddy.getLinks() != null)
			{
				messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, yonaBuddy.getLinks().getYonaDailyActivityReports(), yonaBuddy.getLinks().getYonaWeeklyActivityReports(), 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			}
			else
			{
				messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			}
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, NotificationEnum.GOALCHANGEMESSAGE.getNotificationType());
		}
	}

	private void handleBuddyInfoChangeMessageClick()
	{
		messageIntentClicked = new Intent(IntentEnum.ACTION_FRIEND_PROFILE.getActionString());
		YonaHeaderTheme yonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue);
		messageIntentClicked.putExtra(AppConstant.YONA_THEME_OBJ, yonaHeaderTheme);
		messageIntentClicked.putExtra(AppConstant.YONAMESSAGE_OBJ, yonaMessageClicked);
		messageIntentClicked.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.friends));
	}

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
			if (dy > 0)
			{
				int visibleItemCount = mLayoutManager.getChildCount();
				int totalItemCount = mLayoutManager.getItemCount();
				int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

				if (!mIsLoading && currentPage < mYonaMessages.getPage().getTotalPages())
				{
					if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
					{
						loadMoreItems();
					}
				}
			}
		}
	};

	/**
	 * load more items
	 */
	private void loadMoreItems()
	{
		currentPage += 1;
		getUserMessages(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.notification_layout, null);

		setupToolbar(view);

		mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		mMessageRecyclerView.setLayoutManager(mLayoutManager);
		mMessageStickyRecyclerAdapter = new MessageStickyRecyclerAdapter(new ArrayList<YonaMessage>(), YonaActivity.getActivity(), onFriendsItemClickListener);
		//mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(YonaActivity.getActivity()));
		mMessageRecyclerView.setAdapter(mMessageStickyRecyclerAdapter);
		mMessageRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
		setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(mMessageStickyRecyclerAdapter));
		getUser();
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_NOTIFICATION));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setTitleAndIcon();
		refreshAdapter();
	}

	/**
	 * update toolbar's Item
	 */
	private void setTitleAndIcon()
	{
		((YonaActivity) getActivity()).updateTabIcon(false);
		profileCircleImageView.setVisibility(View.GONE);
		toolbarTitle.setText(getString(R.string.message));
		rightIcon.setVisibility(View.GONE);
	}

	/**
	 * Refresh recyclerview's adapter
	 */
	private void refreshAdapter()
	{
		mMessageStickyRecyclerAdapter.clear();
		currentPage = 0;
		getUserMessages(false);
		//YonaApplication.getEventChangeManager().getDataState().setEmbeddedWithBuddyActivity(null);
	}

	private void getUser()
	{
		APIManager.getInstance().getAuthenticateManager().getUserFromServer();
	}

	private Href getURLToFetchMessages(boolean loadMore)
	{
		Href urlForMessageFetch = null;
		if (mYonaMessages == null)
		{
			User user = YonaApplication.getEventChangeManager().getDataState().getUser();
			urlForMessageFetch = user.getLinks().getYonaMessages();
		}
		else if (mYonaMessages.getLinks().getNext() != null && loadMore)
		{
			urlForMessageFetch = mYonaMessages.getLinks().getNext();
		}
		else if (mYonaMessages.getPage().getTotalPages() > 1)
		{
			urlForMessageFetch = mYonaMessages.getLinks().getFirst();
		}
		else
		{
			urlForMessageFetch = mYonaMessages.getLinks().getSelf();
		}
		return urlForMessageFetch;
	}


	/**
	 * to get the list of user's messages
	 */
	private void getUserMessages(boolean loadMore)
	{
		try
		{
			mIsLoading = true;
			String urlForMessageFetch = getURLToFetchMessages(loadMore).getHref();
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> handleYonaMessagesFetchSuccess((YonaMessages) result)), ((result) -> handleYonaMessagesFetchFailure(result)), null);
			APIManager.getInstance().getNotificationManager().getMessages(urlForMessageFetch, false, dataLoadListenerImpl);
		}
		catch (IllegalArgumentException e)
		{
			AppUtils.reportException(NotificationFragment.class.getSimpleName(), e, Thread.currentThread(), null);
		}
	}

	private Object handleYonaMessagesFetchSuccess(YonaMessages result)
	{
		YonaActivity.getActivity().showLoadingView(false, null);
		if (isAdded() && result != null && result instanceof YonaMessages)
		{
			YonaMessages mMessages = (YonaMessages) result;
			if (mMessages.getEmbedded() != null && mMessages.getEmbedded().getYonaMessages() != null)
			{
				mYonaMessages = mMessages;
				if (mIsLoading)
				{
					mMessageStickyRecyclerAdapter.updateData(mYonaMessages.getEmbedded().getYonaMessages());
				}
				else
				{
					mMessageStickyRecyclerAdapter.notifyDataSetChange(mYonaMessages.getEmbedded().getYonaMessages());
				}
			}
		}
		mIsLoading = false;
		return null;
	}


	private Object handleYonaMessagesFetchFailure(Object errorMessage)
	{
		YonaActivity.getActivity().showLoadingView(false, null);
		YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		mIsLoading = false;
		return null;
	}

	/**
	 * update RecyclerView item header for grouping section
	 *
	 * @param headerDecor
	 */
	private void setRecyclerHeaderAdapterUpdate(StickyRecyclerHeadersDecoration headerDecor)
	{
		mMessageRecyclerView.addItemDecoration(headerDecor);

		// Add decoration for dividers between list items
		//mMessageRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

		// Add touch listeners
		StickyRecyclerHeadersTouchListener touchListener =
				new StickyRecyclerHeadersTouchListener(mMessageRecyclerView, headerDecor);
		touchListener.setOnHeaderClickListener(
				new StickyRecyclerHeadersTouchListener.OnHeaderClickListener()
				{
					@Override
					public void onHeaderClick(View header, int position, long headerId)
					{
					}
				});
	}

	private YonaBuddy findBuddy(Href href)
	{
		return APIManager.getInstance().getActivityManager().findYonaBuddy(href);
	}

	private void updateStatusAsRead(YonaMessage message)
	{
		APIManager.getInstance().getNotificationManager().setReadMessage(mYonaMessages.getEmbedded().getYonaMessages(), message, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				mMessageStickyRecyclerAdapter.notifyDataSetChange((List<YonaMessage>) result);
			}

			@Override
			public void onError(Object errorMessage)
			{

			}
		});
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.NOTIFICATION;
	}
}
