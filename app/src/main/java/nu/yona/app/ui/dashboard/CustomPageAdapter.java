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

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.graph.TimeBucketGraph;
import nu.yona.app.customview.graph.TimeFrameGraph;
import nu.yona.app.ui.ChartItemHolder;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class CustomPageAdapter extends PagerAdapter {

    private Context mContext;
    private List<DayActivity> dayActivities;
    private List<WeekActivity> weekActivities;
    private YonaFontTextView dateTitle;

    /**
     * Instantiates a new Custom page adapter.
     *
     * @param context   the context
     * @param dateTitle the date title
     */
    public CustomPageAdapter(Context context, YonaFontTextView dateTitle) {
        mContext = context;
        this.dateTitle = dateTitle;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        if (dayActivities != null) {
            return initiateDayActivityReport(collection, position);
        } else if (weekActivities != null) {
            return initiateWeekActivityReport(collection, position);
        } else {
            return null;
        }
    }

    private ViewGroup initiateDayActivityReport(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.detail_activity_fragment, collection, false);
        DayActivity dayActivity = dayActivities.get(position);
        ((FrameLayout) layout.findViewById(R.id.graphView)).addView(inflateDayActivityView(inflater, dayActivity, layout));
        collection.addView(layout);
        return layout;
    }

    private View inflateDayActivityView(LayoutInflater inflater, DayActivity dayActivity, ViewGroup collection) {
        View layoutView;
        switch (dayActivity.getChartTypeEnum()) {
            case NOGO_CONTROL:
                layoutView = inflater.inflate(R.layout.nogo_chart_layout, collection, false);
                break;
            default:
                layoutView = inflater.inflate(R.layout.goal_chart_item, collection, false);
                break;
        }
        updateView(new ChartItemHolder(layoutView, null, dayActivity.getChartTypeEnum()), dayActivity);
        return layoutView;
    }

    private ViewGroup initiateWeekActivityReport(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.detail_activity_fragment, collection, false);
        collection.addView(layout);
        return layout;
    }

    private void updateView(final ChartItemHolder holder, DayActivity dayActivity) {
        ViewGroup viewGroup = (ViewGroup) holder.getGoalGraphView();
        switch (dayActivity.getChartTypeEnum()) {
            case TIME_FRAME_CONTROL:
                if (dayActivity.getTimeZoneSpread() != null) {
                    TimeFrameGraph timeFrameGraph = new TimeFrameGraph(mContext);
                    timeFrameGraph.chartValuePre(dayActivity.getTimeZoneSpread());
                    viewGroup.addView(timeFrameGraph);
                }
                updatedetail(dayActivity, holder);
                break;
            case TIME_BUCKET_CONTROL:
                int maxDurationAllow = (int) dayActivity.getYonaGoal().getMaxDurationMinutes();
                if (maxDurationAllow > 0) {
                    TimeBucketGraph timeBucketGraph = new TimeBucketGraph(mContext);
                    timeBucketGraph.graphArguments(dayActivity.getTotalMinutesBeyondGoal(), (int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
                    viewGroup.addView(timeBucketGraph);
                }
                updatedetail(dayActivity, holder);
                break;
            case SPREAD_CONTROL:
                break;
            case NOGO_CONTROL:
                holder.getGoalDesc().setText("geen hits, hou vol!");
                if (dayActivity.getGoalAccomplished()) {
                    holder.getNogoStatus().setImageResource(R.drawable.adult_happy);
                } else {
                    holder.getNogoStatus().setImageResource(R.drawable.adult_sad);
                }
                if (dayActivity.getYonaGoal() != null && !TextUtils.isEmpty(dayActivity.getYonaGoal().getType())) {
                    holder.getGoalType().setText(dayActivity.getYonaGoal().getActivityCategoryName());
                }
                break;
            default:
                break;
        }
    }

    private void updatedetail(final DayActivity dayActivity, final ChartItemHolder holder) {
        if (dayActivity.getYonaGoal() != null && !TextUtils.isEmpty(dayActivity.getYonaGoal().getActivityCategoryName())) {
            holder.getGoalType().setText(dayActivity.getYonaGoal().getActivityCategoryName() + "");
        }
        holder.getGoalScore().setText(dayActivity.getTotalActivityDurationMinutes() + "");
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        if (dayActivities != null) {
            return dayActivities.size();
        } else if (weekActivities != null) {
            return weekActivities.size();
        } else {
            return 0;
        }
    }

    /**
     * Notify data set changed.
     *
     * @param activityList the activity list
     */
    public void notifyDataSetChanged(List<?> activityList) {
        if (activityList != null && activityList.get(0) instanceof DayActivity) {
            dayActivities = (List<DayActivity>) activityList;
        } else {
            weekActivities = (List<WeekActivity>) activityList;
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}