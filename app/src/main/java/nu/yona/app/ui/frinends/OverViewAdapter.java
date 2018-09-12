/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.ui.StickyHeaderHolder;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.message.MessageItemViewHolder;

/**
 * Created by bhargavsuthar on 25/05/16.
 */
public class OverViewAdapter extends RecyclerView.Adapter<MessageItemViewHolder> implements StickyRecyclerHeadersAdapter<StickyHeaderHolder>
{

	private final OnFriendsItemClickListener mOnFriendsItemClickListener;
	private List<YonaBuddy> listYonaMessage;
	private final Context context;

	/**
	 * Instantiates a new Overview adapter.
	 *
	 * @param yonaMessages      the yona messages
	 * @param itemClickListener the item click listener
	 */
	public OverViewAdapter(final List<YonaBuddy> yonaMessages, Context context, OnFriendsItemClickListener itemClickListener)
	{
		this.listYonaMessage = yonaMessages;
		this.context = context;
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
		YonaBuddy yonaObject = (YonaBuddy) getItem(position);
		holder.img_status.setVisibility(View.GONE);
		holder.swipeLayout.setRightSwipeEnabled(false);

		if (yonaObject != null && yonaObject.getEmbedded() != null && yonaObject.getEmbedded().getYonaUser() != null)
		{
			StringBuilder username = new StringBuilder();
			if (!TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getFirstName()))
			{
				username.append(yonaObject.getEmbedded().getYonaUser().getFirstName());
			}
			if (!TextUtils.isEmpty(yonaObject.getEmbedded().getYonaUser().getLastName()))
			{
				username.append(" " + yonaObject.getEmbedded().getYonaUser().getLastName());
			}
			holder.txtTitleMsg.setText(username.toString());

			if (yonaObject.getEmbedded() != null)
			{
				if (yonaObject.getEmbedded().getYonaUser() != null)
				{

					// TODO: How about other status, NOT_REQUESTED, REJECTED. We only using single else for rest of case.
					if (StatusEnum.getStatusEnum(yonaObject.getReceivingStatus()) == StatusEnum.ACCEPTED)
					{
						String displayDate = yonaObject.getLastMonitoredActivityDateToDisplay();
						holder.txtFooterMsg.setText(displayDate != null && !displayDate.isEmpty() ?
								yonaObject.getLastMonitoredActivityDateToDisplay() : context.getString(R.string.last_seen_never_seen_online));
					}
					else
					{
						holder.txtFooterMsg.setText(context.getString(R.string.not_accepted_yet));
					}

					if (username.length() > 0)
					{
						holder.profileIconTxt.setVisibility(View.VISIBLE);
						holder.profileIconTxt.setText(username.substring(0, 1).toUpperCase());
						holder.profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_self_round));
					}
				}
			}
			else if (!TextUtils.isEmpty(yonaObject.getNickname()))
			{
				holder.txtFooterMsg.setText(yonaObject.getNickname());
				holder.profileIconTxt.setVisibility(View.VISIBLE);
				holder.profileIconTxt.setText(username.substring(0, 1).toUpperCase());
				holder.profileIconTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_self_round));
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
	public Object getItem(int position)
	{
		return listYonaMessage.get(position);
	}

	@Override
	public long getHeaderId(int position)
	{
		Object mObject = getItem(position);
		return ((YonaBuddy) mObject).getSendingStatus().charAt(0);
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
			holder.getHeaderText().setText(((YonaBuddy) yonaObject).getSendingStatusToDisplay());
		}
	}

	@Override
	public int getItemCount()
	{
		return listYonaMessage.size();
	}

	/**
	 * Notify data set change.
	 *
	 * @param yonaMessages the yona messages
	 */
	public void notifyDataSetChange(final List<YonaBuddy> yonaMessages)
	{
		this.listYonaMessage = yonaMessages;
		sortAdapterItem();
		notifyDataSetChanged();
	}

	private void sortAdapterItem()
	{
		Collections.sort(listYonaMessage, new Comparator<YonaBuddy>()
		{
			@Override
			public int compare(YonaBuddy o1, YonaBuddy o2)
			{
				if (!TextUtils.isEmpty(o1.getSendingStatus()) && !TextUtils.isEmpty(o2.getSendingStatus()))
				{
					return o1.getSendingStatus().compareTo(o2.getSendingStatus());
				}
				return 0;

			}

		});
	}
}

