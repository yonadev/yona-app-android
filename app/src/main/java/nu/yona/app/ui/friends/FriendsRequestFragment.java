/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.Properties;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.profile.BaseProfileFragment;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 17/05/16.
 */
public class FriendsRequestFragment extends BaseProfileFragment implements View.OnClickListener
{

	/**
	 * Yona object
	 */
	private YonaMessage mYonaMessage;
	/**
	 * Content title is Message type
	 */
	private YonaFontTextView mTxtContentTitle,
	/**
	 * content desc is Message info
	 */
	mTxtContentDesc,
	/**
	 * update Profile name
	 */
	profileName,
	/**
	 * update nick name
	 */
	profileNickName;
	/**
	 * Accept button
	 */
	private YonaFontButton btnAccept;
	/**
	 * Reject button
	 */
	private YonaFontButton btnReject;

	private TextView profileIconTxt;
	private ImageView profileImage;
	/**
	 * Profile top layout for updatig background color
	 */
	private CollapsingToolbarLayout profileTopLayout;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			mYonaMessage = (YonaMessage) getArguments().getSerializable(AppConstant.YONAMESSAGE_OBJ);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.friend_request_layout, null);

		setupToolbar(view);

		profileName = (YonaFontTextView) view.findViewById(R.id.name);
		profileNickName = (YonaFontTextView) view.findViewById(R.id.nick_name);
		profileImage = (ImageView) view.findViewById(R.id.profileImage);
		profileTopLayout = (CollapsingToolbarLayout) view.findViewById(R.id.profile_top_layout);
		profileIconTxt = (TextView) view.findViewById(R.id.profileIcon);
		btnAccept = (YonaFontButton) view.findViewById(R.id.btnAccepter);
		btnAccept.setOnClickListener(this);
		btnReject = (YonaFontButton) view.findViewById(R.id.btnReject);
		btnReject.setOnClickListener(this);
		mTxtContentTitle = (YonaFontTextView) view.findViewById(R.id.content_title);
		mTxtContentDesc = (YonaFontTextView) view.findViewById(R.id.content_desc);
		populateView();
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_FRIEND_REQUEST));
		return view;
	}

	/**
	 * populateView
	 */
	private void populateView()
	{
		if (mYonaMessage != null)
		{
			if (mYonaMessage.getNotificationMessageEnum() != null && !TextUtils.isEmpty(mYonaMessage.getNotificationMessageEnum().getUserMessage()))
			{
				mTxtContentTitle.setText(mYonaMessage.getNotificationMessageEnum().getUserMessage());
			}
			if (mYonaMessage.getEmbedded() != null && mYonaMessage.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getMobileNumber()))
			{
				mTxtContentDesc.setText(getString(R.string.friend_request_content, mYonaMessage.getEmbedded().getYonaUser().getMobileNumber()));
			}
		}
		YonaActivity.getActivity().updateTabIcon(true);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setTitleAndIcon();
		updateProfile();
	}

	/**
	 * update toolbar
	 */
	private void setTitleAndIcon()
	{
		profileCircleImageView.setVisibility(View.GONE);
		toolbarTitle.setText(getString(R.string.empty_description));
		rightIcon.setVisibility(View.GONE);
	}

	/**
	 * Update Profile detail
	 */
	private void updateProfile()
	{
		if (mYonaMessage != null)
		{
			if (mYonaMessage.getEmbedded() != null && mYonaMessage.getEmbedded().getYonaUser() != null)
			{
				profileName.setText(getString(R.string.full_name, !TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getFirstName()) ? mYonaMessage.getEmbedded().getYonaUser().getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
						!TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getLastName()) ? mYonaMessage.getEmbedded().getYonaUser().getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
			}
			profileNickName.setText(!TextUtils.isEmpty(mYonaMessage.getNickname()) ? mYonaMessage.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
			setTextIcon();
//            profileImage.setImageDrawable(getImage(null, false, R.color.grape_two, mYonaMessage.getEmbedded().getYonaUser().getFirstName(), mYonaMessage.getEmbedded().getYonaUser().getLastName()));
		}
		profileTopLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue_two));
	}

	private void setTextIcon()
	{
		profileIconTxt.setVisibility(View.VISIBLE);
		profileIconTxt.setText(getNameInital());
		profileIconTxt.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_big_self_round));
	}

	private String getNameInital()
	{
		return mYonaMessage.getEmbedded().getYonaUser().getFirstName().substring(0, 1) + mYonaMessage.getEmbedded().getYonaUser().getLastName().substring(0, 1);
	}

	@Override
	public void onClick(View v)
	{
		if (mYonaMessage == null && mYonaMessage.getLinks() == null)
		{
			return;
		}
		switch (v.getId())
		{
			case R.id.btnAccepter:
				if (!TextUtils.isEmpty(mYonaMessage.getLinks().getYonaAccept().getHref()))
				{
					responseFriendRequest(mYonaMessage.getLinks().getYonaAccept().getHref());
					YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.FRIEND_REQUEST_SCREEN, getString(R.string.accept));
				}
				break;
			case R.id.btnReject:
				if (!TextUtils.isEmpty(mYonaMessage.getLinks().getYonaReject().getHref()))
				{
					responseFriendRequest(mYonaMessage.getLinks().getYonaReject().getHref());
					YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.FRIEND_REQUEST_SCREEN, getString(R.string.reject));
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Accept User's Friend Request
	 */
	private void responseFriendRequest(final String url)
	{
		MessageBody messageBody = new MessageBody();
		messageBody.setProperties(new Properties());
		YonaActivity.getActivity().showLoadingView(true, null);
		APIManager.getInstance().getNotificationManager().postMessage(url, messageBody, 0, 0, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				goBackToScreen();
			}

			@Override
			public void onError(Object errorMessage)
			{
				YonaActivity.getActivity().showLoadingView(false, null);
				YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
			}
		});
	}

	/**
	 * Go back to screen
	 */
	private void goBackToScreen()
	{
		YonaActivity.getActivity().onBackPressed();
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.FRIEND_REQUEST_SCREEN;
	}
}
