/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 21/04/16.
 */
public class TimeZoneGoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public YonaFontTextView indexGoalTxt;
    public YonaFontTextView startTimeTxt;
    public YonaFontTextView endTimeTxt;
    private OnItemClickListener mClickListener;

    public TimeZoneGoalViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);
        itemView.setClickable(true);
        this.mClickListener = listener;
        indexGoalTxt = (YonaFontTextView) itemView.findViewById(R.id.index_time_zone_goal);
        startTimeTxt = (YonaFontTextView) itemView.findViewById(R.id.txtGoalStartTime);
        endTimeTxt = (YonaFontTextView) itemView.findViewById(R.id.txtGoalEndTime);

    }

    @Override
    public void onClick(View v) {
        mClickListener.onItemClick(v, 0);
    }
}
