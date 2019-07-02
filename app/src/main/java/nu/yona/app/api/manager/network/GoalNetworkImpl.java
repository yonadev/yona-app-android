/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhargavsuthar on 15/04/16.
 */
public class GoalNetworkImpl extends BaseImpl
{

	/**
	 * Gets user goals.
	 *
	 * @param url      the url
	 * @param listener the listener
	 */
	public void getUserGoals(String url, DataLoadListener listener)
	{
		try
		{
			getRestApi().getUserGoals(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), Locale.getDefault().toString().replace('_', '-')).enqueue(getGoals(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Put user budget goals.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param goal         the goal
	 * @param listener     the listener
	 */
	public void putUserBudgetGoals(String url, String yonaPassword, PostBudgetYonaGoal goal, DataLoadListener listener)
	{
		try
		{
			getRestApi().putUserGoals(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), goal).enqueue(getGoals(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Put user time zone goals.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param goal         the goal
	 * @param listener     the listener
	 */
	public void putUserTimeZoneGoals(String url, String yonaPassword, PostTimeZoneYonaGoal goal, DataLoadListener listener)
	{
		try
		{
			getRestApi().putUserGoals(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), goal).enqueue(getGoals(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Delete goal.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param listener     the listener
	 */
	public void deleteGoal(String url, String yonaPassword, DataLoadListener listener)
	{
		try
		{
			getRestApi().deleteUserGoal(url, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getCall(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Update user budget goals.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param goal         the goal
	 * @param listener     the listener
	 */
	public void updateUserBudgetGoals(String url, String yonaPassword, PostBudgetYonaGoal goal, DataLoadListener listener)
	{
		try
		{
			getRestApi().updateUserGoal(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), "", goal).enqueue(getGoals(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Update user time zone goals.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param goal         the goal
	 * @param listener     the listener
	 */
	public void updateUserTimeZoneGoals(String url, String yonaPassword, PostTimeZoneYonaGoal goal, DataLoadListener listener)
	{
		try
		{
			getRestApi().updateUserGoal(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), "", goal).enqueue(getGoals(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalNetworkImpl.class, e, Thread.currentThread());
		}
	}

	private Callback<Goals> getGoals(final DataLoadListener listener)
	{
		return new Callback<Goals>()
		{
			@Override
			public void onResponse(Call<Goals> call, Response<Goals> response)
			{
				if (listener == null)
				{
					return;
				}
				if (response.code() < NetworkConstant.RESPONSE_STATUS)
				{
					listener.onDataLoad(response.body());
				}
				else
				{
					onError(response, listener);
				}
			}

			@Override
			public void onFailure(Call<Goals> call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}
}
