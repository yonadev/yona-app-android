/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.User;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DetailsProfileFragment extends BaseProfileFragment implements EventChangeListener {

    private YonaFontEditTextView firstName, lastName, nickName, mobileNumber;
    private YonaFontButton removeFriendButton;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_details_fragment, null);

        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
        nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);

        removeFriendButton = (YonaFontButton) view.findViewById(R.id.removeFriendButton);

        removeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO remove friend api call
            }
        });
        YonaApplication.getEventChangeManager().registerListener(this);
        if (getArguments() != null && getArguments().get(AppConstant.USER) != null) {
            user = (User) getArguments().get(AppConstant.USER);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewMode();
    }

    private void profileViewMode() {
        if (user != null) {
            firstName.setClickable(false);
            firstName.setKeyListener(null);
            firstName.setText(TextUtils.isEmpty(user.getFirstName()) ? getString(R.string.blank) : user.getFirstName());

            lastName.setClickable(false);
            lastName.setKeyListener(null);
            lastName.setText(TextUtils.isEmpty(user.getLastName()) ? getString(R.string.blank) : user.getLastName());

            nickName.setClickable(false);
            nickName.setKeyListener(null);
            nickName.setText(TextUtils.isEmpty(user.getNickname()) ? getString(R.string.blank) : user.getNickname());

            if (isNull(user) && isNull(YonaApplication.getUser())
                    && user.getLinks().getEdit().getHref().equals(YonaApplication.getUser().getLinks().getEdit().getHref())) {
                removeFriendButton.setVisibility(View.VISIBLE);
            } else {
                removeFriendButton.setVisibility(View.GONE);
            }
        }
        int NUMBER_LENGTH = 9;

        mobileNumber.setClickable(false);
        mobileNumber.setKeyListener(null);
        String number = user.getMobileNumber();
        if (!TextUtils.isEmpty(number) && number.length() > NUMBER_LENGTH) {
            number = number.substring(number.length() - NUMBER_LENGTH);
            number = number.substring(0, 3) + getString(R.string.space) + number.substring(3, 6) + getString(R.string.space) + number.substring(6, 9);
            mobileNumber.setText(getString(R.string.country_code_with_zero) + number);
        }
    }

    private boolean isNull(User user) {
        if (user != null && user.getLinks() != null && user.getLinks().getEdit() != null
                && !TextUtils.isEmpty(user.getLinks().getEdit().getHref())) {
            return false;
        }
        return true;
    }

    @Override
    public void onStateChange(int eventType, final Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_USER_UPDATE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (object != null & object instanceof User) {
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
