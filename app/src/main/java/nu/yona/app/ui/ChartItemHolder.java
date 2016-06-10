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

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.ChartTypeEnum;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class ChartItemHolder extends RecyclerView.ViewHolder {

    private YonaFontTextView goalType, goalScore, goalDesc;

    /**
     * Instantiates a new Chart item holder.
     *
     * @param itemView      the item view
     * @param listener      the listener
     * @param chartTypeEnum the chart type enum
     */
    public ChartItemHolder(View itemView, View.OnClickListener listener, ChartTypeEnum chartTypeEnum) {
        super(itemView);
        switch (chartTypeEnum) {
            case TIME_FRAME_CONTROL:
                showTimeFrameControl(itemView, listener);
                break;
            case NOGO_CONTROL:
                //TODO set view of nogo
            case TIME_BUCKET_CONTROL:
                showTimeBucketControl(itemView, listener);
                break;
            case SPREAD_CONTROL:
                showSpreadControl(itemView, listener);
                break;
            case WEEK_SCORE_CONTROL:
                showWeekControl(itemView, listener);
                break;
            default:
                break;
        }
    }

    private void showTimeFrameControl(View view, View.OnClickListener listener) {
        inflateCommonView(view);
    }

    private void showTimeBucketControl(View view, View.OnClickListener listener) {
        inflateCommonView(view);
    }

    private void showSpreadControl(View view, View.OnClickListener listener) {
        inflateCommonView(view);
    }

    private void showWeekControl(View view, View.OnClickListener listener) {
        inflateCommonView(view);
    }


    private void inflateCommonView(View view) {
        goalType = (YonaFontTextView) view.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) view.findViewById(R.id.goalScore);
        goalDesc = (YonaFontTextView) view.findViewById(R.id.goalDesc);
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

}
