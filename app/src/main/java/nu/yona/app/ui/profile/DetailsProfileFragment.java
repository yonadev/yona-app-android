/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.profile;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DetailsProfileFragment extends BaseProfileFragment implements EventChangeListener
{

	private YonaFontEditTextView firstName, lastName, nickName;
	private YonaFontNumberTextView mobileNumber;
	private YonaFontButton removeFriendButton;
	private User user;
	private YonaBuddy buddyUser;
	private YonaMessage yonaMessage;
	private String mUrl;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.profile_details_fragment, null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			YonaActivity.getActivity().postponeEnterTransition();
		}
		firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
		lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
		nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
		mobileNumber = (YonaFontNumberTextView) view.findViewById(R.id.mobile_number);

		removeFriendButton = (YonaFontButton) view.findViewById(R.id.removeFriendButton);

		YonaApplication.getEventChangeManager().registerListener(this);
		if (getArguments() != null)
		{
			if (getArguments().get(AppConstant.USER) != null)
			{
				user = (User) getArguments().get(AppConstant.USER);
			}
			else if (getArguments().get(AppConstant.YONAMESSAGE_OBJ) != null)
			{
				yonaMessage = (YonaMessage) getArguments().get(AppConstant.YONAMESSAGE_OBJ);
			}
			else if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null)
			{
				buddyUser = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
			}
			else if (getArguments().getString(AppConstant.URL) != null)
			{
				mUrl = getArguments().getString(AppConstant.URL);
			}
		}


		if (yonaMessage != null || buddyUser != null)
		{
			((YonaActivity) getActivity()).updateTabIcon(true);
		}
		else
		{
			((YonaActivity) getActivity()).updateTabIcon(false);
		}
		return view;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		profileViewMode();
	}

	private void profileViewMode()
	{
		String number;

		firstName.setClickable(false);
		firstName.setFocusable(false);

		lastName.setClickable(false);
		lastName.setFocusable(false);

		nickName.setClickable(false);
		nickName.setFocusable(false);

		if (user != null && yonaMessage != null)
		{
			firstName.setText(TextUtils.isEmpty(user.getFirstName()) ? getString(R.string.blank) : user.getFirstName());
			lastName.setText(TextUtils.isEmpty(user.getLastName()) ? getString(R.string.blank) : user.getLastName());
			nickName.setText(TextUtils.isEmpty(yonaMessage.getNickname()) ? getString(R.string.blank) : yonaMessage.getNickname());
			number = user.getMobileNumber();
		}
		else if (user != null && mUrl != null)
		{
			firstName.setText(TextUtils.isEmpty(user.getEmbedded().getYonaUser().getFirstName()) ? getString(R.string.blank) : user.getEmbedded().getYonaUser().getFirstName());
			lastName.setText(TextUtils.isEmpty(user.getEmbedded().getYonaUser().getLastName()) ? getString(R.string.blank) : user.getEmbedded().getYonaUser().getLastName());
			nickName.setText(TextUtils.isEmpty(user.getNickname()) ? getString(R.string.blank) : user.getNickname());
			number = TextUtils.isEmpty(user.getEmbedded().getYonaUser().getMobileNumber()) ? getString(R.string.blank) : user.getEmbedded().getYonaUser().getMobileNumber();
		}
		else if (user != null)
		{
			firstName.setText(TextUtils.isEmpty(user.getFirstName()) ? getString(R.string.blank) : user.getFirstName());
			lastName.setText(TextUtils.isEmpty(user.getLastName()) ? getString(R.string.blank) : user.getLastName());
			nickName.setText(TextUtils.isEmpty(user.getNickname()) ? getString(R.string.blank) : user.getNickname());
			number = user.getMobileNumber();
		}
		else if (yonaMessage != null && yonaMessage.getEmbedded() != null && yonaMessage.getEmbedded().getYonaUser() != null)
		{
			firstName.setText(TextUtils.isEmpty(yonaMessage.getEmbedded().getYonaUser().getFirstName()) ? getString(R.string.blank) : yonaMessage.getEmbedded().getYonaUser().getFirstName());
			lastName.setText(TextUtils.isEmpty(yonaMessage.getEmbedded().getYonaUser().getLastName()) ? getString(R.string.blank) : yonaMessage.getEmbedded().getYonaUser().getLastName());
			nickName.setText(TextUtils.isEmpty(yonaMessage.getNickname()) ? getString(R.string.blank) : yonaMessage.getNickname());
			number = TextUtils.isEmpty(yonaMessage.getEmbedded().getYonaUser().getMobileNumber()) ? getString(R.string.blank) : yonaMessage.getEmbedded().getYonaUser().getMobileNumber();
		}
		else if (buddyUser != null && buddyUser.getEmbedded() != null && buddyUser.getEmbedded().getYonaUser() != null)
		{
			firstName.setText(TextUtils.isEmpty(buddyUser.getEmbedded().getYonaUser().getFirstName()) ? getString(R.string.blank) : buddyUser.getEmbedded().getYonaUser().getFirstName());
			lastName.setText(TextUtils.isEmpty(buddyUser.getEmbedded().getYonaUser().getLastName()) ? getString(R.string.blank) : buddyUser.getEmbedded().getYonaUser().getLastName());
			nickName.setText(TextUtils.isEmpty(buddyUser.getNickname()) ? getString(R.string.blank) : buddyUser.getNickname());
			number = TextUtils.isEmpty(buddyUser.getEmbedded().getYonaUser().getMobileNumber()) ? getString(R.string.blank) : buddyUser.getEmbedded().getYonaUser().getMobileNumber();
		}
		else
		{
			number = null;
		}
		if (!isNull(buddyUser))
		{
			removeFriendButton.setVisibility(View.VISIBLE);
			removeFriendButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					deleteBuddy();
				}
			});
		}
		else
		{
			removeFriendButton.setVisibility(View.GONE);
		}

		if (yonaMessage != null)
		{
			((YonaActivity) getActivity()).updateTabIcon(true);
		}

		mobileNumber.setClickable(false);
		mobileNumber.setFocusable(false);


		mobileNumber.setText(number);
	}

	private void deleteBuddy()
	{
		YonaActivity.getActivity().showLoadingView(true, null);
		APIManager.getInstance().getBuddyManager().deleteBuddy(buddyUser, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				YonaActivity.getActivity().onBackPressed();
				YonaActivity.getActivity().onBackPressed();
				getSharedAppDataState().setEmbeddedWithBuddyActivity(null);
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_FRIEND_TIMELINE, null);
						YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_FRIEND_OVERVIEW, null);
					}
				}, AppConstant.TIMER_DELAY);
			}

			@Override
			public void onError(Object errorMessage)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				YonaActivity.getActivity().onBackPressed();
			}
		});
	}

	private boolean isNull(YonaBuddy buddyUser)
	{
		return (buddyUser == null || buddyUser.getLinks() == null || buddyUser.getLinks().getSelf() == null
				|| TextUtils.isEmpty(buddyUser.getLinks().getSelf().getHref()));
	}

	@Override
	public void onStateChange(int eventType, final Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_USER_UPDATE:
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if (object != null & object instanceof User)
						{
							user = (User) object;
						}
						profileViewMode();
					}
				}, AppConstant.TIMER_DELAY_HUNDRED);
				break;
			default:
				break;
		}

	}
}
