/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.api.db.DBConstant;
import nu.yona.app.api.model.Activity;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.Logger;

/**
 * Created by kinnarvasa on 08/06/16.
 */
public class ActivityTrackerDAO extends BaseDAO
{

	/**
	 * Instantiates a new Base dao.
	 *
	 * @param mOpenHelper the m open helper
	 */
	public ActivityTrackerDAO(SQLiteOpenHelper mOpenHelper)
	{
		super(mOpenHelper);
	}

	/**
	 * Save activities.
	 *
	 * @param activity the activity
	 */
	public void saveActivities(Activity activity)
	{
		insert(DBConstant.TBL_ACTIVITY_TRACKER, activity.getDbContentValues());
		Logger.logi("APPDEV-1238", "Activity inserted : " + activity.getDbContentValues().toString());
	}

	/**
	 * Gets activities.
	 *
	 * @return the activities
	 */
	public List<Activity> getActivities()
	{
		List<Activity> activityList = new ArrayList<>();
		printTotalActivityCount();
		Cursor c = query(DBConstant.TBL_ACTIVITY_TRACKER, DBConstant.APPLICATION_NAME + " DESC", DBConstant.ROW_LIMIT);
		try
		{
			if (c != null && c.getCount() > 0)
			{
				c.moveToFirst();
				do
				{
					Activity activity = new Activity(c);
					if (!activity.getApplication().equals("NULL"))
					{
						activityList.add(activity);
					}
				} while (c.moveToNext());
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(GoalDAO.class.getSimpleName(), e, Thread.currentThread());
		}
		finally
		{
			if (c != null)
			{
				c.close();
			}
		}
		return activityList;
	}


	/**
	 * Clear activities.
	 */
	public void clearActivities()
	{
		delete(DBConstant.TBL_ACTIVITY_TRACKER,
				DBConstant.APPLICATION_START_TIME +
						" IN (SELECT " + DBConstant.APPLICATION_START_TIME +
						" from " + DBConstant.TBL_ACTIVITY_TRACKER +
						" ORDER BY " + DBConstant.APPLICATION_NAME + " DESC LIMIT " + DBConstant.ROW_LIMIT + ")",
				null);
		printTotalActivityCount();
	}

	public void printTotalActivityCount()
	{
		Cursor c = query(DBConstant.TBL_ACTIVITY_TRACKER);
		Logger.logi("APPDEV-1238", "Activities Count " + c.getCount());
		c.close();
	}

}
