/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
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
public class FriendsRequestFragment extends BaseProfileFragment implements View.OnClickListener {

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
    private YonaFontButton btnAccept,
    /**
     * Reject button
     */
    btnReject;
    private ImageView profileImage;
    /**
     * Profile top layout for updatig background color
     */
    private LinearLayout profileTopLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYonaMessage = (YonaMessage) getArguments().getSerializable(AppConstant.YONAMESSAGE_OBJ);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_request_layout, null);
        profileName = (YonaFontTextView) view.findViewById(R.id.name);
        profileNickName = (YonaFontTextView) view.findViewById(R.id.nick_name);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        profileTopLayout = (LinearLayout) view.findViewById(R.id.profile_top_layout);
        btnAccept = (YonaFontButton) view.findViewById(R.id.btnAccepter);
        btnAccept.setOnClickListener(this);
        btnReject = (YonaFontButton) view.findViewById(R.id.btnReject);
        btnReject.setOnClickListener(this);
        mTxtContentTitle = (YonaFontTextView) view.findViewById(R.id.content_title);
        mTxtContentDesc = (YonaFontTextView) view.findViewById(R.id.content_desc);
        populateView();
        return view;
    }

    /**
     * populateView
     */
    private void populateView() {
        if (mYonaMessage != null) {
            if (mYonaMessage.getNotificationMessageEnum() != null && !TextUtils.isEmpty(mYonaMessage.getNotificationMessageEnum().getUserMessage())) {
                mTxtContentTitle.setText(mYonaMessage.getNotificationMessageEnum().getUserMessage());
            }
            if (mYonaMessage.getEmbedded() != null && mYonaMessage.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getMobileNumber())) {
                mTxtContentDesc.setText(getString(R.string.friend_request_content, mYonaMessage.getEmbedded().getYonaUser().getMobileNumber()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        updateProfile();
    }

    /**
     * update toolbar
     */
    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                leftIcon.setVisibility(View.GONE);
                toolbarTitle.setText(getString(R.string.empty_description));
                rightIcon.setVisibility(View.GONE);
            }
        }, AppConstant.TIMER_DELAY);

    }

    /**
     * Update Profile detail
     */
    private void updateProfile() {
        if (mYonaMessage != null) {
            if (mYonaMessage.getEmbedded() != null && mYonaMessage.getEmbedded().getYonaUser() != null) {
                profileName.setText(getString(R.string.full_name, !TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getFirstName()) ? mYonaMessage.getEmbedded().getYonaUser().getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                        !TextUtils.isEmpty(mYonaMessage.getEmbedded().getYonaUser().getLastName()) ? mYonaMessage.getEmbedded().getYonaUser().getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
            }
            profileNickName.setText(!TextUtils.isEmpty(mYonaMessage.getNickname()) ? mYonaMessage.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
        }
        profileImage.setImageDrawable(getImage(null, false, R.color.grape_two, YonaApplication.getUser().getFirstName(), YonaApplication.getUser().getLastName()));
        profileTopLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue_two));
    }

    @Override
    public void onClick(View v) {
        if (mYonaMessage == null && mYonaMessage.getLinks() == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnAccepter:
                if (!TextUtils.isEmpty(mYonaMessage.getLinks().getYonaAccept().getHref())) {
                    responseFriendRequest(mYonaMessage.getLinks().getYonaAccept().getHref());
                }
                break;
            case R.id.btnReject:
                if (!TextUtils.isEmpty(mYonaMessage.getLinks().getYonaReject().getHref())) {
                    responseFriendRequest(mYonaMessage.getLinks().getYonaReject().getHref());
                }
                break;
            default:
                break;
        }
    }

    /**
     * Accept User's Friend Request
     */
    private void responseFriendRequest(final String url) {
        MessageBody messageBody = new MessageBody();
        messageBody.setProperties(new Properties());
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getNotificationManager().postMessage(url, messageBody, 0, 0, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                goBackToScreen();
            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
            }
        });
    }

    /**
     * Go back to screen
     */
    private void goBackToScreen() {
        YonaActivity.getActivity().onBackPressed();
    }
}
