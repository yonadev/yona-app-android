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
public interface DBConstant {
    /**
     * The constant DATABASE_NAME.
     */
    String DATABASE_NAME = "yonaDB";
    /**
     * The constant DATABASE_VERSION.
     */
    int DATABASE_VERSION = 1;
    /**
     * The constant NO_DATA_ERROR.
     */
    String NO_DATA_ERROR = "No Data";
    /**
     * DB TABLES
     */
    String TBL_USER_DATA = "userData";
    /**
     * The constant TBL_ACTIVITY_CATEGORIES.
     */
    String TBL_ACTIVITY_CATEGORIES = "activityCategories";
    /**
     * The constant TBL_GOAL.
     */
    String TBL_GOAL = "goal";

    /**
     * The constant TBL_ACTIVITY_TRACKER.
     */
    String TBL_ACTIVITY_TRACKER = "activityTracker";
    /** DB TABLES **/

    /**
     * DB Fields
     */
    String ID = "Id";
    /**
     * The constant SOURCE_OBJECT.
     */
    String SOURCE_OBJECT = "sourceObject";
    /**
     * DB Fields
     */

    String APPLICATION_NAME = "applicationName";

    /**
     * The constant APPLICATION_START_TIME.
     */
    String APPLICATION_START_TIME = "applicationStartTime";

    /**
     * The constant APPLICATION_END_TIME.
     */
    String APPLICATION_END_TIME = "applicationEndTime";
}
