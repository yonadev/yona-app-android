/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.GoalManager;
import nu.yona.app.api.manager.dao.GoalDAO;
import nu.yona.app.api.manager.network.GoalNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getAppUser;
import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class GoalManagerImpl implements GoalManager
{


	private final GoalNetworkImpl goalNetwork;
	private final GoalDAO goalDAO;
	private final Context mContext;

	/**
	 * Instantiates a new Goal manager.
	 *
	 * @param context the context
	 */
	public GoalManagerImpl(Context context)
	{
		goalNetwork = new GoalNetworkImpl();
		goalDAO = new GoalDAO(DatabaseHelper.getInstance(context), context);
		this.mContext = context;
	}

	/**
	 * Get the User's Goal from server and update into database
	 *
	 * @param listener
	 */
	@Override
	public void getUserGoal(final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref()))
			{
				getSharedAppDataState().setEmbeddedWithBuddyActivity(null);
				goalNetwork.getUserGoals(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * @param goal     PostBudgetYonaGoal object
	 * @param listener
	 */
	@Override
	public void postBudgetGoals(PostBudgetYonaGoal goal, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref()))
			{
				goalNetwork.putUserBudgetGoals(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), goal, new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * @param goal     PostTimeZoneYonaGoal object
	 * @param listener
	 */
	@Override
	public void postTimeZoneGoals(PostTimeZoneYonaGoal goal, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref()))
			{
				goalNetwork.putUserTimeZoneGoals(getAppUser().getEmbedded().getYonaGoals().getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), goal, new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * @param yonaGoal YonaGoal Object
	 * @param listener
	 */
	@Override
	public void deleteGoal(YonaGoal yonaGoal, final DataLoadListener listener)
	{
		try
		{
			if (yonaGoal != null && yonaGoal.getLinks() != null && yonaGoal.getLinks().getSelf() != null && !TextUtils.isEmpty(yonaGoal.getLinks().getSelf().getHref()))
			{
				goalNetwork.deleteGoal(yonaGoal.getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	@Override
	public void updateBudgetGoals(PostBudgetYonaGoal goal, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(goal.getLinks().getSelf().getHref()))
			{
				goalNetwork.updateUserBudgetGoals(goal.getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), goal, new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	@Override
	public void updateTimeZoneGoals(PostTimeZoneYonaGoal goal, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(goal.getLinks().getSelf().getHref()))
			{
				goalNetwork.updateUserTimeZoneGoals(goal.getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), goal, new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						handleError(errorMessage, listener);
					}
				});
			}
			else
			{
				listener.onError(mContext.getString(R.string.urlnotfound));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Get Goals from Database
	 *
	 * @return
	 */
	@Override
	public Goals getUserGoalFromDb()
	{
		return goalDAO.getUserGoal();
	}

	private void handleError(Object errorMessage, DataLoadListener listener)
	{
		if (listener == null)
		{
			return;
		}
		if (errorMessage instanceof ErrorMessage)
		{
			listener.onError(errorMessage);
		}
		else
		{
			listener.onError(new ErrorMessage(errorMessage.toString()));
		}
	}

	@Override
	public void saveGoals(Goals goals, DataLoadListener listener)
	{
		goalDAO.saveGoalData(goals, listener);
	}
}
