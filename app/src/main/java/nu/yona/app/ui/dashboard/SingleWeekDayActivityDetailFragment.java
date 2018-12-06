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

import com.squareup.picasso.Picasso;

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

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class SingleWeekDayActivityDetailFragment extends BaseFragment implements EventChangeListener
{

	private CustomPageAdapter customPageAdapter;
	private ViewPager viewPager;
	private WeekActivity weekActivity;
	private View view;
	private ImageView previousItem, nextItem;
	private YonaFontTextView dateTitle;
	private List<WeekActivity> weekActivityList;
	private YonaHeaderTheme mYonaHeaderTheme;
	private YonaBuddy yonaBuddy;
	private LinearLayout commentBox;
	private String yonaWeekDetailUrl;
	private YonaFontEditTextViewGeneral messageTxt;
	private RecyclerView commentRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private List<YonaMessage> mYonaCommentsList;
	private boolean isUserCommenting = false;
	private CommentsAdapter commentsAdapter;
	private YonaMessage currentReplayingMsg;
	private ImageView chatBoxImage;
	private YonaMessage notificationMessage;

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

	private final NestedScrollView.OnScrollChangeListener nesteadScrollistener = (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
		if (!isUserCommenting())
		{
			loadMoreItems(v);
		}
	};

	private void loadMoreItems(NestedScrollView v)
	{
		View view = v.getChildAt(v.getChildCount() - 1);
		int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
		if (diff == 0)
		{
			int visibleItemCount = mLayoutManager.getChildCount();
			int totalItemCount = mLayoutManager.getItemCount();
			int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
			EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
			if (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
					&& embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()
					&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
			{
				fetchComments(viewPager.getCurrentItem());
			}
		}
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
		if (getArguments() != null)
		{
			if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null && getArguments().get(AppConstant.YONA_BUDDY_OBJ) instanceof YonaBuddy)
			{
				yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
			}
			else
			{
				yonaBuddy = APIManager.getInstance().getActivityManager().findYonaBuddy((Href) getArguments().get(AppConstant.YONA_BUDDY_OBJ));
			}
			if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null)
			{
				mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
			}
			if (getArguments().get(AppConstant.YONA_WEEK_DETAIL_URL) != null)
			{
				yonaWeekDetailUrl = (String) getArguments().get(AppConstant.YONA_WEEK_DETAIL_URL);
			}
			if (getArguments().get(AppConstant.YONA_MESSAGE) != null)
			{
				notificationMessage = (YonaMessage) getArguments().get(AppConstant.YONA_MESSAGE);
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		initializeViewAttributes(inflater);
		weekActivityList = new ArrayList<>();
		initializeCommentControl(view);
		setUpWeekActivity();
		initializeOnClickListeners();
		initializeOnPageClickListener();
		if (!TextUtils.isEmpty(yonaWeekDetailUrl))
		{
			setDayActivityDetails();
		}
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_WEEK_ACTIVITY_DETAIL_SCREEN));
		YonaApplication.getEventChangeManager().registerListener(this);
		return view;
	}

	private void initializeViewAttributes(LayoutInflater inflater)
	{
		view = inflater.inflate(R.layout.detail_pager_fragment, null);
		View activityRootView = view.findViewById(R.id.main_content);
		udpateBottomTabVisibility(activityRootView);
		setupToolbar(view);
		if (mYonaHeaderTheme != null)
		{
			mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
		}
		previousItem = view.findViewById(R.id.previous);
		nextItem = view.findViewById(R.id.next);
		dateTitle = view.findViewById(R.id.date);
		commentBox = view.findViewById(R.id.comment_box);
		chatBoxImage = view.findViewById(R.id.comment_box_image);
		messageTxt = view.findViewById(R.id.userMessage);
		viewPager = view.findViewById(R.id.viewPager);
		customPageAdapter = new CustomPageAdapter(getActivity(), itemClickListener);
		NestedScrollView nestedScrollView = view.findViewById(R.id.nesteadScrollview);
		nestedScrollView.setOnScrollChangeListener(nesteadScrollistener);
		viewPager.setAdapter(customPageAdapter);
	}

	private void initializeOnClickListeners()
	{
		previousItem.setOnClickListener(v -> previousWeekActivity());
		nextItem.setOnClickListener(v -> nextWeekActivity());
		view.findViewById(R.id.btnSend).setOnClickListener(v -> {
			if (!TextUtils.isEmpty(messageTxt.getText()))
			{
				if (isUserCommenting)
				{
					postCommentInNetworkCall(messageTxt.getText().toString(), currentReplayingMsg != null ? currentReplayingMsg.getLinks().getReplyComment().getHref() : null, true);
				}
				else
				{
					postCommentInNetworkCall(messageTxt.getText().toString(), weekActivity.getLinks().getAddComment().getHref(), false);
				}
			}
		});
	}

	private void initializeOnPageClickListener()
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
				fetchComments(position);
				updateFlow(position);
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
	}

	private void initializeCommentControl(View view)
	{
		commentRecyclerView = view.findViewById(R.id.messageList);
		mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
		mLayoutManager.setAutoMeasureEnabled(true);
		commentsAdapter = new CommentsAdapter(mYonaCommentsList, messageItemClick);
		commentRecyclerView.setLayoutManager(mLayoutManager);
		commentRecyclerView.setAdapter(commentsAdapter);
		commentRecyclerView.setNestedScrollingEnabled(false);
	}

	private void setUpWeekActivity()
	{
		if (getArguments() != null && getArguments().get(AppConstant.OBJECT) instanceof WeekActivity)
		{
			weekActivity = (WeekActivity) getArguments().get(AppConstant.OBJECT);
		}
	}

	private void previousWeekActivity()
	{
		if (weekActivity != null)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.PREVIOUS);
			loadWeekActivity(weekActivity.getLinks().getPrev().getHref());
		}
	}

	private void nextWeekActivity()
	{
		if (weekActivity != null)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.NEXT);
			loadWeekActivity(weekActivity.getLinks().getNext().getHref());
		}
	}

	private void setDayActivityDetails()
	{
		loadWeekActivity(yonaWeekDetailUrl);
	}

	private void loadWeekActivity(String url)
	{
		if (url == null)
		{
			return;
		}
		YonaActivity.getActivity().showLoadingView(true, null);

		if (getLocalWeekActivity(url) != null)
		{
			weekActivity = getLocalWeekActivity(url);
			updateDayActivityData(weekActivity);
			YonaActivity.getActivity().showLoadingView(false, null);
		}
		else
		{
			loadWeeksDetailActivity(url);
		}
	}

	private void loadWeeksDetailActivity(String url)
	{
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> handleWeeksDetailActivityOnSuccess(result)), ((result) -> handleOnError(result)), null);
		APIManager.getInstance().getActivityManager().getWeeksDetailActivity(url, dataLoadListenerImpl);

	}

	private Object handleWeeksDetailActivityOnSuccess(Object result)
	{
		if (result instanceof WeekActivity)
		{
			weekActivityList.add((WeekActivity) result);
			weekActivity = (WeekActivity) result;
			updateDayActivityData(weekActivity);
		}
		YonaActivity.getActivity().showLoadingView(false, null);
		return null;
	}

	private Object handleOnError(Object errorMessage)
	{
		if (errorMessage instanceof ErrorMessage)
		{
			YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
		}
		else
		{
			YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
		}
		YonaActivity.getActivity().showLoadingView(false, null);
		return null;
	}


	private WeekActivity getLocalWeekActivity(String url)
	{
		for (WeekActivity weekActivity : weekActivityList)
		{
			if (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getSelf() != null && !TextUtils.isEmpty(weekActivity.getLinks().getSelf().getHref()) && url.equalsIgnoreCase(weekActivity.getLinks().getSelf().getHref()))
			{
				return weekActivity;
			}
		}
		return null;
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
	}

	private void updateDayActivityData(WeekActivity weekActivity1)
	{
		customPageAdapter.notifyDataSetChanged(weekActivityList);
		fetchComments(weekActivityList.indexOf(weekActivity));
		viewPager.setCurrentItem(weekActivityList.indexOf(weekActivity1));
		updateFlow(weekActivityList.indexOf(weekActivity1));
		YonaActivity.getActivity().showLoadingView(false, null);
		setUpYonaHeaderTheme();
		setWeekDetailActivityTitle();
		setUpUserDetailsInHeader();
	}

	private void setUpYonaHeaderTheme()
	{
		if (weekActivity.getLinks().getYonaBuddy() == null)
		{
			mYonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape);
		}
		else
		{
			mYonaHeaderTheme = new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue, R.drawable.triangle_shadow_blue);
		}
		mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
	}

	private void setWeekDetailActivityTitle()
	{
		if (notificationMessage != null && notificationMessage.getLinks() != null && notificationMessage.getLinks().getYonaActivityCategory() != null)
		{
			String categoryName = APIManager.getInstance().getActivityManager().getActivityCategoryName(notificationMessage.getLinks().getYonaActivityCategory().getHref());
			toolbarTitle.setText(categoryName != null ? categoryName.toUpperCase() : "");
			toolbarTitle.setVisibility(View.VISIBLE);
		}
		if (weekActivity != null && weekActivity.getYonaGoal() != null && !TextUtils.isEmpty(weekActivity.getYonaGoal().getActivityCategoryName()))
		{
			toolbarTitle.setText(weekActivity.getYonaGoal().getActivityCategoryName().toUpperCase());
		}
	}

	private void setUpUserDetailsInHeader()
	{
		if (mYonaHeaderTheme.isBuddyFlow())
		{
			setUpUserHeaderIcon();
		}
		else
		{
			profileCircleImageView.setVisibility(View.GONE);
			rightIcon.setVisibility(View.GONE);
		}
	}

	private void setUpUserHeaderIcon()
	{
		if (notificationMessage != null)// This case comes when navigating from notifications
		{
			setUserAvatarFromNotification();
		}
		else if (yonaBuddy != null)// User ICON will not be set if yona buddy is also null.
		{
			setUserAvatarFromYonaBuddy();
		}
	}

	private void setUserAvatarFromNotification()
	{
		if (notificationMessage.getLinks().getUserPhoto() != null)
		{
			setUserAvatarImage(notificationMessage.getLinks().getUserPhoto().getHref());
		}
		else
		{
			setUserAvatarFromName(yonaBuddy.getNickname());
		}
	}

	private void setUserAvatarFromYonaBuddy()
	{
		if (yonaBuddy.getLinks().getUserPhoto() != null)
		{
			setUserAvatarImage(yonaBuddy.getLinks().getUserPhoto().getHref());
		}
		else
		{
			setUserAvatarFromName(yonaBuddy.getNickname());
		}
	}

	private void setUserAvatarImage(String userAvatarURL)
	{
		profileCircleImageView.setVisibility(View.VISIBLE);
		rightIcon.setVisibility(View.GONE);
		rightIconProfile.setVisibility(View.GONE);
		if (userAvatarURL != null)
		{
			Picasso.with(getContext()).load(userAvatarURL).noFade().into(profileCircleImageView);
		}
		profileClickEvent(profileCircleImageView);
	}

	private void setUserAvatarFromName(String nickname)
	{
		profileCircleImageView.setVisibility(View.GONE);
		rightIcon.setVisibility(View.GONE);
		rightIconProfile.setVisibility(View.VISIBLE);
		profileIconTxt.setVisibility(View.VISIBLE);
		if (nickname != null)
		{
			profileIconTxt.setText(nickname.substring(0, 1).toUpperCase());
		}
		profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
		profileClickEvent(profileIconTxt);
	}

	private void profileClickEvent(View profileView)
	{
		profileView.setOnClickListener(v -> {
			Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
			if (yonaBuddy != null)
			{
				intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue));
				intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
			}
			else
			{
				intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, R.drawable.icn_reminder, getString(R.string.dashboard), R.color.grape, R.drawable.triangle_shadow_grape));
				intent.putExtra(AppConstant.USER, YonaApplication.getEventChangeManager().getDataState().getUser());
			}
			YonaActivity.getActivity().replaceFragment(intent);
		});
	}

	private void updateFlow(int position)
	{
		if (weekActivityList != null && weekActivityList.size() > 0)
		{
			dateTitle.setText(weekActivityList.get(position).getStickyTitle());
		}

		if ((weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getPrev() != null && !TextUtils.isEmpty(weekActivity.getLinks().getPrev().getHref())))
		{
			previousItem.setVisibility(View.VISIBLE);
		}
		else
		{
			previousItem.setVisibility(View.INVISIBLE);
		}
		if ((weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getNext() != null && !TextUtils.isEmpty(weekActivity.getLinks().getNext().getHref())))
		{
			nextItem.setVisibility(View.VISIBLE);
		}
		else
		{
			nextItem.setVisibility(View.INVISIBLE);
		}

	}

	private void fetchComments(final int position)
	{
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(result -> handleAddCommentNetworkCallSuccess(result, position), (result -> handleOnError(result)), null);
		APIManager.getInstance().getActivityManager().getCommentsForWeek(weekActivityList, position, dataLoadListenerImpl);
	}

	private Object handleAddCommentNetworkCallSuccess(Object result, int position)
	{
		if (result instanceof List<?>)
		{
			weekActivityList = (List<WeekActivity>) result;
			customPageAdapter.notifyDataSetChanged(weekActivityList);
			updateCurrentCommentList(weekActivityList, position);
			YonaActivity.getActivity().showLoadingView(false, null);
		}
		return null;
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
	private void postCommentInNetworkCall(String message, String url, boolean isReplying)
	{
		clearViewWhenUserCommenting();
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result -> handleOnAddCommentSuccess()), (result -> handleOnError(result)), null);
		APIManager.getInstance().getActivityManager().addComment(url, isReplying, message, dataLoadListenerImpl);
	}

	private Object handleOnAddCommentSuccess()
	{
		messageTxt.getText().clear();
		updateParentcommentView();
		fetchComments(viewPager.getCurrentItem());
		YonaActivity.getActivity().showLoadingView(false, null);
		return null;
	}

	private void clearViewWhenUserCommenting()
	{
		YonaActivity.getActivity().showLoadingView(true, null);
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.WEEK_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.SEND);
		if (weekActivity != null && weekActivity.getComments() != null)
		{
			if (weekActivity.getComments().getPage() != null)
			{
				weekActivity.getComments().setPage(null);
			}
			if (weekActivity.getComments().getEmbedded() != null && weekActivity.getComments().getEmbedded().getYonaMessages() != null)
			{
				weekActivity.getComments().getEmbedded().getYonaMessages().clear();
			}
		}
	}

	public void updateParentcommentView()
	{
		setUserCommenting(false);
		if (weekActivity != null && weekActivity.getComments() != null && weekActivity.getComments().getEmbedded() != null && weekActivity.getComments().getEmbedded().getYonaMessages() != null)
		{
			mYonaCommentsList = weekActivity.getComments().getEmbedded().getYonaMessages();
		}
		commentsAdapter.notifyDatasetChanged(mYonaCommentsList);
		if (!mYonaHeaderTheme.isBuddyFlow() && yonaBuddy == null)
		{
			commentBox.setVisibility(View.GONE);
		}
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
		if (commentsAdapter != null)
		{
			List<YonaMessage> yonaMessages = new ArrayList<>();
			yonaMessages.add(currentMsg);
			this.mYonaCommentsList = yonaMessages;
			commentsAdapter.notifyDatasetChanged(yonaMessages);
		}
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