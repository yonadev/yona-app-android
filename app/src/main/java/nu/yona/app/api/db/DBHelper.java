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
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String FIELD_INTEGER = " INTEGER,";
    private static final String FIELD_INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY,";
    private static final String FIELD_INTEGER_WITHOUT_COMMA = " INTEGER";
    private static final String FIELD_NUMERIC = " NUMERIC,";
    private static final String FIELD_REAL = " REAL,";
    private static final String FIELD_REAL_WITHOUT_COMMA = " REAL";
    private static final String FIELD_TEXT = " TEXT,";
    private static final String FIELD_TEXT_WITHOUT_COMMA = " TEXT";
    private static final String FIELD_BLOB = " BLOB,";
    private static final String FIELD_BOOLEAN = " BOOLEAN,";
    private static final String FIELD_BOOLEAN_WITHOUT_COMMA = " BOOLEAN";
    private static final String FIELD_BLOB_WITHOUT_COMMA = " BLOB";
    private static final String NUMERIC_PRIMARY_KEY = " NUMERIC PRIMARY KEY, ";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOT_NULL = " NOT NULL";

    public static final String TABLE_USER_REGISTER = CREATE_TABLE_IF_NOT_EXISTS + DBConstant.USER_DATA
            + "("
            + DBConstant.ID + FIELD_TEXT
            + DBConstant.SOURCE_OBJECT + FIELD_BLOB
            + ")";
}
