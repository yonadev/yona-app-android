/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import java.util.Date;

import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public interface ActivityManager {
    /**
     * Gets days activity for logged in user
     *
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void getDaysActivity(int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Gets buddy days activity for friends/buddies activity
     *
     * @param url          the url
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void getBuddyDaysActivity(String url, int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Gets day detail activity details of logged in user /friends
     *
     * @param url      the url
     * @param listener the listener
     */
    void getDayDetailActivity(String url, DataLoadListener listener);

    /**
     * Gets weeks activity for logged in user
     *
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void getWeeksActivity(int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Gets buddy's weeks activity
     *
     * @param url          the url
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void getBuddyWeeksActivity(String url, int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Gets weeks detail activity.
     *
     * @param url      the url
     * @param listener the listener
     */
    void getWeeksDetailActivity(String url, DataLoadListener listener);

    /**
     * Post activity to db.
     *
     * @param applicationName the application name
     * @param startDate       the start date
     * @param endDate         the end date
     */
    void postActivityToDB(String applicationName, Date startDate, Date endDate);

    /**
     * Post all db activities.
     */
    void postAllDBActivities();
}
