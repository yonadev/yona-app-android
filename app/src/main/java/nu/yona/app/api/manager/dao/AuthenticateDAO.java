/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import nu.yona.app.api.db.DBConstant;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class AuthenticateDAO extends BaseDAO
{


	/**
	 * Instantiates a new Authenticate dao.
	 *
	 * @param context the context
	 */
	public AuthenticateDAO(Context context)
	{
		super(DatabaseHelper.getInstance(context));
	}

	/**
	 * Update data for register user.
	 *
	 * @param result   the result
	 * @param listener the listener
	 */
	public void updateDataForRegisterUser(Object result, DataLoadListener listener)
	{
		// do process for storing data in database.
		try
		{
			ContentValues values = new ContentValues();
			String USER_ID = "1";
			values.put(DBConstant.ID, USER_ID);
			values.put(DBConstant.SOURCE_OBJECT, serializer.serialize(result));
			// we will store only one user in database, so check if already user exist in db, just update.
			if (getUser() == null)
			{
				insert(DBConstant.TBL_USER_DATA, values);
			}
			else
			{
				update(DBConstant.TBL_USER_DATA, values, DBConstant.ID + " = ?", USER_ID);
			}
			if (listener != null)
			{
				listener.onDataLoad(getUser());
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateDAO.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets user.
	 *
	 * @return the user
	 */
	public User getUser()
	{
		Cursor c = query(DBConstant.TBL_USER_DATA);
		try
		{
			if (c != null && c.getCount() > 0)
			{

				if (c.moveToFirst())
				{
					return serializer.deserialize(c.getBlob(c.getColumnIndex(DBConstant.SOURCE_OBJECT)), User.class);
				}
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityCategories.class.getSimpleName(), e, Thread.currentThread());
		}
		finally
		{
			if (c != null)
			{
				c.close();
			}
		}
		return null;
	}
	
	public JSONObject getStoredUserJSON()
	{
		Cursor c = query(DBConstant.TBL_USER_DATA);
		try
		{
			if (c != null && c.getCount() > 0)
			{

				if (c.moveToFirst())
				{
					return new JSONObject(new String(c.getBlob(c.getColumnIndex(DBConstant.SOURCE_OBJECT))));
				}
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityCategories.class.getSimpleName(), e, Thread.currentThread());
		}
		finally
		{
			if (c != null)
			{
				c.close();
			}
		}
		return null;
	}
}
