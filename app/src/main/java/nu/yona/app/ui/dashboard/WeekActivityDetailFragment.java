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
import nu.yona.app.api.model.Day;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
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
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getAppUser;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class WeekActivityDetailFragment extends BaseFragment implements EventChangeListener
{

	private CustomPageAdapter customPageAdapter;
	private ViewPager viewPager;
	private WeekActivity activity;
	private View view;
	private ImageView previousItem, nextItem;
	private YonaFontTextView dateTitle;
	private List<WeekActivity> weekActivityList;
	private YonaHeaderTheme mYonaHeaderTheme;
	private YonaBuddy yonaBuddy;
	private LinearLayout commentBox;
	private YonaFontEditTextViewGeneral messageTxt;
	private RecyclerView commentRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private List<YonaMessage> mYonaCommentsList;
	private boolean isUserCommenting = false;
	private CommentsAdapter commentsAdapter;
	private YonaMessage currentReplayingMsg;
	private ImageView chatBoxImage;
	private boolean isDataLoading = false;

	private final View.OnClickListener itemClickListener = v -> {
		switch (v.getId())
		{
			case R.id.circle_view:
				Day day = (Day) v.getTag(R.integer.day_key);
				if (day != null)
				{
					openDetailPage(day);
				}
				break;
			default:
				break;
		}
	};

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
					isUserCommenting = true;
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
			EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
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

	private void openDetailPage(Day activity)
	{
		Intent weekDayIntent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
		weekDayIntent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, activity.getLinks().getYonaDayDetails().getHref());
		weekDayIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
		weekDayIntent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
		YonaActivity.getActivity().replaceFragment(weekDayIntent);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		yonaBuddy = getArgument(AppConstant.YONA_BUDDY_OBJ, YonaBuddy.class);
		if (yonaBuddy == null && getArgument(AppConstant.YONA_BUDDY_OBJ, Href.class) != null)
		{
			yonaBuddy = APIManager.getInstance().getActivityManager().findYonaBuddy(getArgument(AppConstant.YONA_BUDDY_OBJ, Href.class));
		}
		mYonaHeaderTheme = getArgument(AppConstant.YONA_THEME_OBJ, YonaHeaderTheme.class, mYonaHeaderTheme);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.detail_pager_fragment, null);
		View activityRootView = view.findViewById(R.id.main_content);
		udpateBottomTabVisibility(activityRootView);
		setUpViewToolBar();
		setupViewComponents();
		viewPager.setAdapter(customPageAdapter);
		initalizeCommentControl(view);
		activity = getArgument(AppConstant.OBJECT, WeekActivity.class, activity);
		setUpOnClickListeners();
		setUpOnPageChangeListener();
		YonaApplication.getEventChangeManager().registerListener(this);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_WEEK_ACTIVITY_DETAIL_SCREEN));
		return view;
	}

	private void setUpViewToolBar()
	{
		setupToolbar(view);
		if (mYonaHeaderTheme != null)
		{
			mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
		}
	}

	private void setupViewComponents()
	{
		previousItem = view.findViewById(R.id.previous);
		nextItem = view.findViewById(R.id.next);
		dateTitle = view.findViewById(R.id.date);
		commentBox = view.findViewById(R.id.comment_box);
		chatBoxImage = view.findViewById(R.id.comment_box_image);
		messageTxt = view.findViewById(R.id.userMessage);
		viewPager = view.findViewById(R.id.viewPager);
		customPageAdapter = new CustomPageAdapter(getActivity(), itemClickListener);
		NestedScrollView nestedScrollView = view.findViewById(R.id.nesteadScrollview);
		nestedScrollView.setOnScrollChangeListener(nestedScrollListener);
	}

	private void setUpOnClickListeners()
	{
		previousItem.setOnClickListener(view -> previousWeekActivity());
		nextItem.setOnClickListener(view -> nextWeekActivity());
		view.findViewById(R.id.btnSend).setOnClickListener(view -> setOnClickListenerSendButton());
	}

	private void nextWeekActivity()
	{
		if (viewPager.getCurrentItem() != weekActivityList.size() - 1)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.NEXT);
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
		}
	}

	private void previousWeekActivity()
	{
		if (viewPager.getCurrentItem() == 0)
		{
			return;
		}
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.PREVIOUS);
		viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
	}

	private void setOnClickListenerSendButton()
	{
		if (TextUtils.isEmpty(messageTxt.getText()))
		{
			return;
		}
		if (isUserCommenting)
		{
			replyComment(messageTxt.getText().toString(), currentReplayingMsg != null ? currentReplayingMsg.getLinks().getReplyComment().getHref() : null);
		}
		else
		{
			addComment(messageTxt.getText().toString(), activity.getLinks().getAddComment().getHref());
		}
	}

	private void setUpOnPageChangeListener()
	{
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageSelected(int position)
			{
				loadCurrentPositionWeekActivity(position);
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
	}

	private void loadCurrentPositionWeekActivity(int position)
	{
		EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
		if (embeddedYonaActivity != null && embeddedYonaActivity.getWeekActivityList() != null && embeddedYonaActivity.getWeekActivityList().size() > 0)
		{
			WeekActivity newWeekActivityToLoad = weekActivityList.get(position);
			getCurrentWeekActivityDetails(newWeekActivityToLoad);
		}
	}

	private void initalizeCommentControl(View view)
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
		getCurrentWeekActivityDetails(activity);
	}

	public void getCurrentWeekActivityDetails(WeekActivity weekActivity)
	{
		YonaActivity.getActivity().displayLoadingView();
		if (isDataLoading)
		{
			return;
		}
		isDataLoading = true;
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> handleWeekActivityDetailsFetchSuccess((WeekActivity) result)), ((result) -> handleWeekActivityDetailsFetchFailure(result)), null);
		APIManager.getInstance().getActivityManager().getDetailOfEachWeekSpreadWithWeekActivity(weekActivity, dataLoadListenerImpl);
	}

	private Object handleWeekActivityDetailsFetchSuccess(WeekActivity result)
	{
		try
		{
			YonaActivity.getActivity().dismissLoadingView();
			isDataLoading = false;
			setupWeekActivityListFromEmbeddedYonaActivity(result);
			setDayDetailTitleAndIcon();
		}
		catch (NullPointerException e)
		{
			AppUtils.reportException(WeekActivityDetailFragment.class, e, Thread.currentThread());
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object handleWeekActivityDetailsFetchFailure(Object errorMessage)
	{
		isDataLoading = false;
		YonaActivity.getActivity().dismissLoadingView();
		YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		return null; // Dummy return value, to allow use as data error handler
	}

	private void setupWeekActivityListFromEmbeddedYonaActivity(WeekActivity result)
	{
		activity = result;
		weekActivityList = new ArrayList<>();
		EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
		if (embeddedYonaActivity != null && embeddedYonaActivity.getWeekActivityList() != null && embeddedYonaActivity.getWeekActivityList().size() > 0)
		{
			for (int i = embeddedYonaActivity.getWeekActivityList().size() - 1; i >= 0; i--)
			{
				if (embeddedYonaActivity.getWeekActivityList().get(i).getYonaGoal().getLinks().getSelf().getHref().equals(activity.getLinks().getYonaGoal().getHref()))
				{
					weekActivityList.add(embeddedYonaActivity.getWeekActivityList().get(i));
				}
			}
			fetchCommentsForCurrentIndex();
		}
		else
		{
			goBack();
		}
	}

	private void fetchCommentsForCurrentIndex()
	{
		int itemIndex = getIndex(activity);
		if (itemIndex >= 0)
		{
			customPageAdapter.notifyDataSetChanged(weekActivityList);
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

	private int getIndex(WeekActivity selectedActivity)
	{
		if (isWeekActivityHrefNotNull(selectedActivity))
		{
			String selectedUrl = selectedActivity.getLinks().getSelf().getHref();
			for (int index = 0; index < weekActivityList.size(); index++)
			{
				if (isSelectedUrlEqualsWeekActivityHref(index, selectedUrl))
				{
					return index;
				}
			}
		}
		return -1;
	}

	private boolean isWeekActivityHrefNotNull(WeekActivity selectedActivity)
	{
		return (weekActivityList != null && selectedActivity != null && selectedActivity.getLinks() != null && selectedActivity.getLinks().getSelf() != null
				&& !TextUtils.isEmpty(selectedActivity.getLinks().getSelf().getHref()));
	}

	private boolean isSelectedUrlEqualsWeekActivityHref(int index, String selectedUrl)
	{
		return (weekActivityList.get(index).getLinks() != null && weekActivityList.get(index).getLinks().getSelf() != null
				&& !TextUtils.isEmpty(weekActivityList.get(index).getLinks().getSelf().getHref())
				&& selectedUrl.equals(weekActivityList.get(index).getLinks().getSelf().getHref()));
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
		toolbarTitle.setText(mYonaHeaderTheme.getHeader_title());
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
		if (!mYonaHeaderTheme.isBuddyFlow())
		{
			return;
		}
		profileIconTxt.setVisibility(View.VISIBLE);
		profileIconTxt.setText(yonaBuddy.getEmbedded().getYonaUser().getFirstName().substring(0, 1).toUpperCase());
		profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
		profileClickEvent(profileIconTxt);
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
				intent.putExtra(AppConstant.USER, getAppUser());
			}
			YonaActivity.getActivity().replaceFragment(intent);
		});
	}

	private void updateFlow(int position)
	{
		dateTitle.setText(weekActivityList.get(position).getStickyTitle());
		if (position == 0)
		{
			previousItem.setVisibility(View.INVISIBLE);
		}
		else
		{
			previousItem.setVisibility(View.VISIBLE);
		}
		if (position == weekActivityList.size() - 1)
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
		APIManager.getInstance().getActivityManager().getCommentsForWeek(weekActivityList, position, dataLoadListener);
	}

	private Object handleGetCommentsFetchSuccess(Object result, int position)
	{
		if (result instanceof List<?>)
		{
			weekActivityList = (List<WeekActivity>) result;
			customPageAdapter.notifyDataSetChanged(weekActivityList, position);
			updateCurrentCommentList(weekActivityList, position);
		}
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
		YonaActivity.getActivity().dismissLoadingView();
		return null; // Dummy return value, to allow use as data error handler
	}

	private void updateCurrentCommentList(List<WeekActivity> weekActivities, int position)
	{
		WeekActivity mWeekActivity = weekActivities.get(position);
		if (mWeekActivity != null && mWeekActivity.getComments() != null && mWeekActivity.getComments().getEmbedded() != null && mWeekActivity.getComments().getEmbedded().getYonaMessages() != null)
		{
			this.mYonaCommentsList = mWeekActivity.getComments().getEmbedded().getYonaMessages();
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
		YonaActivity.getActivity().displayLoadingView();
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.SEND);
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
	}

	private Object handleOnAddCommentSuccess()
	{
		messageTxt.getText().clear();
		updateParentcommentView();
		fetchComments(viewPager.getCurrentItem());
		YonaActivity.getActivity().dismissLoadingView();
		return null; // Dummy return value, to allow use as data load handler
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

	public void setUserCommenting(boolean userCommenting)
	{
		isUserCommenting = userCommenting;
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN;
	}
}
