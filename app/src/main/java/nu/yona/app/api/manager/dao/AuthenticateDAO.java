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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import nu.yona.app.api.db.DBConstant;
import nu.yona.app.api.db.DbSerializer;
import nu.yona.app.api.db.JsonSerializer;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class AuthenticateDAO extends BaseDAO {

    private String USER_ID = "1"; // single user app and default id in db for that is 1.
    private DbSerializer serializer = new JsonSerializer();

    public AuthenticateDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        super(mOpenHelper, context);
    }

    public void updateDataForRegisterUser(Object result, DataLoadListener listener) {
        // do process for storing data in database.
        try {
            ContentValues values = new ContentValues();
            values.put(DBConstant.ID, USER_ID);
            values.put(DBConstant.SOURCE_OBJECT, serializer.serialize(result));
            // we will store only one user in database, so check if already user exist in db, just update.
            if (getUser() == null) {
                insert(DBConstant.TBL_USER_DATA, values);
            } else {
                update(DBConstant.TBL_USER_DATA, values, DBConstant.ID + " = ?", new String[]{USER_ID});
            }
            listener.onDataLoad(result);
        } catch (Exception e) {
            listener.onError(e.getMessage() != null ? e.getMessage() : e.getLocalizedMessage());
        }
    }

    public User getUser() {
        Cursor c = query(DBConstant.TBL_USER_DATA, null, null, null, null, null);
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    return serializer.deserialize(c.getBlob(c.getColumnIndex(DBConstant.SOURCE_OBJECT)), User.class);
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
