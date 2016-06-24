/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.enums.ChartTypeEnum;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class WeekActivity extends BaseEntity {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("timeZoneId")
    @Expose
    private String timeZoneId;
    @SerializedName("spread")
    @Expose
    private List<Integer> spread = new ArrayList<Integer>();
    @SerializedName("totalActivityDurationMinutes")
    @Expose
    private Integer totalActivityDurationMinutes;
    @SerializedName("dayActivities")
    @Expose
    private DayActivities dayActivities;
    @SerializedName("_links")
    @Expose
    private Links links;
    @SerializedName("yonaGoal")
    @Expose
    private YonaGoal yonaGoal;
    @SerializedName("chartTypeEnum")
    @Expose
    private ChartTypeEnum chartTypeEnum;
    @SerializedName("@stickyTitle")
    @Expose
    private String stickyTitle;
    @SerializedName("@stickyHeaderId")
    @Expose
    private int stickyHeaderId;
    @SerializedName("@TimeZoneSpread")
    @Expose
    private List<TimeZoneSpread> timeZoneSpread;

    /**
     * Gets date.
     *
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets time zone id.
     *
     * @return The timeZoneId
     */
    public String getTimeZoneId() {
        return timeZoneId;
    }

    /**
     * Sets time zone id.
     *
     * @param timeZoneId The timeZoneId
     */
    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    /**
     * Gets spread.
     *
     * @return The spread
     */
    public List<Integer> getSpread() {
        return spread;
    }

    /**
     * Sets spread.
     *
     * @param spread The spread
     */
    public void setSpread(List<Integer> spread) {
        this.spread = spread;
    }

    /**
     * Gets total activity duration minutes.
     *
     * @return The totalActivityDurationMinutes
     */
    public Integer getTotalActivityDurationMinutes() {
        return totalActivityDurationMinutes;
    }

    /**
     * Sets total activity duration minutes.
     *
     * @param totalActivityDurationMinutes The totalActivityDurationMinutes
     */
    public void setTotalActivityDurationMinutes(Integer totalActivityDurationMinutes) {
        this.totalActivityDurationMinutes = totalActivityDurationMinutes;
    }

    /**
     * Gets day activities.
     *
     * @return The dayActivities
     */
    public DayActivities getDayActivities() {
        return dayActivities;
    }

    /**
     * Sets day activities.
     *
     * @param dayActivities The dayActivities
     */
    public void setDayActivities(DayActivities dayActivities) {
        this.dayActivities = dayActivities;
    }

    /**
     * Gets links.
     *
     * @return The links
     */
    public Links getLinks() {
        return links;
    }

    /**
     * Sets links.
     *
     * @param links The _links
     */
    public void setLinks(Links links) {
        this.links = links;
    }

    /**
     * Gets yona goal.
     *
     * @return the yona goal
     */
    public YonaGoal getYonaGoal() {
        return this.yonaGoal;
    }

    /**
     * Sets yona goal.
     *
     * @param yonaGoal the yona goal
     */
    public void setYonaGoal(YonaGoal yonaGoal) {
        this.yonaGoal = yonaGoal;
    }

    /**
     * Gets chart type enum.
     *
     * @return the chart type enum
     */
    public ChartTypeEnum getChartTypeEnum() {
        return this.chartTypeEnum;
    }

    /**
     * Sets chart type enum.
     *
     * @param chartTypeEnum the chart type enum
     */
    public void setChartTypeEnum(ChartTypeEnum chartTypeEnum) {
        this.chartTypeEnum = chartTypeEnum;
    }


    /**
     * Get Sticky Title
     *
     * @return sticky title
     */
    public String getStickyTitle() {
        return stickyTitle;
    }

    /**
     * Set Sticky Title
     *
     * @param stickyTitle the sticky title
     */
    public void setStickyTitle(String stickyTitle) {
        this.stickyTitle = stickyTitle;
    }

    /**
     * Gets sticky header id.
     *
     * @return the sticky header id
     */
    public int getStickyHeaderId() {
        return this.stickyHeaderId;
    }

    /**
     * Sets sticky header id.
     *
     * @param stickyHeaderId the sticky header id
     */
    public void setStickyHeaderId(int stickyHeaderId) {
        this.stickyHeaderId = stickyHeaderId;
    }

    /**
     * Gets time zone spread.
     *
     * @return the time zone spread
     */
    public List<TimeZoneSpread> getTimeZoneSpread() {
        return this.timeZoneSpread;
    }

    /**
     * Sets time zone spread.
     *
     * @param timeZoneSpread the time zone spread
     */
    public void setTimeZoneSpread(List<TimeZoneSpread> timeZoneSpread) {
        this.timeZoneSpread = timeZoneSpread;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
