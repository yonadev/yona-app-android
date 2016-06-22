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

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivities;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.ui.ChartItemHolder;
import nu.yona.app.utils.DateUtility;

/**
 * Created by kinnarvasa on 09/06/16.
 */
public class PerWeekStickyAdapter extends RecyclerView.Adapter<ChartItemHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<WeekActivity> perWeekActivityList;
    private View.OnClickListener listener;

    /**
     * Instantiates a new Per Week sticky adapter.
     *
     * @param chartItem the chart item
     * @param listener  the listener
     */
    public PerWeekStickyAdapter(List<WeekActivity> chartItem, View.OnClickListener listener) {
        this.perWeekActivityList = chartItem;
        this.listener = listener;
    }

    @Override
    public ChartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_chart_item, parent, false);
        return new ChartItemHolder(layoutView, listener, ChartTypeEnum.WEEK_SCORE_CONTROL);
    }

    @Override
    public void onBindViewHolder(ChartItemHolder holder, int position) {
        WeekActivity weekActivity = (WeekActivity) getItem(position);

        if (weekActivity != null) {
            if (weekActivity.getYonaGoal() != null && !TextUtils.isEmpty(weekActivity.getYonaGoal().getActivityCategoryName())) {
                holder.getGoalType().setText(weekActivity.getYonaGoal().getActivityCategoryName());
            }
            holder.getGoalDesc().setText(R.string.week_score);
            Iterator calDates = DateUtility.getWeekDay(weekActivity.getDate()).entrySet().iterator();
            int i = 0;
            boolean isCurrentDateReached = false;
            int mAccomplishedGoalCount = 0;
            while (calDates.hasNext()) {
                Map.Entry pair = (Map.Entry) calDates.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                Calendar calendar = Calendar.getInstance();
                View view = null;
                DayActivities dayActivity = weekActivity.getDayActivities();
                boolean isAccomplised = false;
                switch (i) {
                    case 0:
                        view = holder.getmWeekDayFirst();
                        isAccomplised = (dayActivity != null && dayActivity.getSUNDAY() != null) ? dayActivity.getSUNDAY().getGoalAccomplished() : false;
                        break;
                    case 1:
                        view = holder.getmWeekDaySecond();
                        isAccomplised = (dayActivity != null && dayActivity.getMONDAY() != null) ? dayActivity.getMONDAY().getGoalAccomplished() : false;
                        break;
                    case 2:
                        view = holder.getmWeekDayThird();
                        isAccomplised = (dayActivity != null && dayActivity.getTUESDAY() != null) ? dayActivity.getTUESDAY().getGoalAccomplished() : false;
                        break;
                    case 3:
                        view = holder.getmWeekDayFourth();
                        isAccomplised = (dayActivity != null && dayActivity.getWEDNESDAY() != null) ? dayActivity.getWEDNESDAY().getGoalAccomplished() : false;
                        break;
                    case 4:
                        view = holder.getmWeekDayFifth();
                        isAccomplised = (dayActivity != null && dayActivity.getTHURSDAY() != null) ? dayActivity.getTHURSDAY().getGoalAccomplished() : false;
                        break;
                    case 5:
                        view = holder.getmWeekDaySixth();
                        isAccomplised = (dayActivity != null && dayActivity.getFRIDAY() != null) ? dayActivity.getFRIDAY().getGoalAccomplished() : false;
                        break;
                    case 6:
                        view = holder.getmWeekDaySeventh();
                        isAccomplised = (dayActivity != null && dayActivity.getSATURDAY() != null) ? dayActivity.getSATURDAY().getGoalAccomplished() : false;
                        break;
                    default:
                        break;
                }
                holder.updateTextOfCircle(view, pair.getKey().toString(), pair.getValue().toString(), isAccomplised, isCurrentDateReached);
                if (!isCurrentDateReached) {
                    isCurrentDateReached = DateUtility.DAY_NO_FORMAT.format(calendar.getTime()).equals(pair.getValue().toString());
                }
                if (isAccomplised) {
                    mAccomplishedGoalCount++;
                }
                i++;
            }
            holder.getGoalScore().setText(mAccomplishedGoalCount + "");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return perWeekActivityList.get(position).getChartTypeEnum().getId();
    }

    /**
     * Gets item.
     *
     * @param position the position
     * @return the item
     */
    public Object getItem(int position) {
        return perWeekActivityList.get(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_header_layout, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public long getHeaderId(int position) {
        Object mObject = getItem(position);
        return ((WeekActivity) mObject).getStickyTitle().charAt(0);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        YonaFontTextView textView = (YonaFontTextView) holder.itemView;
        Object yonaObject = getItem(position);
        if (yonaObject != null) {
            textView.setText(((WeekActivity) yonaObject).getStickyTitle());

        }
    }

    @Override
    public int getItemCount() {
        return perWeekActivityList.size();
    }

    /**
     * Update data.
     *
     * @param weekActivityList the yona messages
     */
    public void updateData(final List<WeekActivity> weekActivityList) {
        perWeekActivityList.addAll(weekActivityList);
        notifyDataSetChanged();
    }

    /**
     * Notify data set change.
     *
     * @param weekActivityList the yona messages
     */
    public void notifyDataSetChange(final List<WeekActivity> weekActivityList) {
        this.perWeekActivityList = weekActivityList;
        notifyDataSetChanged();
    }


    /**
     * Clear.
     */
    public void clear() {
        while (getItemCount() > 0) {
            remove((WeekActivity) getItem(0));
        }
    }

    /**
     * Remove.
     *
     * @param item the item
     */
    public void remove(WeekActivity item) {
        int position = perWeekActivityList.indexOf(item);
        if (position > -1) {
            perWeekActivityList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
