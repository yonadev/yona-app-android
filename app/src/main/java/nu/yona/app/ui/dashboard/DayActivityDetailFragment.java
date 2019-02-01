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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextViewGeneral;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.comment.CommentsAdapter;
import nu.yona.app.utils.AppConstant;

import static nu.yona.app.YonaApplication.getUserFromDB;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class DayActivityDetailFragment extends BaseFragment implements EventChangeListener
{

	private CustomPageAdapter customPageAdapter;
	private ViewPager viewPager;
	private DayActivity activity;
	private View view;
	private ImageView previousItem, nextItem;
	private YonaFontTextView dateTitle;
	private List<DayActivity> dayActivityList;
	private YonaHeaderTheme mYonaHeaderTheme;
	private YonaBuddy yonaBuddy;
	private LinearLayout commentBox;
	private YonaFontEditTextViewGeneral messageTxt;
	private YonaFontButton sendButton;
	private boolean isUserCommenting = false;
	private RecyclerView commentRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private List<YonaMessage> mYonaCommentsList;
	private CommentsAdapter commentsAdapter;
	private YonaMessage currentReplayingMsg;
	private ImageView chatBoxImage;
	private boolean isDataLoading = false;

	private final View.OnClickListener messageItemClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (v.getTag() instanceof YonaMessage)
			{
				YonaMessage currentMsg = (YonaMessage) v.getTag();
				//Todo update ui of current selected page of adapter
				if (customPageAdapter != null)
				{
					setUserCommenting(true);
					currentReplayingMsg = currentMsg;
					visibleAddCommentView(currentReplayingMsg);
				}
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SHOW_CHAT_OPTION, null);
			}
		}
	};

	private final NestedScrollView.OnScrollChangeListener nestedScrollListener = (nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY) -> {
		if (!isUserCommenting())
		{
			loadMoreItems(nestedScrollView);
		}
	};

	private void loadMoreItems(NestedScrollView nestedScrollView)
	{
		View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
		int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
		if (diff == 0)
		{
			int visibleItemCount = mLayoutManager.getChildCount();
			int totalItemCount = mLayoutManager.getItemCount();
			int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
			EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
			if (isViewScrollable(embeddedYonaActivity, visibleItemCount, firstVisibleItemPosition, totalItemCount))
			{
				fetchComments(viewPager.getCurrentItem());
			}
		}
	}

	private boolean isViewScrollable(EmbeddedYonaActivity embeddedYonaActivity, int visibleItemCount, int firstVisibleItemPosition, int totalItemCount)
	{
		return (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
				&& embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()
				&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		yonaBuddy = getArgument(AppConstant.YONA_BUDDY_OBJ, YonaBuddy.class, yonaBuddy);
		mYonaHeaderTheme = getArgument(AppConstant.YONA_THEME_OBJ, YonaHeaderTheme.class, mYonaHeaderTheme);
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		initializeViewAttributes(inflater);
		initializeCommentControl(view);
		activity = getArgument(AppConstant.OBJECT, DayActivity.class, activity);
		initializeOnClickListeners();
		initializeOnPageClickListener();
		YonaApplication.getEventChangeManager().registerListener(this);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_DAY_ACTIVITY_DETAIL_SCREEN));
		return view;
	}

	private void initializeViewAttributes(LayoutInflater inflater)
	{
		view = inflater.inflate(R.layout.detail_pager_fragment, null);
		View activityRootView = view.findViewById(R.id.main_content);
		udpateBottomTabVisibility(activityRootView);
		setUpViewComponents();
	}

	private void setUpViewComponents()
	{
		setUpViewToolBar();
		previousItem = view.findViewById(R.id.previous);
		nextItem = view.findViewById(R.id.next);
		dateTitle = view.findViewById(R.id.date);
		commentBox = view.findViewById(R.id.comment_box);
		chatBoxImage = view.findViewById(R.id.comment_box_image);
		messageTxt = view.findViewById(R.id.userMessage);
		sendButton = view.findViewById(R.id.btnSend);
		setUpViewPager();
		NestedScrollView nestedScrollView = view.findViewById(R.id.nesteadScrollview);
		nestedScrollView.setOnScrollChangeListener(nestedScrollListener);
	}

	private void setUpViewToolBar()
	{
		setupToolbar(view);
		if (mYonaHeaderTheme != null)
		{
			mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
		}
	}

	private void setUpViewPager()
	{
		viewPager = view.findViewById(R.id.viewPager);
		customPageAdapter = new CustomPageAdapter(getActivity());
		viewPager.setAdapter(customPageAdapter);
	}

	private void initializeOnClickListeners()
	{
		previousItem.setOnClickListener(view -> showPreviousDayActivity());
		nextItem.setOnClickListener(view -> showNextDayActivity());
		sendButton.setOnClickListener(view -> setOnClickListenerSendButton());
	}

	private void initializeOnPageClickListener()
	{
		if (viewPager == null)
		{
			return;
		}
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageSelected(int position)
			{
				EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
				if (embeddedYonaActivity != null && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0)
				{
					DayActivity newDayActivityToLoad = dayActivityList.get(position);
					getCurrentDayActivityDetails(newDayActivityToLoad);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
	}

	private void showPreviousDayActivity()
	{
		if (viewPager.getCurrentItem() != 0)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.PREVIOUS);
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
		}
	}

	private void showNextDayActivity()
	{
		if (viewPager.getCurrentItem() != dayActivityList.size() - 1)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.NEXT);
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
		}
	}

	private void setOnClickListenerSendButton()
	{
		if (TextUtils.isEmpty(messageTxt.getText()))
		{
			return;
		}
		if (isUserCommenting())
		{
			replyComment(messageTxt.getText().toString(), currentReplayingMsg != null ? currentReplayingMsg.getLinks().getReplyComment().getHref() : null);
		}
		else
		{
			addComment(messageTxt.getText().toString(), activity.getLinks().getAddComment().getHref());
		}
	}

	private void initializeCommentControl(View view)
	{
		commentRecyclerView = view.findViewById(R.id.messageList);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		mLayoutManager.setAutoMeasureEnabled(true);
		commentsAdapter = new CommentsAdapter(mYonaCommentsList, messageItemClick);
		commentRecyclerView.setLayoutManager(mLayoutManager);
		commentRecyclerView.setAdapter(commentsAdapter);
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
		getCurrentDayActivityDetails(activity);
	}

	public void getCurrentDayActivityDetails(DayActivity dayActivity)
	{
		if (isDataLoading)
		{
			return;
		}
		YonaActivity.getActivity().showLoadingView(true, null);
		isDataLoading = true;
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleDetailOfEachSpreadWithDayActivityFetchSuccess(result), (result) -> handleDetailOfEachSpreadWithDayActivityFetchFailure(result), null);
		APIManager.getInstance().getActivityManager().getDetailOfEachSpreadWithDayActivity(dayActivity, dataLoadListenerImpl);
	}

	private Object handleDetailOfEachSpreadWithDayActivityFetchSuccess(Object result)
	{
		activity = (DayActivity) result;
		isDataLoading = false;
		YonaActivity.getActivity().showLoadingView(false, null);
		setDayActivityDetails();
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object handleDetailOfEachSpreadWithDayActivityFetchFailure(Object errorMessage)
	{
		isDataLoading = false;
		YonaActivity.getActivity().showLoadingView(false, null);
		YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		return null; // Dummy return value, to allow use as data error handler
	}

	private void setDayActivityDetails()
	{
		dayActivityList = new ArrayList<>();
		EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
		if (embeddedYonaActivity != null && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0)
		{
			setUpDayActivityListFromEmbeddedYonaActivity(embeddedYonaActivity);
			fetchCommentsForCurrentIndex();
		}
		else
		{
			goBack();
		}
		setDayDetailTitleAndIcon();
	}

	private void setUpDayActivityListFromEmbeddedYonaActivity(EmbeddedYonaActivity embeddedYonaActivity)
	{

		for (int i = embeddedYonaActivity.getDayActivityList().size() - 1; i >= 0; i--)
		{
			if (embeddedYonaActivity.getDayActivityList().get(i).getYonaGoal().getLinks().getSelf().getHref().equals(activity.getLinks().getYonaGoal().getHref()))
			{
				dayActivityList.add(embeddedYonaActivity.getDayActivityList().get(i));
			}
		}
	}

	private void fetchCommentsForCurrentIndex()
	{
		int itemIndex = getIndex(activity);
		if (itemIndex >= 0)
		{
			customPageAdapter.notifyDataSetChanged(dayActivityList);
			if (itemIndex != viewPager.getCurrentItem())
			{
				viewPager.setCurrentItem(itemIndex);
			}
			else
			{
				fetchComments(itemIndex);
			}
			updateFlow(itemIndex);
		}
		else
		{
			goBack();
		}
	}

	private void goBack()
	{
		new Handler().postDelayed(() -> YonaActivity.getActivity().onBackPressed(), AppConstant.ONE_SECOND);
	}

	private int getIndex(DayActivity selectedActivity)
	{
		if (isDayActivityHrefNotNull(selectedActivity))
		{
			String selectedUrl = selectedActivity.getLinks().getSelf().getHref();
			for (int index = 0; index < dayActivityList.size(); index++)
			{
				if (isSelectedUrlEqualsDayActivityHref(index, selectedUrl))
				{
					return index;
				}
			}
		}
		return -1;
	}

	private boolean isDayActivityHrefNotNull(DayActivity selectedActivity)
	{
		return (dayActivityList != null && selectedActivity != null && selectedActivity.getLinks() != null && selectedActivity.getLinks().getSelf() != null
				&& !TextUtils.isEmpty(selectedActivity.getLinks().getSelf().getHref()));
	}

	private boolean isSelectedUrlEqualsDayActivityHref(int index, String selectedUrl)
	{
		return (dayActivityList.get(index).getLinks() != null && dayActivityList.get(index).getLinks().getSelf() != null
				&& !TextUtils.isEmpty(dayActivityList.get(index).getLinks().getSelf().getHref())
				&& selectedUrl.equals(dayActivityList.get(index).getLinks().getSelf().getHref()));
	}

	private void setDayDetailTitleAndIcon()
	{

		if (mYonaHeaderTheme.isBuddyFlow() && yonaBuddy != null)
		{
			setProfileIconForBuddy();
		}
		else
		{
			setProfileIconForUser();
		}
		if (activity != null && activity.getYonaGoal() != null && !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()))
		{
			toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		}
	}

	private void setProfileIconForBuddy()
	{
		profileCircleImageView.setVisibility(View.GONE);
		rightIcon.setVisibility(View.GONE);
		rightIconProfile.setVisibility(View.VISIBLE);
		if (yonaBuddy.getEmbedded() != null && yonaBuddy.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(yonaBuddy.getEmbedded().getYonaUser().getFirstName()))
		{
			profileIconTxt.setVisibility(View.VISIBLE);
			profileIconTxt.setText(yonaBuddy.getEmbedded().getYonaUser().getFirstName().substring(0, 1).toUpperCase());
			profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
			profileClickEvent(profileIconTxt);
		}
	}

	private void setProfileIconForUser()
	{
		profileCircleImageView.setVisibility(View.GONE);
		rightIcon.setVisibility(View.GONE);
		if (mYonaHeaderTheme.isBuddyFlow())
		{
			profileIconTxt.setVisibility(View.VISIBLE);
			profileIconTxt.setText(yonaBuddy.getEmbedded().getYonaUser().getFirstName().substring(0, 1).toUpperCase());
			profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
			profileClickEvent(profileIconTxt);
		}
	}

	private void profileClickEvent(View profileView)
	{
		profileView.setOnClickListener(v -> {
			Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
			if (getArguments() != null)
			{
				intent.putExtras(getArguments());
			}
			intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
			if (yonaBuddy != null)
			{
				intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
			}
			else
			{
				intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, R.drawable.icn_reminder, getString(R.string.dashboard), R.color.grape, R.drawable.triangle_shadow_grape));
				intent.putExtra(AppConstant.USER, getUserFromDB());
			}
			YonaActivity.getActivity().replaceFragment(intent);
		});
	}

	private void updateFlow(int position)
	{
		dateTitle.setText(dayActivityList.get(position).getStickyTitle());
		if (position == 0)
		{
			previousItem.setVisibility(View.INVISIBLE);
		}
		else
		{
			previousItem.setVisibility(View.VISIBLE);
		}
		if (position == dayActivityList.size() - 1)
		{
			nextItem.setVisibility(View.INVISIBLE);
		}
		else
		{
			nextItem.setVisibility(View.VISIBLE);
		}
	}

	private void fetchComments(final int position)
	{
		DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result) -> handleGetCommentsFetchSuccess(result, position), (result) -> handleErrorMessage(result), null);
		APIManager.getInstance().getActivityManager().getComments(dayActivityList, position, dataLoadListener);
	}

	private Object handleGetCommentsFetchSuccess(Object result, int position)
	{
		if (result instanceof List<?>)
		{
			dayActivityList = (List<DayActivity>) result;
			customPageAdapter.notifyDataSetChanged(dayActivityList, position);
			updateCurrentCommentList(dayActivityList, position);
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private void updateCurrentCommentList(List<DayActivity> dayActivityList, int position)
	{
		DayActivity mDayActivity = dayActivityList.get(position);
		if (mDayActivity != null && mDayActivity.getComments() != null && mDayActivity.getComments().getEmbedded() != null && mDayActivity.getComments().getEmbedded().getYonaMessages() != null)
		{
			this.mYonaCommentsList = mDayActivity.getComments().getEmbedded().getYonaMessages();
		}
		else
		{
			this.mYonaCommentsList = null;
		}
		if (mYonaCommentsList != null && mYonaCommentsList.size() > 0)
		{
			chatBoxImage.setVisibility(View.VISIBLE);
		}
		else
		{
			chatBoxImage.setVisibility(View.GONE);
		}
		commentsAdapter.notifyDatasetChanged(mYonaCommentsList);
	}

	//TODO @Bhargav, when user click on send button from comment box, it will call this API.

	private void addComment(String message, String url)
	{
		doComment(message, url, false);
	}

	private void replyComment(String message, String url)
	{
		doComment(message, url, true);
	}

	private void doComment(String message, String url, boolean isreplaying)
	{
		YonaActivity.getActivity().showLoadingView(true, null);
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.SEND);
		if (activity != null && activity.getComments() != null)
		{
			if (activity.getComments().getPage() != null)
			{
				activity.getComments().setPage(null);
			}
			if (activity.getComments().getEmbedded() != null && activity.getComments().getEmbedded().getYonaMessages() != null)
			{
				activity.getComments().getEmbedded().getYonaMessages().clear();
			}
		}
		DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result) -> handleOnAddCommentSuccess(), (result) -> handleErrorMessage(result), null);
		APIManager.getInstance().getActivityManager().addComment(url, isreplaying, message, dataLoadListener);
		YonaActivity.getActivity().showLoadingView(false, null);
	}

	private Object handleOnAddCommentSuccess()
	{
		messageTxt.getText().clear();
		updateParentcommentView();
		fetchComments(viewPager.getCurrentItem());
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object handleErrorMessage(Object errorMessage)
	{
		if (errorMessage instanceof ErrorMessage)
		{
			YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		}
		else
		{
			YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
		}
		return null; // Dummy return value, to allow use as data error handler
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_SHOW_CHAT_OPTION:
				commentBox.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}

	public void visibleAddCommentView(YonaMessage currentMsg)
	{
		if (commentsAdapter == null)
		{
			return;
		}
		List<YonaMessage> yonaMessages = new ArrayList<>();
		yonaMessages.add(currentMsg);
		this.mYonaCommentsList = yonaMessages;
		commentsAdapter.notifyDatasetChanged(yonaMessages);
	}

	public boolean isUserCommenting()
	{
		return isUserCommenting;
	}

	public void updateParentcommentView()
	{
		setUserCommenting(false);
		if (activity != null && activity.getComments() != null && activity.getComments().getEmbedded() != null && activity.getComments().getEmbedded().getYonaMessages() != null)
		{
			mYonaCommentsList = activity.getComments().getEmbedded().getYonaMessages();
		}
		commentsAdapter.notifyDatasetChanged(mYonaCommentsList);
		if (!mYonaHeaderTheme.isBuddyFlow() && yonaBuddy == null)
		{
			commentBox.setVisibility(View.GONE);
		}
	}

	public void setUserCommenting(boolean userCommenting)
	{
		isUserCommenting = userCommenting;
	}


	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN;
	}
}
