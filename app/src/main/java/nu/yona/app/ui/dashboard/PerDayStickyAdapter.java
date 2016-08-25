package nu.yona.app.ui.dashboard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.ui.ChartItemHolder;
import nu.yona.app.ui.StickyHeaderHolder;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class PerDayStickyAdapter extends RecyclerView.Adapter<ChartItemHolder> implements StickyRecyclerHeadersAdapter<StickyHeaderHolder> {

    private List<DayActivity> dayActivityList;
    private View.OnClickListener listener;
    private Context mContext;

    /**
     * Instantiates a new Per day sticky adapter.
     *
     * @param chartItem the chart item
     * @param listener  the listener
     */
    public PerDayStickyAdapter(List<DayActivity> chartItem, View.OnClickListener listener) {
        this.dayActivityList = chartItem;
        this.listener = listener;
    }

    @Override
    public ChartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View layoutView;
        switch (ChartTypeEnum.getChartTypeEnum(viewType)) {
            case NOGO_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nogo_chart_layout, parent, false);
                break;
            case TIME_BUCKET_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_budget_item, parent, false);
                break;
            case TIME_FRAME_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_frame_item, parent, false);
                break;
            default:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_chart_item, parent, false);
                break;
        }
        return new ChartItemHolder(layoutView, listener, ChartTypeEnum.getChartTypeEnum(viewType));
    }

    @Override
    public void onBindViewHolder(final ChartItemHolder holder, int position) {
        DayActivity dayActivity = (DayActivity) getItem(position);

        if (dayActivity != null) {
            holder.getView().setTag(dayActivity);
            switch (dayActivity.getChartTypeEnum()) {
                case TIME_FRAME_CONTROL:
                    if (dayActivity.getTimeZoneSpread() != null) {
                        holder.getTimeFrameGraph().chartValuePre(dayActivity.getTimeZoneSpread());
                    }
                    updatedetail(dayActivity, holder);
                    break;
                case TIME_BUCKET_CONTROL:
                    int maxDurationAllow = (int) dayActivity.getYonaGoal().getMaxDurationMinutes();
                    if (maxDurationAllow > 0) {
                        holder.getTimeBucketGraph().graphArguments(dayActivity.getTotalMinutesBeyondGoal(), (int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
                    }
                    updatedetail(dayActivity, holder);
                    break;
                case SPREAD_CONTROL:
                    break;
                case NOGO_CONTROL:
                    if (dayActivity.getGoalAccomplished()) {
                        holder.getNogoStatus().setImageResource(R.drawable.adult_happy);
                        holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalachieved));
                    } else {
                        holder.getNogoStatus().setImageResource(R.drawable.adult_sad);
                        holder.getGoalDesc().setText(mContext.getString(R.string.nogogoalbeyond, dayActivity.getTotalMinutesBeyondGoal()));
                    }
                    if (dayActivity.getYonaGoal() != null && !TextUtils.isEmpty(dayActivity.getYonaGoal().getType())) {
                        holder.getGoalType().setText(dayActivity.getYonaGoal().getActivityCategoryName());
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void updatedetail(final DayActivity dayActivity, ChartItemHolder holder) {
        if (dayActivity.getYonaGoal() != null && !TextUtils.isEmpty(dayActivity.getYonaGoal().getActivityCategoryName())) {
            holder.getGoalType().setText(dayActivity.getYonaGoal().getActivityCategoryName() + "");
        }
        if (!dayActivity.getGoalAccomplished()) {
            if(dayActivity.getChartTypeEnum() == ChartTypeEnum.TIME_BUCKET_CONTROL) {
                holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoalbeyondtime));
            } else {
                holder.getGoalDesc().setText(mContext.getString(R.string.goaloverminute));
            }
        } else {
            holder.getGoalDesc().setText(mContext.getString(R.string.budgetgoaltime));
        }
        if (!dayActivity.getGoalAccomplished()) {
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
        } else {
            holder.getGoalScore().setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        int goalMinutes;
        if (dayActivity.getChartTypeEnum() == ChartTypeEnum.TIME_FRAME_CONTROL) {
            goalMinutes = dayActivity.getTotalMinutesBeyondGoal();
        } else {
            goalMinutes = Math.abs(((int) dayActivity.getYonaGoal().getMaxDurationMinutes()) - dayActivity.getTotalActivityDurationMinutes());
        }
        holder.getGoalScore().setText(goalMinutes + "");
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
