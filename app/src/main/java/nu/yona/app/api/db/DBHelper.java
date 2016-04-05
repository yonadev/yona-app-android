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

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class DBHelper {
    private final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private final String FIELD_INTEGER = " INTEGER,";
    private final String FIELD_INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY,";
    private final String FIELD_INTEGER_WITHOUT_COMMA = " INTEGER";
    private final String FIELD_NUMERIC = " NUMERIC,";
    private final String FIELD_REAL = " REAL,";
    private final String FIELD_REAL_WITHOUT_COMMA = " REAL";
    private final String FIELD_TEXT = " TEXT,";
    private final String FIELD_TEXT_WITHOUT_COMMA = " TEXT";
    private final String FIELD_BLOB = " BLOB,";
    private final String FIELD_BOOLEAN = " BOOLEAN,";
    private final String FIELD_BOOLEAN_WITHOUT_COMMA = " BOOLEAN";
    private final String FIELD_BLOB_WITHOUT_COMMA = " BLOB";
    public final String TABLE_USER_REGISTER = CREATE_TABLE_IF_NOT_EXISTS + DBConstant.TBL_USER_DATA
            + "("
            + DBConstant.ID + FIELD_TEXT
            + DBConstant.SOURCE_OBJECT + FIELD_BLOB_WITHOUT_COMMA
            + ")";
    private final String NUMERIC_PRIMARY_KEY = " NUMERIC PRIMARY KEY, ";
    private final String UNIQUE = " UNIQUE";
    private final String NOT_NULL = " NOT NULL";
}
