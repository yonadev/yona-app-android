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
import java.util.List;

import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public interface ActivityManager
{


	void getDetailOfEachSpreadWithDayActivity(final DayActivity dayActivity, final DataLoadListener listener);

	void getDetailOfEachWeekSpreadWithWeekActivity(final WeekActivity weekActivity, final DataLoadListener listener);

	/**
	 * Gets days activity.
	 *
	 * @param loadMore    the load more
	 * @param isBuddyFlow the is buddy flow
	 * @param url         the url
	 * @param listener    the listener
	 */
	void getDaysActivity(boolean loadMore, boolean isBuddyFlow, Href url, DataLoadListener listener);

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
	 * @param loadMore    the load more
	 * @param isBuddyFlow the is buddy flow
	 * @param href        the href
	 * @param listener    the listener
	 */
	void getWeeksActivity(boolean loadMore, boolean isBuddyFlow, Href href, DataLoadListener listener);

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

	void getComments(List<DayActivity> dayActivityList, int position, final DataLoadListener listener);

	void getCommentsForWeek(List<WeekActivity> weekActivityList, int position, final DataLoadListener listener);

	YonaBuddy findYonaBuddy(Href yonaBuddy);

	void addComment(String url, boolean isReplaying, String comment, final DataLoadListener listener);

	void addComment(WeekActivity dayActivity, String comment, final DataLoadListener listener);

	String getActivityCategoryName(String categoryPath);
}
