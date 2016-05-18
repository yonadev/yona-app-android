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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.User;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ProfileFragment extends BaseProfileFragment implements EventChangeListener {
    private final int PROFILE = 0, BADGES = 1;
    private ImageView profileImageView;
    private YonaFontTextView name, nickName;
    private ViewPager viewPager;
    private LinearLayout profileTopLayout;
    private TabLayout tabLayout;
    private int backgroundColor, profileBgColor;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        profileImageView = (ImageView) view.findViewById(R.id.profileImage);
        profileTopLayout = (LinearLayout) view.findViewById(R.id.profile_top_layout);
        name = (YonaFontTextView) view.findViewById(R.id.name);
        nickName = (YonaFontTextView) view.findViewById(R.id.nick_name);
        if (getArguments() != null) {
            if (getArguments().get(AppConstant.COLOR_CODE) != null) {
                backgroundColor = getArguments().getInt(AppConstant.COLOR_CODE);
            } else {
                backgroundColor = R.color.grape; // default color will be grape;
            }
            if (getArguments().get(AppConstant.SECOND_COLOR_CODE) != null) {
                profileBgColor = getArguments().getInt(AppConstant.SECOND_COLOR_CODE);
            } else {
                profileBgColor = R.color.mid_blue; // default bg color for profile picture.
            }
            if (getArguments().get(AppConstant.USER) != null) {
                user = (User) getArguments().get(AppConstant.USER);
            }
        } else {
            backgroundColor = R.color.grape; // default color will be grape;
            profileBgColor = R.color.mid_blue; // default bg color for profile picture.
        }
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        updateProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void updateProfile() {
        if (user != null) {
            name.setText(getString(R.string.full_name, !TextUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                    !TextUtils.isEmpty(user.getLastName()) ? user.getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
            nickName.setText(!TextUtils.isEmpty(user.getNickname()) ? user.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
        }
        //TODO if server provide profile picture, pass bitmap of that else pass null
        profileImageView.setImageDrawable(getImage(null, false, profileBgColor));
        profileTopLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), backgroundColor));
        tabLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), backgroundColor));
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
        }, AppConstant.TIMER_DELAY_THREE_HUNDRED);

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
        DetailsProfileFragment detailsProfileFragment = new DetailsProfileFragment();
        detailsProfileFragment.setArguments(getArguments());
        adapter.addFragment(detailsProfileFragment, getString(R.string.profiledetails));
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
                        updateProfile();
                    }
                }, AppConstant.TIMER_DELAY_HUNDRED);
                break;
            default:
                break;
        }

    }
}
