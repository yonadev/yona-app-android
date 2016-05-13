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
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DetailsProfileFragment extends BaseProfileFragment implements EventChangeListener {

    private YonaFontEditTextView firstName, lastName, nickName, mobileNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_details_fragment, null);

        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
        nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);
        YonaApplication.getEventChangeManager().registerListener(this);
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
        firstName.setClickable(false);
        firstName.setKeyListener(null);
        firstName.setText(TextUtils.isEmpty(YonaApplication.getUser().getFirstName()) ? getString(R.string.blank) : YonaApplication.getUser().getFirstName());

        lastName.setClickable(false);
        lastName.setKeyListener(null);
        lastName.setText(TextUtils.isEmpty(YonaApplication.getUser().getLastName()) ? getString(R.string.blank) : YonaApplication.getUser().getLastName());

        nickName.setClickable(false);
        nickName.setKeyListener(null);
        nickName.setText(TextUtils.isEmpty(YonaApplication.getUser().getNickname()) ? getString(R.string.blank) : YonaApplication.getUser().getNickname());

        int NUMBER_LENGTH = 9;

        mobileNumber.setClickable(false);
        mobileNumber.setKeyListener(null);
        String number = YonaApplication.getUser().getMobileNumber();
        if (!TextUtils.isEmpty(number) && number.length() > NUMBER_LENGTH) {
            number = number.substring(number.length() - NUMBER_LENGTH);
            number = number.substring(0, 3) + getString(R.string.space) + number.substring(3, 6) + getString(R.string.space) + number.substring(6, 9);
            mobileNumber.setText(getString(R.string.country_code_with_zero) + number);
        }
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_USER_UPDATE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        profileViewMode();
                    }
                }, AppConstant.TIMER_DELAY_HUNDRED);
                break;
            default:
                break;
        }

    }
}
