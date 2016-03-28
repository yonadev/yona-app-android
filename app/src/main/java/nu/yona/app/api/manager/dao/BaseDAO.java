/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.dao;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import nu.yona.app.R;

/**
 * Author @MobiquityInc
 */
public class BaseDAO {

    public final SQLiteOpenHelper mOpenHelper;
    private final Context mContext;
    private String baseUrl;

    public String getBaseUrl(){
        return mContext.getString(R.string.server_url);
    }

    public BaseDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        this.mOpenHelper = mOpenHelper;
        this.mContext = context;
    }

    protected void delete(String tableName, String where, String[] whereArgs) {
        if (mOpenHelper != null) {
            mOpenHelper.getWritableDatabase().delete(tableName, where, whereArgs);
        }
    }

}
