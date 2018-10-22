/*
 * Copyright (c) 2018 Stichting Yona Foundation
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

import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.enums.NotificationMessageEnum;
import nu.yona.app.ui.StickyHeaderHolder;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.frinends.OnFriendsItemClickListener;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class MessageStickyRecyclerAdapter extends RecyclerView.Adapter<MessageItemViewHolder> implements StickyRecyclerHeadersAdapter<StickyHeaderHolder>
{

	private List<YonaMessage> listYonaMessage;
	private final YonaActivity activity;
	private YonaMessage currentYonaMessage;
	private MessageItemViewHolder currentMessageItemHolder;
	private final OnFriendsItemClickListener mOnFriendsItemClickListener;

	/**
	 * Instantiates a new Message sticky recycler adapter.
	 *
	 * @param yonaMessages      the yona messages
	 * @param yonaActivity      the yona activity
	 * @param itemClickListener the item click listener
	 */
	public MessageStickyRecyclerAdapter(List<YonaMessage> yonaMessages, YonaActivity yonaActivity, OnFriendsItemClickListener itemClickListener)
	{
		this.listYonaMessage = yonaMessages;
		this.activity = yonaActivity;
		this.mOnFriendsItemClickListener = itemClickListener;

	}

	@Override
	public MessageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item_layout, parent, false);
		return new MessageItemViewHolder(layoutView, mOnFriendsItemClickListener);
	}

	@Override
	public void onBindViewHolder(MessageItemViewHolder holder, int position)
	{
		currentYonaMessage = (YonaMessage) getItem(position);
		currentMessageItemHolder = holder;
		if (currentYonaMessage != null)
		{
			setUpMessageListItemTitle();
			setUpMessageItemHolderDetails();
			currentMessageItemHolder.deleteMsg.setTag(currentYonaMessage);
			currentMessageItemHolder.messageContainer.setTag(currentYonaMessage);
		}
	}

	private void setUpMessageListItemTitle()
	{
		currentMessageItemHolder.txtTitleMsg.setText(currentYonaMessage.getNotificationMessageEnum().getUserMessage());
		currentMessageItemHolder.img_status.setImageResource(currentYonaMessage.getNotificationMessageEnum().getImageId());
	}


	private void setUpMessageItemHolderSwipe()
	{
		boolean isEditable = currentYonaMessage.getLinks() != null && currentYonaMessage.getLinks().getEdit() != null;
		currentMessageItemHolder.swipeLayout.setRightSwipeEnabled(isEditable);
	}

	private void setUpMessageItemHolderDetails()
	{
		setUpMessageItemHolderSwipe();
		if (currentYonaMessage.getNotificationMessageEnum() == NotificationMessageEnum.SYSTEM_MESSAGE)
		{
			setUpSystemMessageItemHolder();
		}
		else if (!TextUtils.isEmpty(currentYonaMessage.getNickname()))
		{
			setUpNonSystemMessageItemHolder();
		}
		setUpMessageContainerBackground();
	}

	private void setUpSystemMessageItemHolder()
	{
		currentMessageItemHolder.img_avtar.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(currentYonaMessage.getMessage()))
		{
			currentMessageItemHolder.txtFooterMsg.setText(currentYonaMessage.getMessage());
		}
		currentMessageItemHolder.profileIconTxt.setVisibility(View.VISIBLE);
		currentMessageItemHolder.profileIconTxt.setText(currentYonaMessage.getNickname().substring(0, 1).toUpperCase());
		currentMessageItemHolder.profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_admin_round));
	}

	private void setUpNonSystemMessageItemHolder()
	{
		currentMessageItemHolder.txtFooterMsg.setText(currentYonaMessage.getNickname());
		if (currentYonaMessage.getNotificationMessageEnum() == NotificationMessageEnum.GOALCONFLICTMESSAGE_ANNOUNCED)
		{
			currentMessageItemHolder.img_avtar.setImageResource(R.drawable.adult_sad);
			currentMessageItemHolder.img_avtar.setVisibility(View.VISIBLE);
			currentMessageItemHolder.profileIconTxt.setVisibility(View.GONE);
		}
		else
		{
			setMessageListItemUserAvatar();
		}
	}

	private void setMessageListItemUserAvatar()
	{
		if (currentYonaMessage.getLinks().getUserPhoto() != null)
		{
			Picasso.with(this.activity).load(currentYonaMessage.getLinks().getUserPhoto().getHref()).noFade().into(currentMessageItemHolder.img_avtar);
			currentMessageItemHolder.img_avtar.setVisibility(View.VISIBLE);
			currentMessageItemHolder.profileIconTxt.setVisibility(View.GONE);
		}
		else
		{
			currentMessageItemHolder.img_avtar.setVisibility(View.GONE);
			currentMessageItemHolder.profileIconTxt.setVisibility(View.VISIBLE);
			currentMessageItemHolder.profileIconTxt.setText(getMessageProfileIconText());
			currentMessageItemHolder.profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_self_round));
		}
	}

	private String getMessageProfileIconText()
	{
		return currentYonaMessage.getNickname().substring(0, 1).toUpperCase();// return nick name from notification object.
	}

	private void setUpMessageContainerBackground()
	{
		boolean isUnread = currentYonaMessage.getLinks() != null && currentYonaMessage.getLinks().getMarkRead() != null;
		int resourceId = (isUnread) ? R.drawable.item_selected_gradient : R.drawable.item_gradient;
		currentMessageItemHolder.messageContainer.setBackground(ContextCompat.getDrawable(activity, resourceId));
	}

	/**
	 * Gets item.
	 *
	 * @param position the position
	 * @return the item
	 */
	public Object getItem(int position)
	{
		return listYonaMessage.get(position);
	}

	@Override
	public long getHeaderId(int position)
	{
		Object mObject = getItem(position);
		return ((YonaMessage) mObject).getStickyTitle().charAt(0);
	}

	@Override
	public StickyHeaderHolder onCreateHeaderViewHolder(ViewGroup parent)
	{
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.message_header_layout, parent, false);
		return new StickyHeaderHolder(view)
		{
		};
	}

	@Override
	public void onBindHeaderViewHolder(StickyHeaderHolder holder, int position)
	{
		Object yonaObject = getItem(position);
		if (yonaObject != null)
		{
			holder.getHeaderText().setText(((YonaMessage) yonaObject).getStickyTitle());
		}
	}

	@Override
	public int getItemCount()
	{
		return listYonaMessage.size();
	}

	/**
	 * Update data.
	 *
	 * @param yonaMessages the yona messages
	 */
	public void updateData(final List<YonaMessage> yonaMessages)
	{
		Set<YonaMessage> unique = new LinkedHashSet<YonaMessage>(listYonaMessage);
		unique.addAll(yonaMessages);
		listYonaMessage = new ArrayList<YonaMessage>(unique);
		notifyDataSetChanged();
	}

	/**
	 * Notify data set change.
	 *
	 * @param yonaMessages the yona messages
	 */
	public void notifyDataSetChange(final List<YonaMessage> yonaMessages)
	{
		this.listYonaMessage = yonaMessages;
		notifyDataSetChanged();
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		while (getItemCount() > 0)
		{
			remove((YonaMessage) getItem(0));
		}
	}

	/**
	 * Remove.
	 *
	 * @param item the item
	 */
	public void remove(YonaMessage item)
	{
		int position = listYonaMessage.indexOf(item);
		if (position > -1)
		{
			listYonaMessage.remove(position);
			notifyItemRemoved(position);
		}
	}
}
