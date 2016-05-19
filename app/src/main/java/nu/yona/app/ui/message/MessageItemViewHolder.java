/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.frinends.OnFriendsItemClickListener;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class MessageItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * Swipe layout
     */
    public com.daimajia.swipe.SwipeLayout swipeLayout;
    /**
     * The Img avtar.
     */
    public ImageView img_avtar;
    /**
     * The Img status.
     */
    public ImageView img_status;
    /**
     * The Txt title msg.
     */
    public YonaFontTextView txtTitleMsg, /**
     * The Txt footer msg.
     */
    txtFooterMsg;

    /**
     * The Delete msg.
     */
    public ImageView deleteMsg;
    private OnFriendsItemClickListener onFriendsItemClickListener;

    /**
     * Instantiates a new Message item view holder.
     *
     * @param itemView          the item view
     * @param itemClickListener click of item view
     */
    public MessageItemViewHolder(View itemView, OnFriendsItemClickListener itemClickListener) {
        super(itemView);
        this.onFriendsItemClickListener = itemClickListener;
        swipeLayout = (com.daimajia.swipe.SwipeLayout) itemView.findViewById(R.id.swipe_layout);
        deleteMsg = (ImageView) itemView.findViewById(R.id.swipe_delete_goal);
        img_avtar = (ImageView) itemView.findViewById(R.id.img_user_icon);
        img_status = (ImageView) itemView.findViewById(R.id.img_status);
        txtTitleMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_title);
        txtFooterMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_footer);
        swipeLayout.setOnClickListener(this);
        deleteMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_delete_goal:
                onFriendsItemClickListener.onFriendsItemDeleteClick(v);
                break;
            case R.id.swipe_layout:
                onFriendsItemClickListener.onFriendsItemClick(v);
                break;
            default:
                break;
        }
    }
}
