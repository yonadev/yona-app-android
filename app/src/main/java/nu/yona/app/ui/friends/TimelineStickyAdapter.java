/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.friends;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.ui.StickyHeaderHolder;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by bhargavsuthar on 28/06/16.
 */
public class TimelineStickyAdapter extends RecyclerView.Adapter<TimelineHolder> implements StickyRecyclerHeadersAdapter<StickyHeaderHolder>
{

	private List<DayActivity> dayActivityList;
	private final View.OnClickListener listener;
	private Context mContext;

	/**
	 * Instantiates a new Per day sticky adapter.
	 *
	 * @param chartItem the chart item
	 * @param listener  the listener
	 */
	public TimelineStickyAdapter(List<DayActivity> chartItem, View.OnClickListener listener)
	{
		this.dayActivityList = chartItem;
		this.listener = listener;
	}

	@Override
	public TimelineHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		mContext = parent.getContext();

		View childView = null;
		switch (ChartTypeEnum.getChartTypeEnum(viewType))
		{
			case NOGO_CONTROL:
				childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_nogo_layout, parent, false);
				break;
			case TIME_BUCKET_CONTROL:
				childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_timebucket_layout, parent, false);
				break;
			case TIME_FRAME_CONTROL:
				childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_timeframe_layout, parent, false);
				break;
			case TITLE:
				childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_timeline, parent, false);
				break;
			case LINE:
				childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_line_view, parent, false);
				break;
			default:
				break;
		}
		return new TimelineHolder(childView, listener, ChartTypeEnum.getChartTypeEnum(viewType));
	}

	@Override
	public void onBindViewHolder(final TimelineHolder holder, int position)
	{
		DayActivity dayActivity = (DayActivity) getItem(position);
		holder.getView().setTag(dayActivity);
		setUpTimeLineViewHolderWithDayActivity(holder, dayActivity);
	}

	private void setUpTimeLineViewHolderWithDayActivity(TimelineHolder holder, DayActivity dayActivity)
	{
		switch (dayActivity.getChartTypeEnum())
		{
			case TIME_FRAME_CONTROL:
				setUpTimeFrameControlHolderView(dayActivity, holder);
				break;
			case TIME_BUCKET_CONTROL:
				setUpTimeBucketControlHolderView(dayActivity, holder);
				break;
			case SPREAD_CONTROL:
				break;
			case NOGO_CONTROL:
				setUpNoGoHolderView(dayActivity, holder);
				break;
			case TITLE:
				holder.getmHeaderCategoryTypeGoal().setText(dayActivity.getYonaGoal().getActivityCategoryName());
			default:
				break;
		}
	}


	private void setUpTimeFrameControlHolderView(DayActivity dayActivity, TimelineHolder holder)
	{
		holder.getmTimeFrameGraph().chartValuePre(dayActivity.getTimeZoneSpread());
		updateProfileImage(holder, dayActivity);
	}

	private void setUpTimeBucketControlHolderView(DayActivity dayActivity, TimelineHolder holder)
	{
		holder.getmTimebucketGraph().graphArguments(dayActivity.getTotalMinutesBeyondGoal(),
				(int) dayActivity.getYonaGoal().getMaxDurationMinutes(), dayActivity.getTotalActivityDurationMinutes());
		updateProfileImage(holder, dayActivity);
	}

	private void setUpNoGoHolderView(DayActivity dayActivity, TimelineHolder holder)
	{
		if (dayActivity.getGoalAccomplished())
		{
			holder.getmNogoImage().setImageResource(R.drawable.adult_happy);
		}
		else
		{
			holder.getmNogoImage().setImageResource(R.drawable.adult_sad);
		}
		holder.getmTxtNogo().setText(dayActivity.getYonaGoal().getNickName());
		holder.getmTxtNogoTime().setText(mContext.getString(R.string.nogogoalbeyond, String.valueOf(dayActivity.getTotalMinutesBeyondGoal())));
	}

	private void updateProfileImage(TimelineHolder holder, DayActivity dayActivity)
	{
		holder.getProfileImageTxt().setVisibility(View.VISIBLE);
		holder.getProfileImageTxt().setText(dayActivity.getYonaGoal().getNickName().substring(0, 1).toUpperCase());
		if (dayActivity.getLinks().getYonaUser() != null)
		{
			holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_friend_round));
		}
		else
		{
			holder.getProfileImageTxt().setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_small_self_round));
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		return dayActivityList.get(position).getChartTypeEnum().getId();
	}

	/**
	 * Gets item.
	 *
	 * @param position the position
	 * @return the item
	 */
	public Object getItem(int position)
	{
		return dayActivityList.get(position);
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
	public long getHeaderId(int position)
	{
		return dayActivityList.get(position).getStickyHeaderId();
	}

	@Override
	public void onBindHeaderViewHolder(StickyHeaderHolder holder, int position)
	{
		Object yonaObject = getItem(position);
		if (yonaObject != null)
		{
			holder.getHeaderText().setText(((DayActivity) yonaObject).getStickyTitle());
		}
	}

	@Override
	public int getItemCount()
	{
		return dayActivityList.size();
	}

	/**
	 * Notify data set change.
	 *
	 * @param dayActivities the yona messages
	 */
	public void notifyDataSetChange(final List<DayActivity> dayActivities)
	{
		this.dayActivityList = dayActivities;
		notifyDataSetChanged();
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		while (getItemCount() > 0)
		{
			remove((DayActivity) getItem(0));
		}
	}

	/**
	 * Remove.
	 *
	 * @param item the item
	 */
	public void remove(DayActivity item)
	{
		int position = dayActivityList.indexOf(item);
		if (position > -1)
		{
			dayActivityList.remove(position);
			notifyItemRemoved(position);
		}
	}
}
