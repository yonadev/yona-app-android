/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ProfileFragment extends BaseProfileFragment {
    private final int PROFILE = 0, BADGES = 1;
    private ImageView profileImageView;
    private YonaFontTextView name, nickName;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        profileImageView = (ImageView) view.findViewById(R.id.profileImage);

        name = (YonaFontTextView) view.findViewById(R.id.name);
        nickName = (YonaFontTextView) view.findViewById(R.id.nick_name);

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        updateProfile();
    }

    private void updateProfile() {
        name.setText(getString(R.string.full_name, YonaApplication.getUser().getFirstName(), YonaApplication.getUser().getLastName()));
        nickName.setText(YonaApplication.getUser().getNickname());
        //TODO if server provide profile picture, pass bitmap of that else pass null
        profileImageView.setBackground(getImage(null, false));
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YonaActivity.getActivity().getLeftIcon().setVisibility(View.GONE);
                YonaActivity.getActivity().updateTitle(getString(R.string.blank));
                YonaActivity.getActivity().getRightIcon().setVisibility(View.GONE);
                viewPager.setCurrentItem(0);
                showOptionsInSelectedTab(viewPager.getCurrentItem());
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }

    private void showOptionsInSelectedTab(int position) {
        switch (position) {
            case PROFILE:
                showProfileOptions();
                break;
            case BADGES:
            default:
                showBadgeOptions();
                break;
        }
    }

    private void showBadgeOptions() {
        YonaActivity.getActivity().getRightIcon().setVisibility(View.GONE);
    }

    private void showProfileOptions() {
        YonaActivity.getActivity().getRightIcon().setVisibility(View.VISIBLE);
        YonaActivity.getActivity().getRightIcon().setTag(getString(R.string.profile));
        YonaActivity.getActivity().getRightIcon().setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_edit));
        YonaActivity.getActivity().getRightIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendIntent = new Intent(IntentEnum.ACTION_EDIT_PROFILE.getActionString());
                YonaActivity.getActivity().replaceFragment(friendIntent);
            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DetailsProfileFragment(), getString(R.string.profiledetails));
        adapter.addFragment(new BadgesProfileFragment(), getString(R.string.badges));
        viewPager.setAdapter(adapter);
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
}
