/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.Day;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.dashboard.CustomPageAdapter;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class SingleDayActivityDetailFragment extends BaseFragment {

    private CustomPageAdapter customPageAdapter;
    private ViewPager viewPager;
    private DayActivity activity;
    private Day day;
    private WeekActivity weekActivity;
    private View view;
    private ImageView previousItem, nextItem;
    private YonaFontTextView dateTitle;
    private YonaHeaderTheme mYonaHeaderTheme;
    private List<DayActivity> dayActivityList;

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
        view = inflater.inflate(R.layout.detail_pager_fragment, null);

        setupToolbar(view);
        if (mYonaHeaderTheme != null) {
            mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
        }

        dayActivityList = new ArrayList<>();
        previousItem = (ImageView) view.findViewById(R.id.previous);
        nextItem = (ImageView) view.findViewById(R.id.next);
        dateTitle = (YonaFontTextView) view.findViewById(R.id.date);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        customPageAdapter = new CustomPageAdapter(getActivity());
        viewPager.setAdapter(customPageAdapter);
        if (getArguments() != null) {
            if (getArguments().get(AppConstant.OBJECT) != null) {
                if (getArguments().get(AppConstant.OBJECT) instanceof DayActivity) {
                    activity = (DayActivity) getArguments().get(AppConstant.OBJECT);
                }
            }
            if (getArguments().get(AppConstant.WEEK_OBJECT) != null) {
                weekActivity = (WeekActivity) getArguments().get(AppConstant.WEEK_OBJECT);
            }
        }
        previousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo call previous url
            }
        });
        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo call next url
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateFlow(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity != null) {
            setDayActivityDetails();
        } else {
            setWeekActivityDetails();
        }
    }

    private void setDayActivityDetails() {
        loadData(activity.getLinks().getYonaDayDetails().getHref());
        setDayDetailTitleAndIcon();
    }

    private void setDayDetailTitleAndIcon() {
        if (activity != null && activity.getYonaGoal() != null && !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName())) {
            toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
        }
        leftIcon.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);
        if (mYonaHeaderTheme.isBuddyFlow()) {
            rightIconProfile.setVisibility(View.VISIBLE);
            rightIconProfile.setImageDrawable(TextDrawable.builder()
                    .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                    .buildRound(activity.getYonaGoal().getNickName().substring(0, 1).toUpperCase(),
                            ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
            profileClickEvent(rightIconProfile);
        } else {
            rightIconProfile.setVisibility(View.GONE);
        }
    }

    private void profileClickEvent(View profileView) {
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
                intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
                if (mYonaHeaderTheme.isBuddyFlow()) {
                    intent.putExtra(AppConstant.COLOR_CODE, R.color.mid_blue_two);
                    intent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
                }
                if (activity.getLinks().getYonaBuddy() != null) {
                    intent.putExtra(AppConstant.URL, activity.getLinks().getYonaBuddy().getHref());
                } else {
                    intent.putExtra(AppConstant.USER, YonaApplication.getEventChangeManager().getDataState().getUser());
                }
                YonaActivity.getActivity().replaceFragment(intent);
            }
        });
    }

    private void setWeekActivityDetails() {
        if (day != null) {
//            DayActivities dayActivities = weekActivity.getDayActivities();
//            if (dayActivities.getSUNDAY() != null && dayActivities.getSATURDAY().getLinks() != null && dayActivities.getSUNDAY().getLinks().getYonaDayDetails() != null) {
//                loadData(dayActivities.getSUNDAY().getLinks().getYonaDayDetails().getHref());
//            }
        }
    }

    private void loadData(String url) {
        if (url == null) {
            return;
        }
        APIManager.getInstance().getActivityManager().getDayDetailActivity(url, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                if (result instanceof DayActivity) {
                    dayActivityList.add((DayActivity) result);
                    customPageAdapter.notifyDataSetChanged(dayActivityList);
                    viewPager.setCurrentItem(dayActivityList.indexOf(activity));
                    updateFlow(dayActivityList.indexOf(activity));
                }

            }

            @Override
            public void onError(Object errorMessage) {

            }
        });
    }

    private void setWeekDetailTitleAndIcon() {
//        if (weekActivity != null && activity.getYonaGoal() != null && !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName())) {
//            toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
//        }
//        leftIcon.setVisibility(View.GONE);
//        rightIcon.setVisibility(View.GONE);
    }

    private void updateFlow(int position) {
        dateTitle.setText(dayActivityList.get(position).getStickyTitle());
        if (position == 0) {
            previousItem.setVisibility(View.INVISIBLE);
        } else {
            previousItem.setVisibility(View.VISIBLE);
        }
        if (position == dayActivityList.size() - 1) {
            nextItem.setVisibility(View.INVISIBLE);
        } else {
            nextItem.setVisibility(View.VISIBLE);
        }
    }
}
