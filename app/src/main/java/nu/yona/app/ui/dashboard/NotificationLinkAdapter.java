package nu.yona.app.ui.dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.NotificationLinkData;

/**
 * @author spatni
 * Class written to display Link URL under Notification Detail.
 * More Detail https://mobiquity.jira.com/browse/MCT-79.
 */
public class NotificationLinkAdapter extends RecyclerView.Adapter<NotificationLinkAdapter.ViewHolder> {
    private List<NotificationLinkData> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtURL;
        public TextView txtEventTime;

        public ViewHolder(View view) {
            super(view);
            txtURL = (TextView) view.findViewById(R.id.txtLink);
            txtEventTime = (TextView) view.findViewById(R.id.txtEventTime);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationLinkAdapter(List<NotificationLinkData> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationLinkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtURL.setText(mDataset.get(position).getUrl());
        holder.txtEventTime.setText(mDataset.get(position).getEventTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}