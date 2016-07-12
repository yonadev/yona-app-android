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
    private WeekActivity weekActivity;
    private Day mDay;
    private View view;
    private ImageView previousItem, nextItem;
    private YonaFontTextView dateTitle;
    private YonaHeaderTheme mYonaHeaderTheme;
    private List<DayActivity> dayActivityList;
    private List<WeekActivity> weekDayActivityList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);

            if (getArguments().get(AppConstant.OBJECT) != null) {
                if (getArguments().get(AppConstant.OBJECT) instanceof DayActivity) {
                    activity = (DayActivity) getArguments().get(AppConstant.OBJECT);
                }
            }
            if (getArguments().get(AppConstant.DAY_OBJECT) instanceof Day) {
                mDay = (Day) getArguments().get(AppConstant.DAY_OBJECT);
            }
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
        weekDayActivityList = new ArrayList<>();
        previousItem = (ImageView) view.findViewById(R.id.previous);
        nextItem = (ImageView) view.findViewById(R.id.next);
        dateTitle = (YonaFontTextView) view.findViewById(R.id.date);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        customPageAdapter = new CustomPageAdapter(getActivity());
        viewPager.setAdapter(customPageAdapter);
        previousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDayActivity();
            }
        });
        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDayActivity();
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

        if (activity != null && activity.getLinks() != null && activity.getLinks().getYonaDayDetails() != null) {
            setDayActivityDetails();
        }
        if (mDay != null && mDay.getLinks() != null && mDay.getLinks().getYonaDayDetails() != null) {
            setWeekDayActivityDetail();
        }
        return view;
    }

    private void previousDayActivity() {
        if (activity != null) {
            loadDayActivity(activity.getLinks().getPrev().getHref());
            setDayDetailTitleAndIcon();
        }
        if (weekActivity != null) {
            loadWeekDayActivity(weekActivity.getLinks().getPrev().getHref());
            setWeekDetailTitleAndIcon();
        }
    }

    private void nextDayActivity() {
        if (activity != null) {
            loadDayActivity(activity.getLinks().getNext().getHref());
            setDayDetailTitleAndIcon();
        }
        if (weekActivity != null) {
            loadWeekDayActivity(weekActivity.getLinks().getNext().getHref());
            setWeekDetailTitleAndIcon();
        }
    }

    private void setDayActivityDetails() {
        loadDayActivity(activity.getLinks().getYonaDayDetails().getHref());
        setDayDetailTitleAndIcon();
    }

    private void setWeekDayActivityDetail() {
        loadWeekDayActivity(mDay.getLinks().getYonaDayDetails().getHref());
        setWeekDetailTitleAndIcon();
    }

    private void setDayDetailTitleAndIcon() {
        if (activity != null && activity.getYonaGoal() != null && !TextUtils.isEmpty(activity.getYonaGoal().getActivityCategoryName())) {
            toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
        }
        leftIcon.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);
        if (mYonaHeaderTheme != null && mYonaHeaderTheme.isBuddyFlow()) {
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
                if (mYonaHeaderTheme != null && mYonaHeaderTheme.isBuddyFlow()) {
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

    private void loadDayActivity(String url) {
        if (url == null) {
            return;
        }
        YonaActivity.getActivity().showLoadingView(true, null);

        if (getLocalDayActivity(url) != null) {
            activity = getLocalDayActivity(url);
            updateDayActivityData(activity);
            YonaActivity.getActivity().showLoadingView(false, null);
        } else {
            APIManager.getInstance().getActivityManager().getDayDetailActivity(url, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof DayActivity) {
                        dayActivityList.add((DayActivity) result);
                        activity = (DayActivity) result;
                        updateDayActivityData(activity);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    YonaActivity.getActivity().showLoadingView(false, null);
                }
            });
        }
    }

    private DayActivity getLocalDayActivity(String url) {
        for (DayActivity dayActivity : dayActivityList) {
            if (dayActivity != null && dayActivity.getLinks() != null && dayActivity.getLinks().getSelf() != null && !TextUtils.isEmpty(dayActivity.getLinks().getSelf().getHref()) && url.equalsIgnoreCase(dayActivity.getLinks().getSelf().getHref())) {
                return dayActivity;
            }
        }
        return null;
    }

    private WeekActivity getLocalWeekActivity(String url) {
        for (WeekActivity weekActivity : weekDayActivityList) {
            if (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getSelf() != null && !TextUtils.isEmpty(weekActivity.getLinks().getSelf().getHref()) && url.equalsIgnoreCase(weekActivity.getLinks().getSelf().getHref())) {
                return weekActivity;
            }
        }
        return null;
    }

    private void updateDayActivityData(DayActivity dayActivity) {
        customPageAdapter.notifyDataSetChanged(dayActivityList);
        viewPager.setCurrentItem(dayActivityList.indexOf(dayActivity));
        updateFlow(dayActivityList.indexOf(dayActivity));
        YonaActivity.getActivity().showLoadingView(false, null);
    }

    private void updateWeekDayActivityData(WeekActivity weekActivity) {
        customPageAdapter.notifyDataSetChanged(weekDayActivityList, false);
        viewPager.setCurrentItem(weekDayActivityList.indexOf(weekActivity));
        updateFlow(weekDayActivityList.indexOf(weekActivity));
        YonaActivity.getActivity().showLoadingView(false, null);
    }

    private void loadWeekDayActivity(String url) {
        if (url == null) {
            return;
        }
        YonaActivity.getActivity().showLoadingView(true, null);

        if (getLocalWeekActivity(url) != null) {
            weekActivity = getLocalWeekActivity(url);
            updateWeekDayActivityData(weekActivity);
        } else {
            APIManager.getInstance().getActivityManager().getWeeksDetailActivity(url, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof WeekActivity) {
                        weekDayActivityList.add((WeekActivity) result);
                        weekActivity = (WeekActivity) result;
                        updateWeekDayActivityData(weekActivity);
                    }
                    YonaActivity.getActivity().showLoadingView(false, null);

                }

                @Override
                public void onError(Object errorMessage) {
                    YonaActivity.getActivity().showLoadingView(false, null);
                }
            });
        }
    }

    private void setWeekDetailTitleAndIcon() {
        if (weekActivity != null && weekActivity.getYonaGoal() != null && !TextUtils.isEmpty(weekActivity.getYonaGoal().getActivityCategoryName())) {
            toolbarTitle.setText(weekActivity.getYonaGoal().getActivityCategoryName().toUpperCase());
        }
        leftIcon.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);

        if (mYonaHeaderTheme != null && mYonaHeaderTheme.isBuddyFlow()) {
            rightIconProfile.setVisibility(View.VISIBLE);
            rightIconProfile.setImageDrawable(TextDrawable.builder()
                    .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                    .buildRound(weekActivity.getYonaGoal().getNickName().substring(0, 1).toUpperCase(),
                            ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
            profileClickEvent(rightIconProfile);
        } else {
            rightIconProfile.setVisibility(View.GONE);
        }
    }

    private void updateFlow(int position) {
        if (weekDayActivityList != null && weekDayActivityList.size() > 0) {
            dateTitle.setText(weekDayActivityList.get(position).getStickyTitle());
        } else if (dayActivityList != null && dayActivityList.size() > 0) {
            dateTitle.setText(dayActivityList.get(position).getStickyTitle());
        }

        if ((activity != null && activity.getLinks() != null && activity.getLinks().getPrev() != null && !TextUtils.isEmpty(activity.getLinks().getPrev().getHref())) ||
                (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getPrev() != null && !TextUtils.isEmpty(weekActivity.getLinks().getPrev().getHref()))) {
            previousItem.setVisibility(View.VISIBLE);
        } else {
            previousItem.setVisibility(View.INVISIBLE);
        }
        if ((activity != null && activity.getLinks() != null && activity.getLinks().getNext() != null && !TextUtils.isEmpty(activity.getLinks().getNext().getHref())) ||
                (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getNext() != null && !TextUtils.isEmpty(weekActivity.getLinks().getNext().getHref()))) {
            nextItem.setVisibility(View.VISIBLE);
        } else {
            nextItem.setVisibility(View.INVISIBLE);
        }

    }
}
