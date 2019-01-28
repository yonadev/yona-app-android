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
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.NotificationLinkData;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextViewGeneral;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.comment.CommentsAdapter;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class SingleDayActivityDetailFragment extends BaseFragment implements EventChangeListener
{

	private CustomPageAdapter customPageAdapter;
	private ViewPager viewPager;
	private DayActivity activity;
	private View view;
	private ImageView previousItem, nextItem;
	private YonaFontTextView dateTitle;
	private YonaHeaderTheme mYonaHeaderTheme;
	private List<DayActivity> dayActivityList;
	private String yonaDayDetailUrl;
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
	private YonaMessage notificationMessage;

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

	private final NestedScrollView.OnScrollChangeListener nesteadScrollistener = new NestedScrollView.OnScrollChangeListener()
	{
		@Override
		public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
		{
			if (!isUserCommenting())
			{
				View view = (View) v.getChildAt(v.getChildCount() - 1);
				int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
				if (diff == 0)
				{
					int visibleItemCount = mLayoutManager.getChildCount();
					int totalItemCount = mLayoutManager.getItemCount();
					int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
					EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
					if (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
							&& embeddedYonaActivity.getPage().getNumber() < embeddedYonaActivity.getPage().getTotalPages()
							&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
					{
						loadMoreItems();
					}
				}
			}
		}
	};

	private void loadMoreItems()
	{
		fetchComments(viewPager.getCurrentItem());
	}

	private final List<NotificationLinkData> linkList = new ArrayList<>();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null)
			{
				if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) instanceof YonaBuddy)
				{
					yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
				}
				else
				{
					yonaBuddy = APIManager.getInstance().getActivityManager().findYonaBuddy((Href) getArguments().get(AppConstant.YONA_BUDDY_OBJ));
				}
			}
			if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null)
			{
				mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
			}
			if (getArguments().get(AppConstant.YONA_DAY_DEATIL_URL) != null)
			{
				yonaDayDetailUrl = (String) getArguments().get(AppConstant.YONA_DAY_DEATIL_URL);
			}
			if (getArguments().get(AppConstant.YONA_MESSAGE) != null)
			{
				notificationMessage = (YonaMessage) getArguments().get(AppConstant.YONA_MESSAGE);
			}

			if (getArguments().get(AppConstant.URL) != null)
			{

				NotificationLinkData linkData = new NotificationLinkData();
				linkData.setUrl(getArguments().getString(AppConstant.URL, ""));

				if (getArguments().get(AppConstant.EVENT_TIME) != null)
				{
					linkData.setEventTime(getArguments().getString(AppConstant.EVENT_TIME, ""));
				}

				linkList.add(linkData);
			}
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.detail_pager_fragment, null);
		View activityRootView = view.findViewById(R.id.main_content);
		udpateBottomTabVisibility(activityRootView);

		setupToolbar(view);
		if (mYonaHeaderTheme != null)
		{
			mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
		}
		if (yonaBuddy != null)
		{
			((YonaActivity) getActivity()).updateTabIcon(true);
		}
		else
		{
			((YonaActivity) getActivity()).updateTabIcon(false);
		}

		dayActivityList = new ArrayList<>();
		previousItem = (ImageView) view.findViewById(R.id.previous);
		nextItem = (ImageView) view.findViewById(R.id.next);
		dateTitle = (YonaFontTextView) view.findViewById(R.id.date);
		commentBox = (LinearLayout) view.findViewById(R.id.comment_box);
		chatBoxImage = (ImageView) view.findViewById(R.id.comment_box_image);
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		customPageAdapter = new CustomPageAdapter(getActivity(), linkList);
		viewPager.setAdapter(customPageAdapter);
		initilizeCommentControl(view);
		setUpDayAcvitivy();
		messageTxt = (YonaFontEditTextViewGeneral) view.findViewById(R.id.userMessage);
		sendButton = (YonaFontButton) view.findViewById(R.id.btnSend);
		NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.nesteadScrollview);
		nestedScrollView.setOnScrollChangeListener(nesteadScrollistener);

		previousItem.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				previousDayActivity();
			}
		});
		nextItem.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				nextDayActivity();
			}
		});

		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!TextUtils.isEmpty(messageTxt.getText()))
				{
					if (isUserCommenting)
					{
						replyComment(messageTxt.getText().toString(), (currentReplayingMsg != null
								&& currentReplayingMsg.getLinks() != null && currentReplayingMsg.getLinks().getReplyComment() != null
								&& currentReplayingMsg.getLinks().getReplyComment().getHref() != null) ? currentReplayingMsg.getLinks().getReplyComment().getHref() : null);
					}
					else
					{
						if (activity != null && activity.getLinks() != null && activity.getLinks().getAddComment() != null && activity.getLinks().getAddComment().getHref() != null)
						{
							addComment(messageTxt.getText().toString(), activity.getLinks().getAddComment().getHref());
						}
					}
				}
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{

			}

			@Override
			public void onPageSelected(int position)
			{
				updateFlow(position);
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{

			}
		});

		if (!TextUtils.isEmpty(yonaDayDetailUrl))
		{
			setDayActivityDetails();
		}
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_DAY_ACTIVITY_DETAIL_SCREEN));
		YonaApplication.getEventChangeManager().registerListener(this);
		return view;
	}

	private void initilizeCommentControl(View view)
	{
		commentRecyclerView = (RecyclerView) view.findViewById(R.id.messageList);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mLayoutManager.setAutoMeasureEnabled(true);
		commentsAdapter = new CommentsAdapter(mYonaCommentsList, messageItemClick);
		commentRecyclerView.setLayoutManager(mLayoutManager);
		commentRecyclerView.setAdapter(commentsAdapter);
		commentRecyclerView.setNestedScrollingEnabled(false);
	}


	private void setUpDayAcvitivy()
	{
		if (getArguments() != null && getArguments().get(AppConstant.OBJECT) instanceof DayActivity)
		{
			activity = (DayActivity) getArguments().get(AppConstant.OBJECT);
		}
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	private void previousDayActivity()
	{
		if (activity != null)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.PREVIOUS);
			loadDayActivity(activity.getLinks().getPrev().getHref());
		}
	}

	private void nextDayActivity()
	{
		if (activity != null)
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN, AnalyticsConstant.NEXT);
			loadDayActivity(activity.getLinks().getNext().getHref());
		}
	}

	private void setDayActivityDetails()
	{
		loadDayActivity(yonaDayDetailUrl);
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

	private void setUserAvatarFromYonaBuddy()
	{
		if (yonaBuddy.getLinks().getUserPhoto() != null)
		{
			setUserAvatarImage(yonaBuddy.getLinks().getUserPhoto().getHref());
		}
		else
		{
			setUserAvatarFromName(yonaBuddy.retreiveNickname());
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
			setUserAvatarFromName(yonaBuddy.retreiveNickname());
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

	private void setDayDetailActivityTitle()
	{
		if (notificationMessage != null && notificationMessage.getLinks() != null && notificationMessage.getLinks().getYonaActivityCategory() != null)
		{
			String categoryName = APIManager.getInstance().getActivityManager().getActivityCategoryName(notificationMessage.getLinks().getYonaActivityCategory().getHref());
			toolbarTitle.setText(categoryName != null ? categoryName.toUpperCase() : "");
			toolbarTitle.setVisibility(View.VISIBLE);
		}
		if (activity != null && activity.getYonaGoal() != null && !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName()))
		{
			toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
		}
	}

	private void profileClickEvent(View profileView)
	{
		profileView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
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
			}
		});
	}

	private void loadDayActivity(String url)
	{
		if (url == null)
		{
			return;
		}
		YonaActivity.getActivity().showLoadingView(true, null);

		if (getLocalDayActivity(url) != null)
		{
			activity = getLocalDayActivity(url);
			updateDayActivityData(activity);
			YonaActivity.getActivity().showLoadingView(false, null);
		}
		else
		{
			APIManager.getInstance().getActivityManager().getDayDetailActivity(url, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					if (result instanceof DayActivity)
					{
						dayActivityList.add((DayActivity) result);
						activity = (DayActivity) result;
						updateDayActivityData(activity);
					}
				}

				@Override
				public void onError(Object errorMessage)
				{
					YonaActivity.getActivity().showLoadingView(false, null);
				}
			});
		}
	}

	private DayActivity getLocalDayActivity(String url)
	{
		for (DayActivity dayActivity : dayActivityList)
		{
			if (dayActivity != null && dayActivity.getLinks() != null && dayActivity.getLinks().getSelf() != null && !TextUtils.isEmpty(dayActivity.getLinks().getSelf().getHref()) && url.equalsIgnoreCase(dayActivity.getLinks().getSelf().getHref()))
			{
				return dayActivity;
			}
		}
		return null;
	}

	private void updateDayActivityData(DayActivity dayActivity)
	{
		customPageAdapter.notifyDataSetChanged(dayActivityList);
		fetchComments(dayActivityList.indexOf(activity));
		viewPager.setCurrentItem(dayActivityList.indexOf(dayActivity));
		updateFlow(dayActivityList.indexOf(dayActivity));
		YonaActivity.getActivity().showLoadingView(false, null);
		setUpYonaHeaderTheme();
		setDayDetailActivityTitle();
		setUpUserDetailsInHeader();

	}

	private void setUpYonaHeaderTheme()
	{
		if (activity.getLinks().getYonaBuddy() == null)
		{
			mYonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.grape, R.drawable.triangle_shadow_grape);
		}
		else
		{
			mYonaHeaderTheme = new YonaHeaderTheme(true, null, null, 0, 0, null, R.color.mid_blue, R.drawable.triangle_shadow_blue);
		}
		mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
	}

	private void updateFlow(int position)
	{
		if (dayActivityList != null && dayActivityList.size() > 0)
		{
			dateTitle.setText(dayActivityList.get(position).getStickyTitle());
		}

		if ((activity != null && activity.getLinks() != null && activity.getLinks().getPrev() != null && !TextUtils.isEmpty(activity.getLinks().getPrev().getHref())))
		{
			previousItem.setVisibility(View.VISIBLE);
		}
		else
		{
			previousItem.setVisibility(View.INVISIBLE);
		}
		if ((activity != null && activity.getLinks() != null && activity.getLinks().getNext() != null && !TextUtils.isEmpty(activity.getLinks().getNext().getHref())))
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
		APIManager.getInstance().getActivityManager().getComments(dayActivityList, position, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				if (result instanceof List<?>)
				{
					dayActivityList = (List<DayActivity>) result;
					customPageAdapter.notifyDataSetChanged(dayActivityList);
					updateCurrentCommentList(dayActivityList, position);
				}
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (errorMessage instanceof ErrorMessage)
				{
					YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
				}
				else
				{
					YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
				}
			}
		});
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
		APIManager.getInstance().getActivityManager().addComment(url, isreplaying, message, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				messageTxt.getText().clear();
				updateParentcommentView();
				fetchComments(viewPager.getCurrentItem());
				//TODO response will be object of YonaMessage -> add in list of comments array and notify UI to update item in list.
			}

			@Override
			public void onError(Object errorMessage)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				//TODO show proper message
			}
		});
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
		return AnalyticsConstant.DAY_ACTIVITY_DETAIL_SCREEN;
	}

}