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

/**
 * Author @MobiquityInc
 */
public class BaseDAO {

    protected final SQLiteOpenHelper mOpenHelper;
    protected DbSerializer serializer = new JsonSerializer();

    public BaseDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        this.mOpenHelper = mOpenHelper;
    }

    protected void delete(String tableName, String where, String[] whereArgs) {
        if (mOpenHelper != null) {
            mOpenHelper.getWritableDatabase().delete(tableName, where, whereArgs);
        }
    }

    protected long insert(String tableName, ContentValues initialValues) {
        if (mOpenHelper != null) {
            return mOpenHelper.getWritableDatabase().insertOrThrow(tableName, null, initialValues);
        }
        return 0;
    }

    protected void update(String tableName, ContentValues values, String where, String... whereArgs) {
        if (mOpenHelper != null) {
            mOpenHelper.getWritableDatabase().update(tableName, values, where, whereArgs);
        }
    }

    protected Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        if (mOpenHelper == null) {
            return null;
        }
        return mOpenHelper.getWritableDatabase().query(tableName, projection, selection, selectionArgs, groupBy, null, sortOrder);
    }

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
            listener.onError(e);
        }
    }
}
