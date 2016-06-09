/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class DateUtility {


    /**
     * Gets relative date.
     *
     * @param future the future
     * @return the relative date
     */
    public static String getRelativeDate(Calendar future) {

        String relativeDate = "";

        long days = getDateDiff(future.getTime(), Calendar.getInstance().getTime(), TimeUnit.DAYS);

        if (days == 0) {
            relativeDate = YonaApplication.getAppContext().getString(R.string.today);
        } else if (days < 2) {
            relativeDate = YonaApplication.getAppContext().getString(R.string.yesterday);
        } else {
            try {
                Date date = new Date(future.getTimeInMillis());
                Calendar futureCalendar = Calendar.getInstance();
                futureCalendar.setTime(date);
                relativeDate = new SimpleDateFormat("EEEE, d MMM").format(future.getTime());

            } catch (Exception e) {
                Log.e(DateUtility.class.getName(), "Date Format exception: " + e);
            }
        }

        return relativeDate;
    }

    /**
     * Gets date diff.
     *
     * @param date1    the date 1
     * @param date2    the date 2
     * @param timeUnit the time unit
     * @return the date diff
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets long format date.
     *
     * @param date the date
     * @return the long format date
     */
    public static String getLongFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_LONG_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

}
