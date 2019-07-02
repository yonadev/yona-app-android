/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.friends.OnFriendsItemClickListener;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class MessageItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

	/**
	 * Swipe layout
	 */
	public com.daimajia.swipe.SwipeLayout swipeLayout;
	/**
	 * The Img avtar.
	 */
	public CircleImageView img_avtar;
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
	public LinearLayout deleteMsg;
	/**
	 * The Message container.
	 */
	public LinearLayout messageContainer;
	private final OnFriendsItemClickListener onFriendsItemClickListener;
	public YonaFontTextView profileIconTxt;

	/**
	 * Instantiates a new Message item view holder.
	 *
	 * @param itemView          the item view
	 * @param itemClickListener click of item view
	 */
	public MessageItemViewHolder(View itemView, OnFriendsItemClickListener itemClickListener)
	{
		super(itemView);
		this.onFriendsItemClickListener = itemClickListener;
		swipeLayout = (com.daimajia.swipe.SwipeLayout) itemView.findViewById(R.id.swipe_layout);
		messageContainer = (LinearLayout) itemView.findViewById(R.id.messageContainer);
		deleteMsg = (LinearLayout) itemView.findViewById(R.id.swipe_delete_goal);
		img_avtar = (CircleImageView) itemView.findViewById(R.id.img_user_icon);
		profileIconTxt = (YonaFontTextView) itemView.findViewById(R.id.profileTextIcon);
		img_status = (ImageView) itemView.findViewById(R.id.img_status);
		txtTitleMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_title);
		txtFooterMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_footer);
		messageContainer.setOnClickListener(this);
		deleteMsg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.swipe_delete_goal:
				if (onFriendsItemClickListener != null)
				{
					onFriendsItemClickListener.onFriendsItemDeleteClick(v);
				}
				break;
			case R.id.messageContainer:
				if (onFriendsItemClickListener != null)
				{
					onFriendsItemClickListener.onFriendsItemClick(v);
				}
				break;
			default:
				if (onFriendsItemClickListener != null)
				{
					onFriendsItemClickListener.onItemClick(v);
				}
				break;
		}
	}
}
