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
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.WeekDayActivity;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.graph.CircleGraphView;
import nu.yona.app.customview.graph.SpreadGraph;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.enums.WeekDayEnum;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.ChartItemHolder;
import nu.yona.app.ui.comment.CommentsAdapter;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class CustomPageAdapter extends PagerAdapter {

    private Context mContext;
    private List<DayActivity> dayActivities;
    private List<WeekActivity> weekActivities;
    private YonaFontTextView goalScore;
    private YonaFontTextView goalDesc;
    private YonaFontTextView goalType;
    private SpreadGraph mSpreadGraph;
    private FrameLayout graphView;
    private boolean isWeekControlVisible = true;
    private View.OnClickListener weekItemClickListener;
    private RecyclerView commentRecyclerView;
    private CommentsAdapter commentsAdapter;
    private LinearLayoutManager mLayoutManager;
    public List<YonaMessage> messageList;
    private boolean isUserCommenting = false;
    private ViewGroup layout;
    private View.OnClickListener mCommentClickListener;
    private YonaMessage currentReplyingMsg;

    public RecyclerView.OnScrollListener getRecyclerviewOnScrollListener() {
        return recyclerviewOnScrollListener;
    }

    public void setRecyclerviewOnScrollListener(RecyclerView.OnScrollListener recyclerviewOnScrollListener) {
        this.recyclerviewOnScrollListener = recyclerviewOnScrollListener;
    }

    private RecyclerView.OnScrollListener recyclerviewOnScrollListener;

    /**
     * Instantiates a new Custom page adapter.
     *
     * @param context the context
     */
    public CustomPageAdapter(Context context) {
        mContext = context;
    }

    public CustomPageAdapter(Context context, View.OnClickListener listener) {
        mContext = context;
        weekItemClickListener = listener;
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
        layout = (ViewGroup) inflater.inflate(R.layout.detail_activity_fragment, collection, false);
        View spreadView = layout.findViewById(R.id.spreadGraphView);
        goalDesc = (YonaFontTextView) spreadView.findViewById(R.id.goalDesc);
        goalType = (YonaFontTextView) spreadView.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) spreadView.findViewById(R.id.goalScore);
        mSpreadGraph = (SpreadGraph) spreadView.findViewById(R.id.spreadGraph);
        DayActivity dayActivity = dayActivities.get(position);
        if (dayActivity != null && dayActivity.getLinks() != null && (dayActivity.getLinks().getReplyComment() != null || dayActivity.getLinks().getAddComment() != null)) {
            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SHOW_CHAT_OPTION, null);
        }
        graphView = ((FrameLayout) layout.findViewById(R.id.graphView));
        graphView.addView(inflateActivityView(inflater, dayActivity.getChartTypeEnum(), layout));
        updateView(new ChartItemHolder(graphView, null, dayActivity.getChartTypeEnum()), null, dayActivity);
        collection.addView(layout);
        return layout;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private View inflateActivityView(LayoutInflater inflater, ChartTypeEnum chartTypeEnum, ViewGroup collection) {
        View layoutView;
        switch (chartTypeEnum) {
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

        return layoutView;
    }

    private ViewGroup initiateWeekActivityReport(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.detail_activity_fragment, collection, false);
        WeekActivity weekActivity = weekActivities.get(position);
        View spreadView = layout.findViewById(R.id.spreadGraphView);
        goalDesc = (YonaFontTextView) spreadView.findViewById(R.id.goalDesc);
        goalType = (YonaFontTextView) spreadView.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) spreadView.findViewById(R.id.goalScore);
        mSpreadGraph = (SpreadGraph) spreadView.findViewById(R.id.spreadGraph);
        if (isWeekControlVisible) {
            ViewGroup weekChart = (ViewGroup) layout.findViewById(R.id.week_chart);
            weekChart.setVisibility(View.VISIBLE); // week control
            showWeekChartData(weekChart, weekActivity);
        }
        if (weekActivity != null && weekActivity.getLinks() != null && (weekActivity.getLinks().getReplyComment() != null || weekActivity.getLinks().getAddComment() != null)) {
            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SHOW_CHAT_OPTION, null);
        }
        graphView = ((FrameLayout) layout.findViewById(R.id.graphView));
        GoalsEnum goalsEnum;
        goalsEnum = GoalsEnum.fromName(weekActivity.getYonaGoal().getType());
        if (goalsEnum == GoalsEnum.BUDGET_GOAL && weekActivity.getYonaGoal().getMaxDurationMinutes() == 0) {
            goalsEnum = GoalsEnum.NOGO;
        }
        graphView.addView(inflateActivityView(inflater, goalsEnum, layout));
        updateView(new ChartItemHolder(graphView, null, goalsEnum), weekActivity, null);
        collection.addView(layout);
        return layout;
    }

    private void showWeekChartData(ViewGroup layout, WeekActivity weekActivity) {
        YonaFontTextView goalType = (YonaFontTextView) layout.findViewById(R.id.topPanel).findViewById(R.id.goalType);
        YonaFontTextView goalScore = (YonaFontTextView) layout.findViewById(R.id.topPanel).findViewById(R.id.goalScore);
        YonaFontTextView goalDesc = (YonaFontTextView) layout.findViewById(R.id.topPanel).findViewById(R.id.goalDesc);
        if (weekActivity != null) {
            goalType.setText(R.string.score);
            goalDesc.setText(R.string.week_score);

            for (WeekDayActivity weekDayActivity : weekActivity.getWeekDayActivity()) {
                WeekDayEnum weekDayEnum = weekDayActivity.getWeekDayEnum();
                View view = null;
                switch (weekDayEnum) {
                    case SUNDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_first);
                        view.setTag(weekActivity.getDayActivities().getSUNDAY());
                        break;
                    case MONDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_second);
                        view.setTag(weekActivity.getDayActivities().getMONDAY());
                        break;
                    case TUESDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_third);
                        view.setTag(weekActivity.getDayActivities().getTUESDAY());
                        break;
                    case WEDNESDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_fourth);
                        view.setTag(weekActivity.getDayActivities().getWEDNESDAY());
                        break;
                    case THURSDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_fifth);
                        view.setTag(weekActivity.getDayActivities().getTHURSDAY());
                        break;
                    case FRIDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_sixth);
                        view.setTag(weekActivity.getDayActivities().getFRIDAY());
                        break;
                    case SATURDAY:
                        view = layout.findViewById(R.id.weekScoreControl).findViewById(R.id.weekday_seventh);
                        view.setTag(weekActivity.getDayActivities().getSATURDAY());
                        break;
                    default:
                        break;
                }

                updateTextOfCircle(view, weekDayActivity.getDay(), weekDayActivity.getDate(), weekDayActivity.getColor(), weekActivity);
            }
            goalScore.setText(weekActivity.getTotalAccomplishedGoal() + "");
        }
    }

    private synchronized void updateTextOfCircle(View view, String day, String date, int color, WeekActivity weekActivity) {
        ((YonaFontTextView) view.findViewById(R.id.txtWeekOfDay)).setText(day);
        ((YonaFontTextView) view.findViewById(R.id.txtDateOfWeek)).setText(date);
        CircleGraphView mWeekCircle = (CircleGraphView) view.findViewById(R.id.circle_view);
        mWeekCircle.setTag(R.integer.day_key, view.getTag());
        mWeekCircle.setTag(R.integer.week_key, weekActivity);
        mWeekCircle.setFillColor(color);
        mWeekCircle.invalidate();
        mWeekCircle.setOnClickListener(weekItemClickListener);
    }

    private View inflateActivityView(LayoutInflater inflater, GoalsEnum chartTypeEnum, ViewGroup collection) {
        View layoutView;
        switch (chartTypeEnum) {
            case NOGO:
                layoutView = inflater.inflate(R.layout.nogo_chart_layout, collection, false);
                break;
            case BUDGET_GOAL:
                layoutView = inflater.inflate(R.layout.time_budget_item, collection, false);
                break;
            case TIME_ZONE_GOAL:
                layoutView = inflater.inflate(R.layout.time_frame_item, collection, false);
                break;
            default:
                layoutView = inflater.inflate(R.layout.goal_chart_item, collection, false);
                break;
        }

        return layoutView;
    }

    private void updateView(final ChartItemHolder holder, WeekActivity weekActivity, DayActivity dayActivity) {
        showSpreadGraph(dayActivity, weekActivity);
        if (dayActivity != null) {
            switch (dayActivity.getChartTypeEnum()) {
                case TIME_FRAME_CONTROL:
                    loadTimeFrameControlForDay(dayActivity, holder);
                    break;
                case TIME_BUCKET_CONTROL:
                    loadTimeBucketControlForDay(dayActivity, holder);
                    break;
                case NOGO_CONTROL:
                    loadNoGoControlForDay(dayActivity, holder);
                    break;
                default:
                    break;
            }
        } else {
            switch (GoalsEnum.fromName(weekActivity.getYonaGoal().getType())) {
                case BUDGET_GOAL:
                    if (weekActivity.getYonaGoal().getMaxDurationMinutes() == 0) {
                        loadNoGoControlForWeek(weekActivity, holder);
                    } else {
                        loadTimeBucketControlForWeek(weekActivity, holder);
                    }
                    break;
                case TIME_ZONE_GOAL:
                    loadTimeFrameControlForWeek();
                    break;
                case NOGO:
                    loadNoGoControlForWeek(weekActivity, holder);
                    break;
            }
        }
    }

    private void loadTimeFrameControlForDay(DayActivity dayActivity, ChartItemHolder holder) {
        int timeFrameGoalMinutes = dayActivity.getTotalMinutesBeyondGoal();
        if (dayActivity.getTimeZoneSpread() != null) {
            holder.getTimeFrameGraph().chartValuePre(dayActivity.getTimeZoneSpread());
        }
        holder.getGoalType().setText(mContext.getString(R.string.score));
        holder.getGoalScore().setText(Math.abs(timeFrameGoalMinutes) + "");
        if (!dayActivity.getGoalAccomplished()) {
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoalbeyondtime));
        } else {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoaltime));
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

    }

    private void loadTimeFrameControlForWeek() {
        graphView.setVisibility(View.GONE);
    }

    private void loadTimeBucketControlForDay(DayActivity dayActivity, ChartItemHolder holder) {
        int goalMinutes = ((int) dayActivity.getYonaGoal().getMaxDurationMinutes()) - dayActivity.getTotalActivityDurationMinutes();
        int maxDurationAllow = (int) dayActivity.getYonaGoal().getMaxDurationMinutes();
        if (maxDurationAllow > 0) {
            holder.getTimeBucketGraph().graphArguments(dayActivity.getTotalMinutesBeyondGoal(), (int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
        }
        holder.getGoalType().setText(mContext.getString(R.string.score));
        if (!dayActivity.getGoalAccomplished()) {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoalbeyondtime));
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
        } else {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoaltime));
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        holder.getGoalScore().setText(Math.abs(goalMinutes) + "");
    }

    private void loadTimeBucketControlForWeek(WeekActivity weekActivity, ChartItemHolder holder) {
        Pair<Integer, Integer> avgUsage = getAverageUsageMinute(weekActivity);
        int goalMinutes = ((int) weekActivity.getYonaGoal().getMaxDurationMinutes()) - avgUsage.first;

        int maxDurationAllow = (int) weekActivity.getYonaGoal().getMaxDurationMinutes();
        if (maxDurationAllow > 0) {
            holder.getTimeBucketGraph().graphArguments(Math.abs(goalMinutes), maxDurationAllow, avgUsage.first);
        }
        holder.getGoalType().setText(mContext.getString(R.string.average));
        if (goalMinutes < 0) {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoalbeyondtime));
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
        } else {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoaltime));
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        holder.getGoalScore().setText(Math.abs(goalMinutes) + "");
    }

    private Pair<Integer, Integer> getAverageUsageMinute(WeekActivity weekActivity) {
        int avgMinute = 0;
        int totalDays = 0;
        int beyondGoal = 0;
        if (weekActivity != null && weekActivity.getDayActivities() != null) {
            if (weekActivity.getDayActivities().getMONDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getMONDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getMONDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getTUESDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getTUESDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getTUESDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getWEDNESDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getWEDNESDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getWEDNESDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getTHURSDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getTHURSDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getTHURSDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getFRIDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getFRIDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getFRIDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getSATURDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getSATURDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getSATURDAY().getTotalMinutesBeyondGoal();
            }
            if (weekActivity.getDayActivities().getSUNDAY() != null) {
                totalDays++;
                avgMinute += weekActivity.getDayActivities().getSUNDAY().getTotalActivityDurationMinutes();
                beyondGoal += weekActivity.getDayActivities().getSUNDAY().getTotalMinutesBeyondGoal();
            }
        }
        if (totalDays > 0) {
            avgMinute /= totalDays;
            beyondGoal /= totalDays;
        }
        return new Pair<>(avgMinute, beyondGoal);
    }

    private void loadNoGoControlForDay(DayActivity dayActivity, ChartItemHolder holder) {
        if (dayActivity.getGoalAccomplished()) {
            holder.getNogoStatus().setImageResource(R.drawable.adult_happy);
            holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalachieved));
        } else {
            holder.getNogoStatus().setImageResource(R.drawable.adult_sad);
            holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalbeyond, dayActivity.getTotalMinutesBeyondGoal() + ""));
        }
        holder.getGoalType().setText(mContext.getString(R.string.score));
    }

    private void loadNoGoControlForWeek(WeekActivity weekActivity, ChartItemHolder holder) {
        graphView.setVisibility(View.GONE);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    private void showSpreadGraph(final DayActivity dayActivity, final WeekActivity weekActivity) {
        if (dayActivity != null && dayActivity.getTimeZoneSpread() != null) {
            mSpreadGraph.chartValuePre(dayActivity.getTimeZoneSpread());
        } else if (weekActivity != null && weekActivity.getTimeZoneSpread() != null) {
            mSpreadGraph.chartValuePre(weekActivity.getTimeZoneSpread());
        }
        goalType.setText(mContext.getString(R.string.spreiding));
        if (dayActivity != null) {
            GoalsEnum goalsEnum = GoalsEnum.fromName(dayActivity.getYonaGoal().getType());
            switch (goalsEnum) {
                case BUDGET_GOAL:
                case TIME_ZONE_GOAL:
                    if (dayActivity.getGoalAccomplished()) {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                        goalDesc.setText(mContext.getString(R.string.goaltotalminute));
                    } else {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                        goalDesc.setText(mContext.getString(R.string.budgetgoalbeyondtime));
                    }
                    break;
                case NOGO:
                    goalDesc.setText(mContext.getString(R.string.goaltotalminute));
                    if (dayActivity.getGoalAccomplished()) {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    } else {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                    }
                    break;
                default:
                    break;
            }
            goalScore.setText(dayActivity.getTotalActivityDurationMinutes() + "");
        } else if (weekActivity != null) {
            GoalsEnum goalsEnum = GoalsEnum.fromName(weekActivity.getYonaGoal().getType());
            switch (goalsEnum) {
                case BUDGET_GOAL:
                case TIME_ZONE_GOAL:
                    goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    break;
                case NOGO:
                    goalDesc.setText(mContext.getString(R.string.goaltotalminute));
                    if (weekActivity.getTotalActivityDurationMinutes() > 0) {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                    } else {
                        goalScore.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    }
                    break;
                default:
                    break;
            }
            goalDesc.setText(mContext.getString(R.string.goaltotalminute));
            goalScore.setText("" + weekActivity.getTotalActivityDurationMinutes());
        }

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
        notifyChanges(activityList);
    }

    /*public void notifyDataSetChanged(List<DayActivity> dayActivities1, int position) {
        if (commentsAdapter != null) {
            if (dayActivities1.get(position) != null && dayActivities1.get(position).getComments() != null && dayActivities1.get(position).getComments().getEmbedded() != null && dayActivities1.get(position).getComments().getEmbedded().getYonaMessages() != null) {
                messageList = dayActivities1.get(position).getComments().getEmbedded().getYonaMessages();
                commentsAdapter.notifyData(dayActivities1.get(position).getComments().getEmbedded().getYonaMessages());
            } else {
                commentsAdapter.notifyData(null);
            }
        }
        notifyChanges(dayActivities1);
    }*/

    public void notifyDataSetChanged(List<?> currentActivities, int position) {
        if (commentsAdapter != null && currentActivities != null && currentActivities.size() > 0) {
            if (currentActivities.get(0) instanceof WeekActivity) {
                WeekActivity currentPosWeekActivity = (WeekActivity) currentActivities.get(position);
                if (currentPosWeekActivity != null && currentPosWeekActivity.getComments() != null && currentPosWeekActivity.getComments().getEmbedded() != null && currentPosWeekActivity.getComments().getEmbedded().getYonaMessages() != null) {
                    messageList = currentPosWeekActivity.getComments().getEmbedded().getYonaMessages();
                } else {
                    messageList = null;
                }
            } else {
                DayActivity currentDayActivity = (DayActivity) currentActivities.get(position);
                if (currentDayActivity != null && currentDayActivity.getComments() != null && currentDayActivity.getComments().getEmbedded() != null && currentDayActivity.getComments().getEmbedded().getYonaMessages() != null) {
                    messageList = currentDayActivity.getComments().getEmbedded().getYonaMessages();
                } else {
                    messageList = null;
                }
            }
            commentsAdapter.notifyData(messageList);
        }
        notifyChanges(currentActivities);
    }

    /**
     * Notify data set changed.
     *
     * @param activityList the activity list
     */
    public void notifyDataSetChanged(List<?> activityList, boolean isWeekControlVisible) {
        this.isWeekControlVisible = isWeekControlVisible;
        notifyChanges(activityList);
    }

    private void notifyChanges(List<?> activityList) {
        if (activityList != null && activityList.size() > 0 && activityList.get(0) instanceof DayActivity) {
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