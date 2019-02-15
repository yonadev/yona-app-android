/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.comment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by bhargavsuthar on 28/07/16.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentHolder>
{

	private List<YonaMessage> listMessages;
	private final View.OnClickListener commentsItemClick;
	private Context mContext;
	private boolean isReplyingComment;

	public CommentsAdapter(List<YonaMessage> messageList, View.OnClickListener clickListener)
	{
		listMessages = messageList;
		commentsItemClick = clickListener;
	}


	@Override
	public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		mContext = parent.getContext();
		View layoutView = null;
		switch (viewType)
		{
			case 1:
				layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_message_item, parent, false);
				break;
			case 2:
				layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_message_item, parent, false);
				break;
			default:
				layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_message_item, parent, false);
				break;
		}

		return new CommentHolder(layoutView, commentsItemClick, viewType);
	}

	@Override
	public void onBindViewHolder(CommentHolder holder, int position)
	{
		YonaMessage mYonaMsg = getItem(position);

		switch (holder.getItemViewType())
		{
			case 1:
				if (position == 0)
				{
					holder.getLineTop().setVisibility(View.GONE);
				}
				else
				{
					holder.getLineTop().setVisibility(View.VISIBLE);
				}
				if (!TextUtils.isEmpty(mYonaMsg.getNickname()))
				{
					holder.getProfileImageTxt().setVisibility(View.VISIBLE);
					holder.getProfileImageTxt().setText(mYonaMsg.getNickname().toString().substring(0, 1).toUpperCase());
					try
					{
						if (getSharedAppDataState().getUser().getLinks().getSelf().getHref().contains(mYonaMsg.getLinks().getYonaUser().getHref()))
						{
							holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_friend_round));
						}
						else
						{
							holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_self_round));
						}
					}
					catch (Exception e)
					{
						AppUtils.reportException(CommentsAdapter.class, e, Thread.currentThread());
						holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_friend_round));
					}

				}
				holder.getTxtParentBuddyName().setText(mYonaMsg.getNickname());
				holder.getTxtParentBuddyMsg().setText(mYonaMsg.getMessage());
				if (!isReplyingComment())
				{
					if (mYonaMsg.getLinks() != null && mYonaMsg.getLinks().getReplyComment() != null && !TextUtils.isEmpty(mYonaMsg.getLinks().getReplyComment().getHref()))
					{
						holder.getTxtParentCommentReplay().setVisibility(View.VISIBLE);
					}
					else
					{
						holder.getTxtParentCommentReplay().setVisibility(View.GONE);
					}
				}
				else
				{
					holder.getTxtParentCommentReplay().setVisibility(View.GONE);
				}
				holder.getTxtParentCommentReplay().setTag(mYonaMsg);
				break;
			case 2:
				if (!TextUtils.isEmpty(mYonaMsg.getNickname()))
				{
					holder.getProfileImageTxt().setVisibility(View.VISIBLE);
					holder.getProfileImageTxt().setText(mYonaMsg.getNickname().toString().substring(0, 1).toUpperCase());
					try
					{
						if (getSharedAppDataState().getUser().getLinks().getSelf().getHref().contains(mYonaMsg.getLinks().getYonaUser().getHref()))
						{
							holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_friend_round));
						}
						else
						{
							holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_self_round));
						}
					}
					catch (Exception e)
					{
						AppUtils.reportException(CommentsAdapter.class, e, Thread.currentThread());
						holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_small_friend_round));
					}
				}
				holder.getTxtChildBuddyName().setText(mYonaMsg.getNickname());
				holder.getTxtChildBuddyMsg().setText(mYonaMsg.getMessage());
				if (!isReplyingComment())
				{
					if (mYonaMsg.getLinks() != null && mYonaMsg.getLinks().getReplyComment() != null && !TextUtils.isEmpty(mYonaMsg.getLinks().getReplyComment().getHref()))
					{
						holder.getTxtChildCommentReplay().setVisibility(View.VISIBLE);
					}
					else
					{
						holder.getTxtChildCommentReplay().setVisibility(View.GONE);
					}
				}
				else
				{
					holder.getTxtChildCommentReplay().setVisibility(View.GONE);
				}
				holder.getTxtChildCommentReplay().setTag(mYonaMsg);
				break;
			default:
				break;

		}


	}

	private YonaMessage getItem(int position)
	{
		return listMessages.get(position);
	}

	@Override
	public int getItemViewType(int position)
	{
		int viewType = 1;
		YonaMessage currentYonaMsg = listMessages.get(position);
		if (position > 0 && currentYonaMsg != null
				&& currentYonaMsg.getLinks() != null
				&& currentYonaMsg.getLinks().getRepliedMessage() != null
				&& !TextUtils.isEmpty(currentYonaMsg.getLinks().getRepliedMessage().getHref()))
		{
			viewType = 2;
		}
		return viewType;
	}

	public void notifyData(List<YonaMessage> listMessages)
	{
		this.listMessages = listMessages;
	}

	public void notifyDatasetChanged(List<YonaMessage> listMessages)
	{
		this.listMessages = listMessages;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		return listMessages != null ? listMessages.size() : 0;
	}

	public boolean isReplyingComment()
	{
		return isReplyingComment;
	}

	public void setReplyingComment(boolean replyingComment)
	{
		isReplyingComment = replyingComment;
	}
}
