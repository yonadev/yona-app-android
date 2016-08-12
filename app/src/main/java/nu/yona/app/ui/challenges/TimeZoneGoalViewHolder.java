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
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 21/04/16.
 */
public class TimeZoneGoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * The Start time txt.
     */
    public final YonaFontTextView startTimeTxt;
    /**
     * The End time txt.
     */
    public final YonaFontTextView endTimeTxt;
    /**
     * The Img delete.
     */
    public final ImageView imgDelete;
    private final OnItemClickListener mClickListener;

    /**
     * Instantiates a new Time zone goal view holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public TimeZoneGoalViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);
        itemView.setClickable(true);
        this.mClickListener = listener;
        startTimeTxt = (YonaFontTextView) itemView.findViewById(R.id.txtGoalStartTime);
        endTimeTxt = (YonaFontTextView) itemView.findViewById(R.id.txtGoalEndTime);
        imgDelete = (ImageView) itemView.findViewById(R.id.swipe_delete_goal);
        startTimeTxt.setOnClickListener(this);
        endTimeTxt.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtGoalStartTime:
                mClickListener.onClickStartTime(v);
                break;
            case R.id.txtGoalEndTime:
                mClickListener.onClickEndTime(v);
                break;
            case R.id.swipe_delete_goal:
                mClickListener.onDelete(v);
                break;
            default:
                break;
        }
    }
}
