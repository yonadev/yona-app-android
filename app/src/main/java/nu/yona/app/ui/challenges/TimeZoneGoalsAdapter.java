/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
class TimeZoneGoalsAdapter extends RecyclerView.Adapter<TimeZoneGoalViewHolder> {
    private final OnItemClickListener clickListener;
    private List<String> mListYonaGoal;

    /**
     * Instantiates a new Time zone goals adapter.
     *
     * @param listYonaGoal      the list yona goal
     * @param itemClickListener the item click listener
     */
    public TimeZoneGoalsAdapter(List<String> listYonaGoal, OnItemClickListener itemClickListener) {
        this.mListYonaGoal = listYonaGoal;
        this.clickListener = itemClickListener;
    }

    @Override
    public TimeZoneGoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timezoen_goal_item_layout, parent, false);
        TimeZoneGoalViewHolder rHolder = new TimeZoneGoalViewHolder(layoutView, clickListener);
        return rHolder;
    }

    @Override
    public void onBindViewHolder(TimeZoneGoalViewHolder holder, int position) {
        String txtTime = (String) getItem(position);
        if (!TextUtils.isEmpty(txtTime)) {
            holder.indexGoalTxt.setText("" + (position + 1));
            String[] times = AppUtils.getSplitedTime(txtTime);
            Bundle tagBundle = new Bundle();
            if (times.length > 0) {
                holder.startTimeTxt.setText(times[0]);
                holder.endTimeTxt.setText(times[1]);
            }
            tagBundle.putString(AppConstant.TIME, txtTime);
            tagBundle.putInt(AppConstant.POSITION, position);
            holder.startTimeTxt.setTag(tagBundle);
            holder.endTimeTxt.setTag(tagBundle);
            holder.imgDelete.setTag(tagBundle);
        }
    }

    @Override
    public int getItemCount() {
        return this.mListYonaGoal.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private Object getItem(int position) {
        return this.mListYonaGoal.get(position);
    }

    /**
     * Time zone notify data set changed.
     *
     * @param timesList the times list
     */
    public void timeZoneNotifyDataSetChanged(List<String> timesList) {
        this.mListYonaGoal = timesList;
        notifyDataSetChanged();
    }

    /**
     * update time for selected Cell
     *
     * @param position   the position
     * @param updateTime the update time
     */
    public void updateTimeForItem(int position, String updateTime) {
        mListYonaGoal.set(position, updateTime);
        notifyDataSetChanged();
    }

    /**
     * Update time on positon of cell which user has recently updated
     *
     * @param position    the position
     * @param updateTime  the update time
     * @param isStartTime the is start time
     */
    public void updateListItem(int position, String updateTime, boolean isStartTime) {
        StringBuilder timebuilder = new StringBuilder();
        String time = (String) getItem(position);
        String[] times = AppUtils.getSplitedTime(time);
        if (isStartTime) {
            timebuilder.append(updateTime);
            timebuilder.append("-");
            timebuilder.append(times[1]);
        } else {
            timebuilder.append(times[0]);
            timebuilder.append("-");
            timebuilder.append(updateTime);
        }

        mListYonaGoal.set(position, timebuilder.toString());
        notifyDataSetChanged();
    }

    /**
     * removed item from position
     *
     * @param position the position
     */
    public void removeItemFromList(int position) {
        mListYonaGoal.remove(position);
        notifyDataSetChanged();
    }


}
