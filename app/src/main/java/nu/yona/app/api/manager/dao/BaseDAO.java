/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import nu.yona.app.api.db.DbSerializer;
import nu.yona.app.api.db.JsonSerializer;
import nu.yona.app.api.model.BaseEntity;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoader;
import nu.yona.app.utils.AppUtils;

/**
 * Author @MobiquityInc
 */
class BaseDAO {

    private final SQLiteOpenHelper mOpenHelper;
    /**
     * The Serializer.
     */
    final DbSerializer serializer = new JsonSerializer();

    /**
     * Instantiates a new Base dao.
     *
     * @param mOpenHelper the m open helper
     * @param context     the context
     */
    BaseDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        this.mOpenHelper = mOpenHelper;
    }

    /**
     * Delete.
     *
     * @param tableName the table name
     * @param where     the where
     * @param whereArgs the where args
     */
    protected void delete(String tableName, String where, String[] whereArgs) {
        if (mOpenHelper != null) {
            mOpenHelper.getWritableDatabase().delete(tableName, where, whereArgs);
        }
    }

    /**
     * Insert long.
     *
     * @param tableName     the table name
     * @param initialValues the initial values
     * @return the long
     */
    long insert(String tableName, ContentValues initialValues) {
        if (mOpenHelper != null) {
            return mOpenHelper.getWritableDatabase().insertOrThrow(tableName, null, initialValues);
        }
        return 0;
    }

    /**
     * Update.
     *
     * @param tableName the table name
     * @param values    the values
     * @param where     the where
     * @param whereArgs the where args
     */
    void update(String tableName, ContentValues values, String where, String... whereArgs) {
        if (mOpenHelper != null) {
            mOpenHelper.getWritableDatabase().update(tableName, values, where, whereArgs);
        }
    }

    /**
     * Query cursor.
     *
     * @param tableName the table name
     * @return the cursor
     */
    Cursor query(String tableName) {
        if (mOpenHelper == null) {
            return null;
        }
        return mOpenHelper.getWritableDatabase().query(tableName, null, null, null, null, null, null);
    }

    /**
     * Bulk insert.
     *
     * @param tableName the table name
     * @param items     the items
     * @param listener  the listener
     */
    protected void bulkInsert(final String tableName, final List<? extends BaseEntity> items, final DataLoadListener listener) {
        try {

            new DataLoader() {
                @Override
                public Object doDBCall() {
                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    db.beginTransaction();
                    db.delete(tableName, null, null);
                    for (BaseEntity q : items) {
                        db.insert(tableName, null, q.getDbContentValues());
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    listener.onDataLoad(null);
                    return null;
                }
            }.executeAsync();
        } catch (Exception e) {
            AppUtils.throwException(BaseDAO.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }
}
