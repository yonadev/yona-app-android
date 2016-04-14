/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.api.db.DBConstant;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 14/04/16.
 */
public class ActivityCategoriesDAO extends BaseDAO {

    public ActivityCategoriesDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        super(mOpenHelper, context);
    }

    /**
     * This method will clear all data from db and store new data in table.
     *
     * @param activityList List of activities received from server
     * @param listener
     */
    public void saveActivityCategories(final List<User> activityList, final DataLoadListener listener) {
        bulkInsert(DBConstant.TBL_ACTIVITY_CATEGORIES, activityList, listener);
    }

    public List<User> getActivityCategories() {
        Cursor c = query(DBConstant.TBL_ACTIVITY_CATEGORIES, null, null, null, null, null);
        try {
            if (c != null && c.getCount() > 0) {
                List<User> activityList = new ArrayList<User>();
                if (c.moveToFirst()) {
                    activityList.add(serializer.deserialize(c.getBlob(c.getColumnIndex(DBConstant.SOURCE_OBJECT)), User.class));
                }
            }
        } catch (Exception e) {
            Log.e(AuthenticateDAO.class.getSimpleName(), "get user error", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }
}
