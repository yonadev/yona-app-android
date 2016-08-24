/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.ui.StickyHeaderHolder;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.TextDrawable;

/**
 * Created by bhargavsuthar on 28/06/16.
 */
public class TimelineStickyAdapter extends RecyclerView.Adapter<TimelineHolder> implements StickyRecyclerHeadersAdapter<StickyHeaderHolder> {

    private List<DayActivity> dayActivityList;
    private View.OnClickListener listener;
    private Context mContext;

    /**
     * Instantiates a new Per day sticky adapter.
     *
     * @param chartItem the chart item
     * @param listener  the listener
     */
    public TimelineStickyAdapter(List<DayActivity> chartItem, View.OnClickListener listener) {
        this.dayActivityList = chartItem;
        this.listener = listener;
    }

    @Override
    public TimelineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View childView = null;
        switch (ChartTypeEnum.getChartTypeEnum(viewType)) {
            case NOGO_CONTROL:
                childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_nogo_layout, parent, false);
                break;
            case TIME_BUCKET_CONTROL:
                childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_timebucket_layout, parent, false);
                break;
            case TIME_FRAME_CONTROL:
                childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_timeframe_layout, parent, false);
                break;
            case TITLE:
                childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_timeline, parent, false);
                break;
            case LINE:
                childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_line_view, parent, false);
                break;
            default:
                break;
        }
        return new TimelineHolder(childView, listener, ChartTypeEnum.getChartTypeEnum(viewType));
    }

    @Override
    public void onBindViewHolder(final TimelineHolder holder, int position) {
        DayActivity dayActivity = (DayActivity) getItem(position);

        if (dayActivity != null) {
            switch (dayActivity.getChartTypeEnum()) {
                case TIME_FRAME_CONTROL:
                    holder.getView().setTag(dayActivity);
                    if (dayActivity.getTimeZoneSpread() != null) {
                        holder.getmTimeFrameGraph().chartValuePre(dayActivity.getTimeZoneSpread());
                    }
                    updateProfileImage(holder, dayActivity);
                    break;
                case TIME_BUCKET_CONTROL:
                    holder.getView().setTag(dayActivity);
                    int maxDurationAllow = (int) dayActivity.getYonaGoal().getMaxDurationMinutes();
                    if (maxDurationAllow > 0) {
                        holder.getmTimebucketGraph().graphArguments(dayActivity.getTotalMinutesBeyondGoal(), (int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.getmTimebucketGraph().startAnimation();
                            }
                        }, 100);
                    }
                    updateProfileImage(holder, dayActivity);
                    break;
                case SPREAD_CONTROL:
                    break;
                case NOGO_CONTROL:
                    holder.getView().setTag(dayActivity);
                    if (dayActivity.getGoalAccomplished()) {
                        holder.getmNogoImage().setImageResource(R.drawable.adult_happy);
                    } else {
                        holder.getmNogoImage().setImageResource(R.drawable.adult_sad);
                    }
                    holder.getmTxtNogo().setText(dayActivity.getYonaGoal().getNickName());
                    holder.getmTxtNogoTime().setText(mContext.getString(R.string.nogogoalbeyond, dayActivity.getYonaGoal().getMaxDurationMinutes()));
                    break;
                case TITLE:
                    holder.getmHeaderCategoryTypeGoal().setText(dayActivity.getYonaGoal().getActivityCategoryName());
                default:
                    break;
            }
        }
    }

    private void updateProfileImage(TimelineHolder holder, DayActivity dayActivity) {
        holder.getmUserIcon().setImageDrawable(TextDrawable.builder()
                .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                .buildRound(YonaActivity.getActivity(), dayActivity.getYonaGoal().getNickName().substring(0, 1).toUpperCase(),
                        ContextCompat.getColor(YonaActivity.getActivity(), R.color.grape), YonaActivity.getActivity().getResources().getInteger(R.integer.list_item_icon_text_size)));
    }

    @Override
    public int getItemViewType(int position) {
        return dayActivityList.get(position).getChartTypeEnum().getId();
    }

    /**
     * Gets item.
     *
     * @param position the position
     * @return the item
     */
    public Object getItem(int position) {
        return dayActivityList.get(position);
    }


    @Override
    public StickyHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_header_layout, parent, false);
        return new StickyHeaderHolder(view) {
        };
    }

    @Override
    public long getHeaderId(int position) {
        return dayActivityList.get(position).getStickyHeaderId();
    }

    @Override
    public void onBindHeaderViewHolder(StickyHeaderHolder holder, int position) {
        Object yonaObject = getItem(position);
        if (yonaObject != null) {
            holder.getHeaderText().setText(((DayActivity) yonaObject).getStickyTitle());
        }
    }

    @Override
    public int getItemCount() {
        return dayActivityList.size();
    }

    /**
     * Notify data set change.
     *
     * @param dayActivities the yona messages
     */
    public void notifyDataSetChange(final List<DayActivity> dayActivities) {
        this.dayActivityList = dayActivities;
        notifyDataSetChanged();
    }

    /**
     * Clear.
     */
    public void clear() {
        while (getItemCount() > 0) {
            remove((DayActivity) getItem(0));
        }
    }

    /**
     * Remove.
     *
     * @param item the item
     */
    public void remove(DayActivity item) {
        int position = dayActivityList.indexOf(item);
        if (position > -1) {
            dayActivityList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
