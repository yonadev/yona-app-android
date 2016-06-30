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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
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
    private CollapsingToolbarLayout profileTopLayout;
    private TabLayout tabLayout;
    private int profileBgColor, tabDeSelectedColor;
    private User user;
    private YonaMessage yonaMessage;
    private YonaBuddy yonaBuddy;
    private YonaHeaderTheme yonaHeaderTheme;
    private DetailsProfileFragment detailsProfileFragment;
    private String mUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null) {
                yonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
            }
            if (getArguments().get(AppConstant.SECOND_COLOR_CODE) != null) {
                profileBgColor = getArguments().getInt(AppConstant.SECOND_COLOR_CODE);
            } else {
                profileBgColor = R.color.mid_blue; // default bg color for profile picture.
            }
            if (getArguments().get(AppConstant.USER) != null) {
                user = (User) getArguments().get(AppConstant.USER);
            } else if (getArguments().get(AppConstant.YONAMESSAGE_OBJ) != null) {
                yonaMessage = (YonaMessage) getArguments().get(AppConstant.YONAMESSAGE_OBJ);
            } else if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null) {
                yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
            } else if (getArguments().getString(AppConstant.URL) != null) {
                mUrl = getArguments().getString(AppConstant.URL);
            }
        } else {
            profileBgColor = R.color.mid_blue; // default bg color for profile picture.
            tabDeSelectedColor = R.color.dashboard_deselected_tab;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);

        setupToolbar(view);
        if (yonaHeaderTheme != null) {
            mToolBar.setBackgroundResource(yonaHeaderTheme.getToolbar());
        }

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        profileImageView = (ImageView) view.findViewById(R.id.profileImage);
        profileTopLayout = (CollapsingToolbarLayout) view.findViewById(R.id.profile_top_layout);
        name = (YonaFontTextView) view.findViewById(R.id.name);
        nickName = (YonaFontTextView) view.findViewById(R.id.nick_name);

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        if (yonaMessage != null && yonaMessage.getEmbedded() == null && yonaMessage.getLinks() != null
                && yonaMessage.getLinks().getYonaUser() != null && !TextUtils.isEmpty(yonaMessage.getLinks().getYonaUser().getHref())) {
            loadFriendProfile(yonaMessage.getLinks().getYonaUser().getHref());
        } else if (yonaBuddy != null) {
            updateProfileAndIcon();
        } else if (!TextUtils.isEmpty(mUrl)) {
            loadFriendProfile(mUrl);
        }

        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        updateProfile();
        showOptionsInSelectedTab(viewPager.getCurrentItem());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void loadFriendProfile(String url) {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getAuthenticateManager().getFriendProfile(url, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                if (result instanceof User) {
                    user = (User) result;
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_UPDATE, user);
                    updateProfileAndIcon();
                }
            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                ErrorMessage message = (ErrorMessage) errorMessage;
                Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateProfileAndIcon() {
        setTitleAndIcon();
        updateProfile();
    }

    private void updateProfile() {
        if (user != null && mUrl != null) {
            if (user.getEmbedded() != null && user.getEmbedded().getYonaUser() != null) {
                name.setText(getString(R.string.full_name, !TextUtils.isEmpty(user.getEmbedded().getYonaUser().getFirstName()) ? user.getEmbedded().getYonaUser().getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                        !TextUtils.isEmpty(user.getEmbedded().getYonaUser().getLastName()) ? user.getEmbedded().getYonaUser().getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
                profileImageView.setImageDrawable(getImage(null, false, profileBgColor, user.getEmbedded().getYonaUser().getFirstName(), user.getEmbedded().getYonaUser().getLastName()));
            }
            nickName.setText(!TextUtils.isEmpty(user.getNickname()) ? user.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
        } else if (user != null) {
            name.setText(getString(R.string.full_name, !TextUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                    !TextUtils.isEmpty(user.getLastName()) ? user.getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
            nickName.setText(!TextUtils.isEmpty(user.getNickname()) ? user.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
            profileImageView.setImageDrawable(getImage(null, false, profileBgColor, user.getFirstName(), user.getLastName()));
        } else if (yonaMessage != null) {
            if (yonaMessage.getEmbedded() != null && yonaMessage.getEmbedded().getYonaUser() != null) {
                name.setText(getString(R.string.full_name, !TextUtils.isEmpty(yonaMessage.getEmbedded().getYonaUser().getFirstName()) ? yonaMessage.getEmbedded().getYonaUser().getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                        !TextUtils.isEmpty(yonaMessage.getEmbedded().getYonaUser().getLastName()) ? yonaMessage.getEmbedded().getYonaUser().getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
                profileImageView.setImageDrawable(getImage(null, false, profileBgColor, yonaMessage.getEmbedded().getYonaUser().getFirstName(), yonaMessage.getEmbedded().getYonaUser().getLastName()));
            }
            nickName.setText(!TextUtils.isEmpty(yonaMessage.getNickname()) ? yonaMessage.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
        } else if (yonaBuddy != null) {
            if (yonaBuddy.getEmbedded() != null && yonaBuddy.getEmbedded().getYonaUser() != null) {
                name.setText(getString(R.string.full_name, !TextUtils.isEmpty(yonaBuddy.getEmbedded().getYonaUser().getFirstName()) ? yonaBuddy.getEmbedded().getYonaUser().getFirstName() : YonaActivity.getActivity().getString(R.string.blank),
                        !TextUtils.isEmpty(yonaBuddy.getEmbedded().getYonaUser().getLastName()) ? yonaBuddy.getEmbedded().getYonaUser().getLastName() : YonaActivity.getActivity().getString(R.string.blank)));
                profileImageView.setImageDrawable(getImage(null, false, profileBgColor, yonaBuddy.getEmbedded().getYonaUser().getFirstName(), yonaBuddy.getEmbedded().getYonaUser().getLastName()));
            }
            nickName.setText(!TextUtils.isEmpty(yonaBuddy.getNickname()) ? yonaBuddy.getNickname() : YonaActivity.getActivity().getString(R.string.blank));
        }
        //TODO if server provide profile picture, pass bitmap of that else pass null
        profileTopLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), yonaHeaderTheme.getHeadercolor()));
        tabLayout.setBackgroundColor(ContextCompat.getColor(YonaActivity.getActivity(), yonaHeaderTheme.getHeadercolor()));
        if (yonaHeaderTheme != null && (yonaHeaderTheme.isBuddyFlow() || yonaMessage != null)) {
            tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.friends_deselected_tab), ContextCompat.getColor(getActivity(), R.color.friends_selected_tab));
        } else {
            tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.dashboard_deselected_tab), ContextCompat.getColor(getActivity(), R.color.dashboard_selected_tab));
        }
    }

    private void setTitleAndIcon() {
        leftIcon.setVisibility(View.GONE);
        toolbarTitle.setText(getString(R.string.blank));
        rightIcon.setVisibility(View.GONE);
        viewPager.setCurrentItem(0);
        showOptionsInSelectedTab(viewPager.getCurrentItem());
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
        rightIcon.setVisibility(View.GONE);
    }

    private void showProfileOptions() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && user != null && yonaMessage == null) {
                    rightIcon.setVisibility(View.VISIBLE);
                    rightIcon.setTag(getString(R.string.profile));
                    rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_edit));
                    rightIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent friendIntent = new Intent(IntentEnum.ACTION_EDIT_PROFILE.getActionString());
                            YonaActivity.getActivity().replaceFragment(friendIntent);
                        }
                    });
                }
            }
        }, AppConstant.TIMER_DELAY_THREE_HUNDRED);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        detailsProfileFragment = new DetailsProfileFragment();
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
