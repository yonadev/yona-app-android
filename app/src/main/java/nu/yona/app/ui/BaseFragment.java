/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;


import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.challenges.ChallengesFragment;
import nu.yona.app.ui.challenges.ChallengesGoalDetailFragment;
import nu.yona.app.ui.dashboard.DashboardFragment;
import nu.yona.app.ui.frinends.AddFriendFragment;
import nu.yona.app.ui.frinends.FriendsFragment;
import nu.yona.app.ui.frinends.FriendsRequestFragment;
import nu.yona.app.ui.message.NotificationFragment;
import nu.yona.app.ui.settings.PrivacyFragment;
import nu.yona.app.ui.settings.SettingsFragment;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class BaseFragment extends Fragment {
    /**
     * The M tool bar.
     */
    protected Toolbar mToolBar;
    /**
     * The Toolbar title.
     */
    protected YonaFontTextView toolbarTitle,
            txtNotificationCounter;
    /**
     * The Left icon.
     */
    protected ImageView leftIcon, /**
     * The Right icon.
     */
    rightIcon,

    rightIconProfile;

    /**
     * Sets toolbar.
     *
     * @param view the view
     */
    protected void setupToolbar(View view) {
        mToolBar = (Toolbar) view.findViewById(R.id.main_toolbar);
        toolbarTitle = (YonaFontTextView) mToolBar.findViewById(R.id.toolbar_title);
        leftIcon = (ImageView) mToolBar.findViewById(R.id.leftIcon);
        rightIcon = (ImageView) mToolBar.findViewById(R.id.rightIcon);
        rightIconProfile = (ImageView) mToolBar.findViewById(R.id.rightIconProfile);
        txtNotificationCounter = (YonaFontTextView) mToolBar.findViewById(R.id.txtNotificationCounter);
        if (!(this instanceof DashboardFragment)) {
            mToolBar.removeView(leftIcon);
        }
        updateToolBarBackground();
        YonaActivity.getActivity().setSupportActionBar(mToolBar);
    }

    private void updateToolBarBackground() {
        if (this instanceof ChallengesFragment || this instanceof ChallengesGoalDetailFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_green);
        } else if (this instanceof NotificationFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape);
        } else if (this instanceof SettingsFragment || this instanceof PrivacyFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
        } else if (this instanceof FriendsFragment || this instanceof FriendsRequestFragment || this instanceof AddFriendFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_blue);
        }
    }
}
