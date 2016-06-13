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
import nu.yona.app.enums.ChartTypeEnum;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class ChartItemHolder extends RecyclerView.ViewHolder {

    private YonaFontTextView goalType, goalScore, goalDesc;
    private View view;
    private View goalGraphView;
    private ImageView nogoStatus;

    /**
     * Instantiates a new Chart item holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public ChartItemHolder(View itemView, View.OnClickListener listener, ChartTypeEnum chartTypeEnum) {
        super(itemView);
        this.view = itemView;
        itemView.setOnClickListener(listener);
        switch (chartTypeEnum) {
            case NOGO_CONTROL:
                initNoGOControlView();
                break;
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
        goalType = (YonaFontTextView) view.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) view.findViewById(R.id.goalScore);
        goalDesc = (YonaFontTextView) view.findViewById(R.id.goalDesc);
        goalGraphView = view.findViewById(R.id.graphView);
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
     * @return
     */
    public ImageView getNogoStatus() {
        return nogoStatus;
    }
}
