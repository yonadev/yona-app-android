/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.Logger;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{

	private static DatabaseHelper mInstance = null;
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private DatabaseHelper(Context context)
	{
		super(context, DBConstant.DATABASE_NAME, null, DBConstant.DATABASE_VERSION);
		synchronized (this)
		{
			Logger.logi(DatabaseHelper.class, "DatabaseHelper constructor called");
			this.getWritableDatabase();
		}
	}

	/**
	 * Gets instance.
	 *
	 * @param context the context
	 * @return the instance
	 */
	public static synchronized DatabaseHelper getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		this.db = db;
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	private void createTables(SQLiteDatabase db)
	{
		try
		{
			db.execSQL(getDBHelper().TABLE_USER_REGISTER);
			db.execSQL(getDBHelper().TABLE_ACTIVITY_CATEGORY);
			db.execSQL(getDBHelper().TABLE_GOAL);
			db.execSQL(getDBHelper().TABLE_ACTIVITY_TRACKER);
		}
		catch (Exception e)
		{
			AppUtils.reportException(DatabaseHelper.class, e, Thread.currentThread());
		}
	}

	/**
	 * Delete all data.
	 */
	public void deleteAllData()
	{
		try
		{
			Logger.loge(DatabaseHelper.class, "Delete all data");
			mInstance.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + DBConstant.TBL_USER_DATA);
			mInstance.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + DBConstant.TBL_ACTIVITY_CATEGORIES);
			mInstance.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + DBConstant.TBL_GOAL);
			mInstance.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + DBConstant.TBL_ACTIVITY_TRACKER);
			createTables(mInstance.getWritableDatabase());
		}
		catch (Exception e)
		{
			AppUtils.reportException(DatabaseHelper.class, e, Thread.currentThread());
		}
	}

	private DBHelper getDBHelper()
	{
		if (dbHelper == null)
		{
			dbHelper = new DBHelper();
		}
		return dbHelper;
	}
}
