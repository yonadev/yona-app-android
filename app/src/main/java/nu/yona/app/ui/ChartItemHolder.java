/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.graph.CircleGraphView;
import nu.yona.app.customview.graph.TimeBucketGraph;
import nu.yona.app.customview.graph.TimeFrameGraph;
import nu.yona.app.enums.ChartTypeEnum;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class ChartItemHolder extends RecyclerView.ViewHolder {

    private YonaFontTextView goalType, goalScore, goalDesc;
    private View view;
    private View goalGraphView;
    private ImageView nogoStatus;
    private TimeFrameGraph timeFrameGraph;
    private TimeBucketGraph timeBucketGraph;
    private View mWeekDayFirst;
    private View mWeekDaySecond;
    private View mWeekDayThird;
    private View mWeekDayFourth;
    private View mWeekDayFifth;
    private View mWeekDaySixth;
    private View mWeekDaySeventh;

    /**
     * Instantiates a new Chart item holder.
     *
     * @param itemView      the item view
     * @param listener      the listener
     * @param chartTypeEnum the chart type enum
     */
    public ChartItemHolder(View itemView, View.OnClickListener listener, ChartTypeEnum chartTypeEnum) {
        super(itemView);
        this.view = itemView;
        itemView.setOnClickListener(listener);
        switch (chartTypeEnum) {
            case NOGO_CONTROL:
                initNoGOControlView();
                break;
            case TIME_BUCKET_CONTROL:
                initTimeBucketControlView();
                break;
            case TIME_FRAME_CONTROL:
                initTimeFrameControlView();
                break;
            case WEEK_SCORE_CONTROL:
                initWeekScoreControlView();
            default:
                initGraphControlView();
                break;
        }

    }

    /**
     * Initialize Nogo Controls
     */
    private void initNoGOControlView() {
        goalType = (YonaFontTextView) view.findViewById(R.id.txtNoGoText);
        goalDesc = (YonaFontTextView) view.findViewById(R.id.txtNogoTime);
        nogoStatus = (ImageView) view.findViewById(R.id.imgNogo);
    }

    /**
     * Initialize default Graph Controls, ex: TimeBucket, TimeFrame, Spread Control
     */

    private void initGraphControlView() {
        initCommonView();
        goalGraphView = view.findViewById(R.id.graphView);
    }

    private void initTimeBucketControlView() {
        initCommonView();
        timeBucketGraph = (TimeBucketGraph) view.findViewById(R.id.timeBucketGraph);
    }

    private void initTimeFrameControlView() {
        initCommonView();
        timeFrameGraph = (TimeFrameGraph) view.findViewById(R.id.timeFrameControl);
    }

    private void initWeekScoreControlView() {
        initCommonView();
        mWeekDayFirst = view.findViewById(R.id.weekday_first);
        mWeekDaySecond = view.findViewById(R.id.weekday_second);
        mWeekDayThird = view.findViewById(R.id.weekday_third);
        mWeekDayFourth = view.findViewById(R.id.weekday_fourth);
        mWeekDayFifth = view.findViewById(R.id.weekday_fifth);
        mWeekDaySixth = view.findViewById(R.id.weekday_sixth);
        mWeekDaySeventh = view.findViewById(R.id.weekday_seventh);
    }

    private void initCommonView() {
        goalType = (YonaFontTextView) view.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) view.findViewById(R.id.goalScore);
        goalDesc = (YonaFontTextView) view.findViewById(R.id.goalDesc);
    }

    /**
     * update text info circle of that date
     *
     * @param view  the view
     * @param day   the day
     * @param date  the date
     * @param color the color
     */
    public synchronized void updateTextOfCircle(View view, String day, String date, int color) {
        ((YonaFontTextView) view.findViewById(R.id.txtWeekOfDay)).setText(day);
        ((YonaFontTextView) view.findViewById(R.id.txtDateOfWeek)).setText(date);
        CircleGraphView mWeekCircle = (CircleGraphView) view.findViewById(R.id.circle_view);
        mWeekCircle.setFillColor(color);
        mWeekCircle.invalidate();
    }

    /**
     * Gets goal type.
     *
     * @return the goal type
     */
    public YonaFontTextView getGoalType() {
        return this.goalType;
    }

    /**
     * Gets goal desc.
     *
     * @return the goal desc
     */
    public YonaFontTextView getGoalDesc() {
        return this.goalDesc;
    }

    /**
     * Gets goal score.
     *
     * @return the goal score
     */
    public YonaFontTextView getGoalScore() {
        return this.goalScore;
    }

    /**
     * Get GraphView
     *
     * @return goal graph view
     */
    public View getGoalGraphView() {
        return goalGraphView;
    }

    /**
     * Get layout view
     *
     * @return view view
     */
    public View getView() {
        return view;
    }

    /**
     * Get Image of nogo status
     *
     * @return nogo status
     */
    public ImageView getNogoStatus() {
        return nogoStatus;
    }

    /**
     * Get Time Bucket Graph
     *
     * @return time bucket graph
     */
    public TimeBucketGraph getTimeBucketGraph() {
        return this.timeBucketGraph;
    }

    /**
     * Get Time Frame Graph
     *
     * @return time frame graph
     */
    public TimeFrameGraph getTimeFrameGraph() {
        return this.timeFrameGraph;
    }

    /**
     * Get First Day of Week which is Sunday
     *
     * @return week day first
     */
    public View getmWeekDayFirst() {
        return mWeekDayFirst;
    }

    /**
     * Get Second Day of Week which is Monday
     *
     * @return week day second
     */
    public View getmWeekDaySecond() {
        return mWeekDaySecond;
    }

    /**
     * Get third Day of Week which is Tuesday
     *
     * @return week day third
     */
    public View getmWeekDayThird() {
        return mWeekDayThird;
    }

    /**
     * Get fourth Day of Week which is Wednesday
     *
     * @return week day fourth
     */
    public View getmWeekDayFourth() {
        return mWeekDayFourth;
    }

    /**
     * Get fifth Day of Week which is Thusday
     *
     * @return week day fifth
     */
    public View getmWeekDayFifth() {
        return mWeekDayFifth;
    }

    /**
     * Get sixth Day of Week which is Friday
     *
     * @return week day sixth
     */
    public View getmWeekDaySixth() {
        return mWeekDaySixth;
    }

    /**
     * Get seventh Day of Week which is saturday
     *
     * @return week day seventh
     */
    public View getmWeekDaySeventh() {
        return mWeekDaySeventh;
    }
}
