/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.utils.AppUtils;

/**
 * Created by bhargavsuthar on 14/04/16.
 *
 * @param <T> the type parameter
 */
class GoalListAdapter<T> extends BaseAdapter
{
	private final Context mContext;
	private final LayoutInflater mInflater;
	private List<T> mListYonaGoal;


	/**
	 * Instantiates a new Goal list adapter.
	 *
	 * @param context      the context
	 * @param listYonaGoal the list yona goal
	 */
	public GoalListAdapter(Context context, List<T> listYonaGoal)
	{
		this.mContext = context;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.mListYonaGoal = listYonaGoal;
	}

	/**
	 * Notify data set changed.
	 *
	 * @param listYonaGoal the list yona goal
	 */
	public void notifyDataSetChanged(List<T> listYonaGoal)
	{
		this.mListYonaGoal = listYonaGoal;
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return mListYonaGoal.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mListYonaGoal.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		GaolViewHolder goalViewHolder;
		if (view == null)
		{
			view = mInflater.inflate(R.layout.goal_item_layout, parent, false);
			goalViewHolder = new GaolViewHolder();
			goalViewHolder.title_goal = (YonaFontTextView) view.findViewById(R.id.goal_title);
			goalViewHolder.desc_goal = (YonaFontTextView) view.findViewById(R.id.goal_sub_content);
			view.setTag(goalViewHolder);
		}
		else
		{
			goalViewHolder = (GaolViewHolder) view.getTag();
		}
		Object object = getItem(position);
		if (object instanceof YonaGoal)
		{
			YonaGoal mYonaGoal = (YonaGoal) getItem(position);
			if (mYonaGoal != null)
			{
				if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString()) && mYonaGoal.getMaxDurationMinutes() > 0)
				{
					goalViewHolder.desc_goal.setText(mContext.getString(R.string.challengesbudgetsubtext, mYonaGoal.getMaxDurationMinutes() + ""));
				}
				else if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString()) && (mYonaGoal.getZones() != null && mYonaGoal.getZones().size() > 0))
				{
					StringBuilder timesBuilder = new StringBuilder();
					for (String timesZone : mYonaGoal.getZones())
					{
						String[] times = AppUtils.getSplitedTime(timesZone);
						if (times.length > 0)
						{
							timesBuilder.append(mContext.getString(R.string.space) + mContext.getString(R.string.challengestimesbetween, times[0], times[1]));
						}
					}
					goalViewHolder.desc_goal.setText(mContext.getString(R.string.challengestimzoesubtext, timesBuilder.toString()));
				}
				else
				{
					goalViewHolder.desc_goal.setText(mContext.getString(R.string.challengesnogosubText));
				}
				goalViewHolder.title_goal.setText(mYonaGoal.getActivityCategoryName());
			}
		}
		return view;
	}

	/**
	 * Update goals list.
	 *
	 * @param yonaGoalList the yona goal list
	 */
	public void updateGoalsList(final List<T> yonaGoalList)
	{
		this.mListYonaGoal = yonaGoalList;
		notifyDataSetChanged();
	}
}
