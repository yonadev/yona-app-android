/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class MessageStickyRecyclerAdapter extends RecyclerView.Adapter<MessageItemViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<YonaMessage> listYonaMessage;
    private YonaActivity activity;

    /**
     * Instantiates a new Message sticky recycler adapter.
     *
     * @param yonaMessages the yona messages
     * @param yonaActivity the yona activity
     */
    public MessageStickyRecyclerAdapter(List<YonaMessage> yonaMessages, YonaActivity yonaActivity) {
        this.listYonaMessage = yonaMessages;
        this.activity = yonaActivity;

    }

    @Override
    public MessageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item_layout, parent, false);
        return new MessageItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MessageItemViewHolder holder, int position) {
        YonaMessage yonaObject = (YonaMessage) getItem(position);
        if (yonaObject != null) {
            if (yonaObject.getEmbedded() != null && yonaObject.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getFirstName())) {
                String username = yonaObject.getEmbedded().getYonaUser().getFirstName();
                holder.txtFooterMsg.setText(username);
                if (username.length() > 0) {
                    holder.img_avtar.setImageDrawable(TextDrawable.builder().buildRound(username.substring(0, 1).toUpperCase(),
                            ContextCompat.getColor(activity, R.color.dashboard)));
                }
            }
            if (yonaObject.getNotificationMessageEnum() != null && !TextUtils.isEmpty(yonaObject.getNotificationMessageEnum().getUserMessage())) {
                holder.txtTitleMsg.setText(yonaObject.getNotificationMessageEnum().getUserMessage());
            }
        }
    }

    /**
     * Gets item.
     *
     * @param position the position
     * @return the item
     */
    public Object getItem(int position) {
        return listYonaMessage.get(position);
    }

    @Override
    public long getHeaderId(int position) {
        Object mObject = getItem(position);
        return ((YonaMessage) mObject).getStickyTitle().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_header_layout, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        YonaFontTextView textView = (YonaFontTextView) holder.itemView;
        Object yonaObject = getItem(position);
        if (yonaObject != null) {
            textView.setText(((YonaMessage) yonaObject).getStickyTitle());
        }
    }

    @Override
    public int getItemCount() {
        return listYonaMessage.size();
    }

    /**
     * Update data.
     *
     * @param yonaMessages the yona messages
     */
    public void updateData(final List<YonaMessage> yonaMessages) {
        listYonaMessage.addAll(yonaMessages);
        notifyDataSetChanged();
    }

    /**
     * Notify data set change.
     *
     * @param yonaMessages the yona messages
     */
    public void notifyDataSetChange(final List<YonaMessage> yonaMessages) {
        this.listYonaMessage = yonaMessages;
        notifyDataSetChanged();
    }

    /**
     * Clear.
     */
    public void clear() {
        while (getItemCount() > 0) {
            remove((YonaMessage) getItem(0));
        }
    }

    /**
     * Remove.
     *
     * @param item the item
     */
    public void remove(YonaMessage item) {
        int position = listYonaMessage.indexOf(item);
        if (position > -1) {
            listYonaMessage.remove(position);
            notifyItemRemoved(position);
        }
    }
}
