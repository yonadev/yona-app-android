/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.YonaFontEditTextView;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DetailsProfileFragment extends BaseProfileFragment {

    private YonaFontEditTextView name, nickName, mobileNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_details_fragment, null);

        name = (YonaFontEditTextView) view.findViewById(R.id.name);
        nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewMode();
    }

    private void profileViewMode() {
        name.setClickable(false);
        name.setKeyListener(null);
        name.setText(getString(R.string.full_name, YonaApplication.getUser().getFirstName(), YonaApplication.getUser().getLastName()));

        nickName.setClickable(false);
        nickName.setKeyListener(null);
        nickName.setText(YonaApplication.getUser().getNickname());

        int NUMBER_LENGTH = 9;

        mobileNumber.setClickable(false);
        mobileNumber.setKeyListener(null);
        String number = YonaApplication.getUser().getMobileNumber();
        if(!TextUtils.isEmpty(number)) {
            number = number.substring(number.length() - NUMBER_LENGTH);
            number = number.substring(0, 3) + getString(R.string.space) + number.substring(3, 6) + getString(R.string.space) + number.substring(6, 9);
            mobileNumber.setText(getString(R.string.country_code_with_zero) + number);
        }
    }
}
