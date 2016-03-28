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
import android.database.sqlite.SQLiteOpenHelper;

import nu.yona.app.api.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class SignupDAO extends BaseDAO{

    public SignupDAO(SQLiteOpenHelper mOpenHelper, Context context) {
        super(mOpenHelper, context);
    }

    public void updateDataForRegisterUser(Object result, DataLoadListener listener){
        // do process for storing data in database.
        try{

        } catch (Exception e){
            listener.onError(e.getMessage() != null ? e.getMessage() : e.getLocalizedMessage());
        }
    }
}
