/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nu.yona.app.R;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class FriendsFragment extends BaseFragment {
    private final int TIMELINE = 0, OVERVIEW = 1;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment, null);

        setupToolbar(view);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
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
        setTabs();
        leftIcon.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.friends);
        rightIcon.setVisibility(View.GONE);
        showOptionsInSelectedTab(viewPager.getCurrentItem());
    }

    private void setTabs() {
        ViewGroup.LayoutParams mParams = tabLayout.getLayoutParams();
        mParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        tabLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.ten), 0, 0);
        tabLayout.setLayoutParams(mParams);
        tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
        tabLayout.setBackgroundResource(R.color.mid_blue_two);
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
        rightIcon.setVisibility(View.GONE);
    }

    private void showOverviewFragmentOptions() {
        rightIcon.setVisibility(View.VISIBLE);
        rightIcon.setTag(getString(R.string.overiview));
        rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_add));
    }

    private void addFriend() {
        Intent friendIntent = new Intent(IntentEnum.ACTION_ADD_FRIEND.getActionString());
        YonaActivity.getActivity().replaceFragment(friendIntent);
    }
}
