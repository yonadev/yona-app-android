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
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class DashboardFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, null);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PerDayFragment(), getString(R.string.perday));
        adapter.addFragment(new PerWeekFragment(), getString(R.string.perweek));
        viewPager.setAdapter(adapter);
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (YonaApplication.getUser() != null && !TextUtils.isEmpty(YonaApplication.getUser().getFirstName())) {
                    YonaActivity.getActivity().getLeftIcon().setVisibility(View.VISIBLE);
                    YonaActivity.getActivity().getLeftIcon().setImageDrawable(TextDrawable.builder()
                            .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                            .buildRound(YonaApplication.getUser().getFirstName().substring(0, 1).toUpperCase(),
                                    ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
                }
                YonaActivity.getActivity().updateTitle(R.string.dashboard);
                YonaActivity.getActivity().getRightIcon().setTag(getString(R.string.dashboard));
                YonaActivity.getActivity().getRightIcon().setVisibility(View.VISIBLE);
                YonaActivity.getActivity().getRightIcon().setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_reminder));

                YonaActivity.getActivity().getRightIcon().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent friendIntent = new Intent(IntentEnum.ACTION_MESSAGE.getActionString());
                        YonaActivity.getActivity().replaceFragment(friendIntent);
                    }
                });

                YonaActivity.getActivity().getLeftIcon().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
                        intent.putExtra(AppConstant.COLOR_CODE, R.color.grape);
                        intent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.mid_blue);
                        intent.putExtra(AppConstant.USER, YonaApplication.getUser());
                        YonaActivity.getActivity().replaceFragment(intent);
                    }
                });
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }
}
