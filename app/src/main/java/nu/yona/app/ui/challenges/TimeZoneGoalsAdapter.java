/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nu.yona.app.R;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class TimeZoneGoalsAdapter extends RecyclerView.Adapter<TimeZoneGoalViewHolder> {
    private List<String> mListYonaGoal;
    private OnItemClickListener clickListener;

    public TimeZoneGoalsAdapter(List<String> listYonaGoal, OnItemClickListener itemClickListener) {
        this.mListYonaGoal = listYonaGoal;
        this.clickListener = itemClickListener;
    }

    @Override
    public TimeZoneGoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timezoen_goal_item_layout, null);
        TimeZoneGoalViewHolder rHolder = new TimeZoneGoalViewHolder(layoutView, clickListener);
        return rHolder;
    }

    @Override
    public void onBindViewHolder(TimeZoneGoalViewHolder holder, int position) {
        String txtTime = (String) getItem(position);
        if (!TextUtils.isEmpty(txtTime)) {
            holder.indexGoalTxt.setText("" + (position + 1));
            String[] times = txtTime.split("-", 2);
            holder.startTimeTxt.setText(times[0]);
            holder.endTimeTxt.setText(times[1]);
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

    public Object getItem(int position) {
        return this.mListYonaGoal.get(position);
    }

}
