/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import nu.yona.app.api.db.DBConstant;
import nu.yona.app.api.model.Goals;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

/**
 * Created by bhargavsuthar on 15/04/16.
 */
public class GoalDAO extends BaseDAO {
    private final String ID = "1";

    public GoalDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        super(mOpenHelper, context);
    }

    /**
     * Insert or Update the User Goals into Database
     *
     * @param goals
     * @param listener
     */
    public void saveGoalData(Goals goals, DataLoadListener listener) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBConstant.ID, ID);
            values.put(DBConstant.SOURCE_OBJECT, serializer.serialize(goals));
            if (getUserGoal() == null) {
                insert(DBConstant.TBL_GOAL, values);
            } else {
                update(DBConstant.TBL_GOAL, values, DBConstant.ID + " = ?", new String[]{ID});
            }
            if (listener != null) {
                listener.onDataLoad(goals);
            }
        } catch (Exception e) {
            AppUtils.throwException(GoalDAO.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public Goals getUserGoal() {
        Cursor c = query(DBConstant.TBL_GOAL, null, null, null, null, null);
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    return serializer.deserialize(c.getBlob(c.getColumnIndex(DBConstant.SOURCE_OBJECT)), Goals.class);
                }
            }
        } catch (Exception e) {
            AppUtils.throwException(GoalDAO.class.getSimpleName(), e, Thread.currentThread(), null);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }
}
