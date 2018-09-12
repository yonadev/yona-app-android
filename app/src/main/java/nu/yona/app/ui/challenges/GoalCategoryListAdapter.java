/*
 * Copyright (c) 2018 Stichting Yona Foundation
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
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 14/04/16.
 *
 * @param <T> the type parameter
 */
class GoalCategoryListAdapter<T> extends BaseAdapter
{
	private final LayoutInflater mInflater;
	private List<T> mListYonaGoal;

	/**
	 * Instantiates a new Goal category list adapter.
	 *
	 * @param context      the context
	 * @param listYonaGoal the list yona goal
	 */
	public GoalCategoryListAdapter(Context context, List<T> listYonaGoal)
	{
		this.mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.mListYonaGoal = listYonaGoal;
	}

	/**
	 * Notify data set changed.
	 *
	 * @param mListYonaGoal the m list yona goal
	 */
	public void notifyDataSetChanged(List<T> mListYonaGoal)
	{
		this.mListYonaGoal = mListYonaGoal;
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
		GaolViewHolder goalViewHolder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.category_goal_item_layout, parent, false);
			goalViewHolder = new GaolViewHolder();
			goalViewHolder.title_goal = (YonaFontTextView) convertView.findViewById(R.id.goal_title);
			convertView.setTag(goalViewHolder);
		}
		else
		{
			goalViewHolder = (GaolViewHolder) convertView.getTag();
		}

		Object object = getItem(position);
		if (object instanceof YonaActivityCategories)
		{
			YonaActivityCategories mYonaActivityCategories = (YonaActivityCategories) getItem(position);
			if (mYonaActivityCategories != null)
			{
				goalViewHolder.title_goal.setText(mYonaActivityCategories.getName());
			}
		}
		return convertView;
	}

}
