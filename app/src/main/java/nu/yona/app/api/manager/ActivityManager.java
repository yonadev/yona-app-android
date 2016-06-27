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

import nu.yona.app.api.model.Href;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public interface ActivityManager {

    /**
     * Gets days activity.
     *
     * @param loadMore     the load more
     * @param isBuddlyFlow the is buddly flow
     * @param url          the url
     * @param listener     the listener
     */
    void getDaysActivity(boolean loadMore, boolean isBuddlyFlow, Href url, DataLoadListener listener);

    /**
     * Gets day detail activity.
     *
     * @param url      the url
     * @param listener the listener
     */
    void getDayDetailActivity(String url, DataLoadListener listener);

    /**
     * Gets weeks activity.
     *
     * @param loadMore     the load more
     * @param isBuddlyFlow the is buddly flow
     * @param href         the href
     * @param listener     the listener
     */
    void getWeeksActivity(boolean loadMore, boolean isBuddlyFlow, Href href, DataLoadListener listener);

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

    /**
     * Gets with buddy activity.
     *
     * @param loadMore the load more
     * @param listener the listener
     */
    void getWithBuddyActivity(boolean loadMore, final DataLoadListener listener);
}
