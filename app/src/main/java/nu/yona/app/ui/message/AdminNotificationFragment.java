/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import nu.yona.app.R;
import nu.yona.app.api.model.Page;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 07/03/17.
 */

public class AdminNotificationFragment extends BaseFragment {
    private YonaHeaderTheme yonaHeaderTheme;
    private TextView adminTitle, adminTime, adminMessage, profileIconTxt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null) {
            yonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_notification_fragment, null);
        setupToolbar(view);
        toolbarTitle.setText(getString(R.string.message));
        if (yonaHeaderTheme != null) {
            mToolBar.setBackgroundResource(yonaHeaderTheme.getToolbar());
        }
        profileIconTxt = (TextView) view.findViewById(R.id.profileTextIcon);
        adminTitle = (TextView) view.findViewById(R.id.admin_title);
        adminTime = (TextView) view.findViewById(R.id.admin_time);
        adminMessage = (TextView) view.findViewById(R.id.yona_message);

        adminTitle.setText(getString(R.string.yona_administrator));

        YonaMessage yonaMessage = (YonaMessage) getArguments().get(AppConstant.ADMIN_MESSAGE);
        adminTime.setText(getTime(yonaMessage.getCreationTime()));

        profileIconTxt.setVisibility(View.VISIBLE);
        profileIconTxt.setText(adminTitle.getText().toString().substring(0, 1).toUpperCase());
        profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_admin_round));

        adminMessage.setText(yonaMessage.getMessage());
        return view;
    }

    private String getTime(String date) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = dtf.parseDateTime(date);
        return dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour();
    }
}
