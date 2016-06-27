/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import nu.yona.app.enums.WeekDayEnum;

/**
 * Created by bhargavsuthar on 27/06/16.
 */
public class WeekDayActivity extends BaseEntity {

    private WeekDayEnum weekDayEnum;

    private String day;

    private String date;

    private int color;


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WeekDayEnum getWeekDayEnum() {
        return weekDayEnum;
    }

    public void setWeekDayEnum(WeekDayEnum weekDayEnum) {
        this.weekDayEnum = weekDayEnum;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
