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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.graph.SpreadGraph;
import nu.yona.app.ui.ChartItemHolder;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class CustomPageAdapter extends PagerAdapter {

    private Context mContext;
    private List<DayActivity> dayActivities;
    private List<WeekActivity> weekActivities;
    private YonaFontTextView dateTitle;
    private YonaFontTextView goalScore;
    private YonaFontTextView goalDesc;
    private YonaFontTextView goalType;
    private SpreadGraph mSpreadGraph;

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
        goalDesc = (YonaFontTextView) layout.findViewById(R.id.goalDesc);
        goalType = (YonaFontTextView) layout.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) layout.findViewById(R.id.goalScore);
        mSpreadGraph = (SpreadGraph) layout.findViewById(R.id.spreadGraph);
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
            case TIME_BUCKET_CONTROL:
                layoutView = inflater.inflate(R.layout.time_budget_item, collection, false);
                break;
            case TIME_FRAME_CONTROL:
                layoutView = inflater.inflate(R.layout.time_frame_item, collection, false);
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
                    holder.getTimeFrameGraph().chartValuePre(dayActivity.getTimeZoneSpread());
                }
                holder.getGoalType().setText(mContext.getString(R.string.score));
                holder.getGoalScore().setText(dayActivity.getTotalActivityDurationMinutes() + "");
                if (dayActivity.getTotalMinutesBeyondGoal() > 0) {
                    holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                } else {
                    holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                showSpreadGraph(dayActivity);
                break;
            case TIME_BUCKET_CONTROL:
                int maxDurationAllow = (int) dayActivity.getYonaGoal().getMaxDurationMinutes();
                if (maxDurationAllow > 0) {
                    holder.getTimeBucketGraph().graphArguments(dayActivity.getTotalMinutesBeyondGoal(), (int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
                }
                holder.getGoalType().setText(mContext.getString(R.string.score));
                if (dayActivity.getTotalMinutesBeyondGoal() > 0) {
                    holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoalbeyondtime));
                } else {
                    holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoaltime));
                }
                if (dayActivity.getTotalMinutesBeyondGoal() > 0) {
                    holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                } else {
                    holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                holder.getGoalScore().setText(dayActivity.getTotalActivityDurationMinutes() + "");
                showSpreadGraph(dayActivity);
                break;
            case NOGO_CONTROL:
                if (dayActivity.getGoalAccomplished()) {
                    holder.getNogoStatus().setImageResource(R.drawable.adult_happy);
                    holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalachieved));
                } else {
                    holder.getNogoStatus().setImageResource(R.drawable.adult_sad);
                    holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalbeyond));
                }
                holder.getGoalType().setText(mContext.getString(R.string.score));
                break;
            default:
                break;
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    private void showSpreadGraph(final DayActivity dayActivity) {
        if (dayActivity.getTimeZoneSpread() != null) {
            mSpreadGraph.chartValuePre(dayActivity.getTimeZoneSpread());
        }
        goalType.setText(mContext.getString(R.string.spreiding));
        goalScore.setText(dayActivity.getTotalActivityDurationMinutes() + "");
        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        goalDesc.setText(mContext.getString(R.string.goaltotalminute));
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