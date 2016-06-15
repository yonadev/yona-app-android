/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 13/06/16.
 */

public class DayActivityDetailFragment extends BaseFragment {

    private CustomPageAdapter customPageAdapter;
    private ViewPager viewPager;
    private DayActivity activity;
    private View view;
    private ImageView previousItem, nextItem;
    private YonaFontTextView dateTitle;
    private List<DayActivity> dayActivityList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_pager_fragment, null);

        setupToolbar(view);

        previousItem = (ImageView) view.findViewById(R.id.previous);
        nextItem = (ImageView) view.findViewById(R.id.next);
        dateTitle = (YonaFontTextView) view.findViewById(R.id.date);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        customPageAdapter = new CustomPageAdapter(getActivity(), dateTitle);
        viewPager.setAdapter(customPageAdapter);
        if (getArguments() != null) {
            if (getArguments().get(AppConstant.OBJECT) != null) {
                activity = (DayActivity) getArguments().get(AppConstant.OBJECT);
                getMoreDetails();
            }
        }
        previousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });
        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != dayActivityList.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
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

    private void getMoreDetails() {
        if (activity != null
                && activity.getSpread() == null || (activity.getSpread() != null && activity.getSpread().size() == 0)
                && activity.getLinks() != null
                && activity.getLinks().getYonaDayDetails() != null
                && !TextUtils.isEmpty(activity.getLinks().getYonaDayDetails().getHref())) {
            YonaActivity.getActivity().showLoadingView(true, null);
            APIManager.getInstance().getActivityManager().getDayDetailActivity(activity.getLinks().getYonaDayDetails().getHref(), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof DayActivity) {
                        activity = (DayActivity) result;
                        updateSpreadInList();
                        loadSpreadGraph();
                        YonaActivity.getActivity().showLoadingView(false, null);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    YonaActivity.getActivity().showLoadingView(false, null);
                    YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
                }
            });
        } else {
            loadSpreadGraph();
        }
    }

    private void updateSpreadInList() {
        try {
            List<DayActivity> dayActivities = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList();
            dayActivities.set(dayActivities.indexOf(activity), activity);
        } catch (Exception e) {
            Log.e(DayActivityDetailFragment.class.getSimpleName(), e.getMessage());
        }

    }

    private void loadSpreadGraph() {
        //TODO load spread graph here
        for (int i = 0; i < activity.getTimeZoneSpread().size(); i++) {
            Log.e(DayActivity.class.getSimpleName(), "loadSpreadGraph: " + i + "-" + activity.getTimeZoneSpread().get(i).getIndex() + ", " + activity.getTimeZoneSpread().get(i).getUsedValue());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dayActivityList = new ArrayList<>();
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
        if (embeddedYonaActivity != null && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0) {
            for (DayActivity dayActivity : embeddedYonaActivity.getDayActivityList()) {
                try {
                    if (dayActivity.getYonaGoal().getLinks().getSelf().getHref().equals(activity.getLinks().getYonaGoal().getHref())) {
                        dayActivityList.add(dayActivity);
                    }
                } catch (Exception e) {
                    Log.e(DayActivityDetailFragment.class.getSimpleName(), e.getMessage());
                }
            }
            customPageAdapter.notifyDataSetChanged(dayActivityList);
            viewPager.setCurrentItem(dayActivityList.indexOf(activity));
            updateFlow(dayActivityList.indexOf(activity));
        } else {
            YonaActivity.getActivity().onBackPressed();
        }

        setTitleAndIcon();
    }

    private void setTitleAndIcon() {
        if (activity != null) {
            toolbarTitle.setText(activity.getYonaGoal().getActivityCategoryName().toUpperCase());
        }
        leftIcon.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);
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
