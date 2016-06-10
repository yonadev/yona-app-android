package nu.yona.app.ui.dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.ui.ChartItemHolder;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public class PerDayStickyAdapter extends RecyclerView.Adapter<ChartItemHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<DayActivity> dayActivityList;
    private View.OnClickListener listener;

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
        View layoutView;
        switch (ChartTypeEnum.getChartTypeEnum(viewType)) {
            case NOGO_CONTROL:
                //TODO set layout of nogo
            case TIME_BUCKET_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timebucket_chart_item, parent, false);
                return new ChartItemHolder(layoutView, listener, ChartTypeEnum.TIME_BUCKET_CONTROL);
            case TIME_FRAME_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeframe_chart_item, parent, false);
                return new ChartItemHolder(layoutView, listener, ChartTypeEnum.TIME_BUCKET_CONTROL);
            case SPREAD_CONTROL:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spreading_chart_item, parent, false);
                return new ChartItemHolder(layoutView, listener, ChartTypeEnum.TIME_BUCKET_CONTROL);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ChartItemHolder holder, int position) {
        DayActivity dayActivity = (DayActivity) getItem(position);

        if (dayActivity != null) {
            holder.getGoalType().setText(dayActivity.getYonaGoal().getActivityCategoryName());
            holder.getGoalScore().setText(dayActivity.getTotalActivityDurationMinutes() + "");
            //TODO fill all other values for item chart here
        }
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
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_header_layout, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public long getHeaderId(int position) {
        Object mObject = getItem(position);
        return ((DayActivity) mObject).getStickyTitle().charAt(0);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        YonaFontTextView textView = (YonaFontTextView) holder.itemView;
        Object yonaObject = getItem(position);
        if (yonaObject != null) {
            textView.setText(((DayActivity) yonaObject).getStickyTitle());
        }
    }

    @Override
    public int getItemCount() {
        return dayActivityList.size();
    }

    /**
     * Update data.
     *
     * @param dayActivities the yona messages
     */
    public void updateData(final List<DayActivity> dayActivities) {
        dayActivityList.addAll(dayActivities);
        notifyDataSetChanged();
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
