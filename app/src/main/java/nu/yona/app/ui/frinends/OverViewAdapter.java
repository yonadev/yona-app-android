/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.message.MessageItemViewHolder;

/**
 * Created by bhargavsuthar on 25/05/16.
 */
public class OverViewAdapter extends RecyclerView.Adapter<MessageItemViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private final OnFriendsItemClickListener mOnFriendsItemClickListener;
    private List<YonaBuddy> listYonaMessage;

    /**
     * Instantiates a new Overview adapter.
     *
     * @param yonaMessages      the yona messages
     * @param itemClickListener the item click listener
     */
    public OverViewAdapter(final List<YonaBuddy> yonaMessages, OnFriendsItemClickListener itemClickListener) {
        this.listYonaMessage = yonaMessages;
        this.mOnFriendsItemClickListener = itemClickListener;

    }

    @Override
    public MessageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item_layout, parent, false);
        return new MessageItemViewHolder(layoutView, mOnFriendsItemClickListener);
    }

    @Override
    public void onBindViewHolder(MessageItemViewHolder holder, int position) {
        YonaBuddy yonaObject = (YonaBuddy) getItem(position);
        holder.img_status.setVisibility(View.GONE);
        holder.swipeLayout.setRightSwipeEnabled(false);

        if (yonaObject != null && yonaObject.getEmbedded() != null && yonaObject.getEmbedded().getYonaUser() != null) {
            StringBuilder username = new StringBuilder();
            if (!TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getFirstName())) {
                username.append(yonaObject.getEmbedded().getYonaUser().getFirstName());
            }
            if (!TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getLastName())) {
                username.append(yonaObject.getEmbedded().getYonaUser().getLastName());
            }
            holder.txtTitleMsg.setText(username.toString());

            if (yonaObject.getEmbedded() != null) {
                if (yonaObject.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getFirstName())) {
                    String userFirstname = yonaObject.getEmbedded().getYonaUser().getFirstName();
                    holder.txtFooterMsg.setText(userFirstname);
                    if (userFirstname.length() > 0) {
                        holder.img_avtar.setImageDrawable(TextDrawable.builder().buildRound(username.substring(0, 1).toUpperCase(),
                                ContextCompat.getColor(YonaActivity.getActivity(), R.color.grape)));
                    }
                }
            } else if (!TextUtils.isEmpty(yonaObject.getNickname())) {
                holder.txtFooterMsg.setText(yonaObject.getNickname());
                holder.img_avtar.setImageDrawable(TextDrawable.builder().buildRound(yonaObject.getNickname().substring(0, 1).toUpperCase(),
                        ContextCompat.getColor(YonaActivity.getActivity(), R.color.grape)));
            }
        }


        holder.messageContainer.setTag(yonaObject);
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
        return ((YonaBuddy) mObject).getSendingStatus().charAt(0);
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
            textView.setText(((YonaBuddy) yonaObject).getSendingStatus());
        }
    }

    @Override
    public int getItemCount() {
        return listYonaMessage.size();
    }

    /**
     * Notify data set change.
     *
     * @param yonaMessages the yona messages
     */
    public void notifyDataSetChange(final List<YonaBuddy> yonaMessages) {
        this.listYonaMessage = yonaMessages;
        sortAdapterItem();
        notifyDataSetChanged();
    }

    private void sortAdapterItem() {
        Collections.sort(listYonaMessage, new Comparator<YonaBuddy>() {
            public int compare(YonaBuddy o1, YonaBuddy o2) {
                if (!TextUtils.isEmpty(o1.getSendingStatus()) && !TextUtils.isEmpty(o2.getSendingStatus())) {
                    return o1.getSendingStatus().compareTo(o2.getSendingStatus());
                }
                return 0;

            }

        });
    }
}

