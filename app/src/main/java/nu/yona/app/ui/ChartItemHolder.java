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

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class ChartItemHolder extends RecyclerView.ViewHolder {

    private YonaFontTextView goalType, goalScore, goalDesc;
    private View view;
    private View goalGraphView;

    /**
     * Instantiates a new Chart item holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public ChartItemHolder(View itemView, View.OnClickListener listener) {
        super(itemView);
        this.view = itemView;
        itemView.setOnClickListener(listener);
        goalType = (YonaFontTextView) itemView.findViewById(R.id.goalType);
        goalScore = (YonaFontTextView) itemView.findViewById(R.id.goalScore);
        goalDesc = (YonaFontTextView) itemView.findViewById(R.id.goalDesc);
        goalGraphView = itemView.findViewById(R.id.graphView);
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
     * @return
     */
    public View getGoalGraphView() {
        return goalGraphView;
    }

    /**
     * Get layout view
     *
     * @return
     */
    public View getView() {
        return view;
    }
}
