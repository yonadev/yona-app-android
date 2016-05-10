/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class FriendsFragment extends BaseFragment {
    private final int TIMELINE = 0, OVERVIEW = 1;
    private ViewPager viewPager;
    private YonaActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frineds_layout, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        activity = (YonaActivity) getActivity();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TimelineFragment(), getString(R.string.timeline));
        adapter.addFragment(new OverviewFragment(), getString(R.string.overiview));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showOptionsInSelectedTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.getLeftIcon().setVisibility(View.GONE);
                activity.updateTitle(R.string.friends);
                activity.getRightIcon().setVisibility(View.GONE);
                showOptionsInSelectedTab(viewPager.getCurrentItem());
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }

    private void showOptionsInSelectedTab(int position) {
        switch (position) {
            case OVERVIEW:
                showOverviewFragmentOptions();
                break;
            case TIMELINE:
            default:
                showTimeLineFragmentOptions();
                break;
        }
    }

    private void showTimeLineFragmentOptions() {
        activity.getRightIcon().setVisibility(View.GONE);
    }

    private void showOverviewFragmentOptions() {
        activity.getRightIcon().setVisibility(View.VISIBLE);
        activity.getRightIcon().setTag(getString(R.string.overiview));
        activity.getRightIcon().setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icn_add));
    }
}
