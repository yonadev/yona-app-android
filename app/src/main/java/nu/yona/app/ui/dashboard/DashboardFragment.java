/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DashboardFragment extends BaseFragment {

    private TabLayout tabLayout;
    private YonaHeaderTheme mYonaHeaderTheme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment, null);

        setupToolbar(view);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
    }

    private void setupViewPager(ViewPager viewPager) {
        setTabs();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PerDayFragment(), getString(R.string.perday));
        adapter.addFragment(new PerWeekFragment(), getString(R.string.perweek));
        viewPager.setAdapter(adapter);
    }

    private void setTabs() {
        ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
        mParams.height = getResources().getDimensionPixelSize(R.dimen.sixty_four);
        tabLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.fifteen), 0, 0);
        if (mYonaHeaderTheme != null) {
            tabLayout.setBackgroundResource(mYonaHeaderTheme.getHeadercolor());
            if (mYonaHeaderTheme.isBuddyFlow()) {
                tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
            } else {
                tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.dashboard_deselected_tab), ContextCompat.getColor(getActivity(), R.color.dashboard_selected_tab));
            }
        }
        tabLayout.setLayoutParams(mParams);
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && !TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName())) {
                    if (mYonaHeaderTheme != null) {
                        if (mYonaHeaderTheme.isBuddyFlow()) {
                            leftIcon.setVisibility(View.GONE);
                            rightIcon.setVisibility(View.VISIBLE);
                            rightIcon.setImageDrawable(TextDrawable.builder()
                                    .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                                    .buildRound(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName().substring(0, 1).toUpperCase(),
                                            ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
                            profileClickEvent(rightIcon);
                        } else {
                            leftIcon.setVisibility(View.VISIBLE);
                            leftIcon.setImageDrawable(TextDrawable.builder()
                                    .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                                    .buildRound(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName().substring(0, 1).toUpperCase(),
                                            ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
                            rightIcon.setTag(mYonaHeaderTheme.getHeader_title());
                            rightIcon.setVisibility(View.VISIBLE);
                            rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_reminder));

                            rightIconClickEvent(rightIcon);
                            profileClickEvent(leftIcon);
                        }
                    }
                }
                toolbarTitle.setText(mYonaHeaderTheme.getHeader_title());
                tabLayout.setVisibility(View.VISIBLE);
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }

    /**
     * Pass the view of profile icon for Me and buddies Profile
     *
     * @param profileView
     */
    private void profileClickEvent(View profileView) {
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
                intent.putExtra(AppConstant.COLOR_CODE, R.color.grape);
                intent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.mid_blue);
                intent.putExtra(AppConstant.USER, YonaApplication.getEventChangeManager().getDataState().getUser());
                YonaActivity.getActivity().replaceFragment(intent);
            }
        });
    }

    /**
     * To Show the Message Notification list and redirect to that view by click on notification icon
     *
     * @param rightIconView
     */
    private void rightIconClickEvent(View rightIconView) {
        rightIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendIntent = new Intent(IntentEnum.ACTION_MESSAGE.getActionString());
                YonaActivity.getActivity().replaceFragment(friendIntent);
            }
        });
    }
}
