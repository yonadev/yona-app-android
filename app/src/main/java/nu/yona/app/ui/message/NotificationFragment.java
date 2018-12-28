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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nu.yona.app.R;
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
import nu.yona.app.ui.friends.OnFriendsItemClickListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

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
				YonaMessage messageClicked = (YonaMessage) view.getTag();
				Intent messageIntent = null;
				switch (messageClicked.getNotificationMessageEnum().getNotificationEnum())
				{
					case SYSTEMMESSAGE:
						messageIntent = handleSystemMessageClick(messageClicked);
						break;
					case BUDDYCONNECTREQUESTMESSAGE:
						messageIntent = handleBuddyConnectRequestMessageClick(messageIntent, messageClicked);
						break;
					case BUDDYCONNECTRESPONSEMESSAGE:
						messageIntent = handleBuddyConnectResponseMessageClick(messageIntent, messageClicked);
						break;
					case ACTIVITYCOMMENTMESSAGE:
						messageIntent = handleActivityCommentMessageClick(messageIntent, messageClicked);
						break;
					case GOALCONFLICTMESSAGE:
						messageIntent = handleGoalConflictMessageClick(messageIntent, messageClicked);
						break;
					case GOALCHANGEMESSAGE:
						messageIntent = handleGoalChangeMessageClick(messageIntent, messageClicked);
						break;
					case BUDDYINFOCHANGEMESSAGE:
						messageIntent = handleBuddyInfoChangeMessageClick(messageClicked);
						break;
					default:
						break;
				}
				replaceFragment(messageIntent, messageClicked);
			}
		}

		private void replaceFragment(Intent messageIntent, YonaMessage messageClicked)
		{
			updateStatusAsRead(messageClicked);
			if (messageIntent != null)
			{
				messageIntent.putExtra(AppConstant.YONA_MESSAGE, messageClicked);
				YonaActivity.getActivity().replaceFragment(messageIntent);
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
					APIManager.getInstance().getNotificationManager().deleteMessage(yonaMessageClicked.getLinks().getEdit().getHref(), new DataLoadListener()
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


	private Intent handleSystemMessageClick(YonaMessage messageClicked)
	{
		Intent messageIntent = new Intent(IntentEnum.ACTION_ADMIN_MESSAGE_DETAIL.getActionString());
		messageIntent.putExtra(AppConstant.ADMIN_MESSAGE, messageClicked);
		messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape));
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.ADMIN_MESSAGE_SCREEN);
		return messageIntent;
	}

	private Intent handleBuddyConnectResponseMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		if (messageClicked.getLinks() != null && messageClicked.getLinks().getEdit() != null && messageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.ACCEPTED)
		{
			messageIntent = new Intent(IntentEnum.ACTION_FRIEND_PROFILE.getActionString());
			YonaHeaderTheme yonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue);
			messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, yonaHeaderTheme);
			messageIntent.putExtra(AppConstant.YONAMESSAGE_OBJ, messageClicked);
			messageIntent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
		}
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.friends));
		return messageIntent;
	}

	private Intent handleBuddyConnectRequestMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.status_friend_request));
		if (messageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.REJECTED)
		{
			return messageIntent;
		}
		if (messageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.ACCEPTED)
		{
			return messageIntent;
		}
		else if (messageClicked.getNotificationMessageEnum().getStatusEnum() == StatusEnum.REQUESTED)
		{
			messageIntent = new Intent(IntentEnum.ACTION_FRIEND_REQUEST.getActionString());
			messageIntent.putExtra(AppConstant.YONAMESSAGE_OBJ, messageClicked);
		}
		return messageIntent;
	}

	private Intent handleActivityCommentMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		if (messageClicked.getLinks() != null && messageClicked.getLinks().getYonaDayDetails() != null)
		{
			return handleDayActivityCommentMessageClick(messageClicked);
		}
		else if (messageClicked.getLinks() != null && messageClicked.getLinks().getWeekDetails() != null)
		{
			return handleWeekActivityCommentMessageClick(messageClicked);
		}
		return messageIntent;
	}

	private Intent handleDayActivityCommentMessageClick(YonaMessage messageClicked)
	{
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN);
		Intent messageIntent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		messageIntent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, messageClicked.getLinks().getYonaDayDetails().getHref());
		return setIntentExtrasForActivityCommentMessageClick(messageIntent, messageClicked);
	}

	private Intent handleWeekActivityCommentMessageClick(YonaMessage messageClicked)
	{
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN);
		Intent messageIntent = new Intent(IntentEnum.ACTION_SINGLE_WEEK_DETAIL_VIEW.getActionString());
		messageIntent.putExtra(AppConstant.YONA_WEEK_DETAIL_URL, messageClicked.getLinks().getWeekDetails().getHref());
		return setIntentExtrasForActivityCommentMessageClick(messageIntent, messageClicked);
	}


	private Intent setIntentExtrasForActivityCommentMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		messageIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, findBuddy(messageClicked.getLinks().getYonaBuddy()));
		// For ActivityCommentMessage type notification Yona-header theme is set up in respective activity after checking for yonaBuddy link.
		return messageIntent;
	}

	private Intent handleGoalConflictMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, NotificationEnum.GOALCONFLICTMESSAGE.getNotificationType());
		if (messageClicked.getLinks().getYonaDayDetails() == null || (TextUtils.isEmpty(messageClicked.getLinks().getYonaDayDetails().getHref())))
		{
			return messageIntent;
		}
		messageIntent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		messageIntent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, messageClicked.getLinks().getYonaDayDetails().getHref());
		messageIntent.putExtra(AppConstant.URL, messageClicked.getUrl());
		messageIntent = setEventTimeForGoalConflictMessageIntent(messageIntent, messageClicked);
		if (messageClicked.getLinks() != null && messageClicked.getLinks().getYonaBuddy() != null && !TextUtils.isEmpty(messageClicked.getLinks().getYonaBuddy().getHref()))
		{
			messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			messageIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, findBuddy(messageClicked.getLinks().getYonaBuddy()));
		}
		else
		{
			messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape));
		}
		return messageIntent;
	}

	private Intent setEventTimeForGoalConflictMessageIntent(Intent messageIntent, YonaMessage messageClicked)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_LONG_DATE_FORMAT, Locale.getDefault());
			Date date = sdf.parse(messageClicked.getActivityStartTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return messageIntent.putExtra(AppConstant.EVENT_TIME, calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
		}
		catch (ParseException e)
		{
			AppUtils.reportException(NotificationFragment.class.getSimpleName(), e, Thread.currentThread());
		}
		return messageIntent;
	}

	private Intent handleGoalChangeMessageClick(Intent messageIntent, YonaMessage messageClicked)
	{
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, NotificationEnum.GOALCHANGEMESSAGE.getNotificationType());
		if (messageClicked.getLinks().getYonaBuddy() != null && !TextUtils.isEmpty(messageClicked.getLinks().getYonaBuddy().getHref()))
		{
			messageIntent = new Intent(IntentEnum.ACTION_DASHBOARD.getActionString());
			YonaBuddy yonaBuddy = findBuddy(messageClicked.getLinks().getYonaBuddy());
			messageIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
			if (yonaBuddy.getLinks() != null)
			{
				messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, yonaBuddy.getLinks().getYonaDailyActivityReports(), yonaBuddy.getLinks().getYonaWeeklyActivityReports(), 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			}
			else
			{
				messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, yonaBuddy.getEmbedded().getYonaUser().getFirstName() + " " + yonaBuddy.getEmbedded().getYonaUser().getLastName(), R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
			}
		}
		return messageIntent;
	}

	private Intent handleBuddyInfoChangeMessageClick(YonaMessage messageClicked)
	{
		Intent messageIntent = new Intent(IntentEnum.ACTION_FRIEND_PROFILE.getActionString());
		YonaHeaderTheme yonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue);
		messageIntent.putExtra(AppConstant.YONA_THEME_OBJ, yonaHeaderTheme);
		messageIntent.putExtra(AppConstant.YONAMESSAGE_OBJ, messageClicked);
		messageIntent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.NOTIFICATION, getString(R.string.friends));
		return messageIntent;
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
			User user = getSharedAppDataState().getUser();
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
