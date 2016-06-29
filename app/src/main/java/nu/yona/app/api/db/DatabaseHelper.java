/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DBConstant.DATABASE_NAME, null, DBConstant.DATABASE_VERSION);
        synchronized (this) {
            Log.i(DatabaseHelper.class.getName(), "DatabaseHelper constructor called");
            this.getWritableDatabase();
        }
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db) {
        try {
            db.execSQL(getDBHelper().TABLE_USER_REGISTER);
            db.execSQL(getDBHelper().TABLE_ACTIVITY_CATEGORY);
            db.execSQL(getDBHelper().TABLE_GOAL);
            db.execSQL(getDBHelper().TABLE_ACTIVITY_TRACKER);
        } catch (Exception e) {
            AppUtils.throwException(DatabaseHelper.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    /**
     * Delete all data.
     */
    public void deleteAllData() {
        try {
            Log.e(DatabaseHelper.class.getSimpleName(), "Delete all data");
            db.execSQL("delete from " + DBConstant.TBL_USER_DATA);
            db.execSQL("delete from " + DBConstant.TBL_ACTIVITY_CATEGORIES);
            db.execSQL("delete from " + DBConstant.TBL_GOAL);
            db.execSQL("delete from " + DBConstant.TBL_ACTIVITY_TRACKER);
            createTables(db);
        } catch (Exception e) {
            AppUtils.throwException(DatabaseHelper.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    private DBHelper getDBHelper() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
        }
        return dbHelper;
    }
}
