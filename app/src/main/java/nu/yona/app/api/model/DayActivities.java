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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class DayActivities {
    @SerializedName("SUNDAY")
    @Expose
    private Day sunday;
    @SerializedName("MONDAY")
    @Expose
    private Day monday;
    @SerializedName("TUESDAY")
    @Expose
    private Day tuesday;
    @SerializedName("WEDNESDAY")
    @Expose
    private Day wednesday;
    @SerializedName("THURSDAY")
    @Expose
    private Day thursday;
    @SerializedName("FRIDAY")
    @Expose
    private Day friday;
    @SerializedName("SATURDAY")
    @Expose
    private Day saturday;

    /**
     * Gets sunday.
     *
     * @return The sunday
     */
    public Day getSUNDAY() {
        return sunday;
    }

    /**
     * Sets sunday.
     *
     * @param sUNDAY The SUNDAY
     */
    public void setSUNDAY(Day sUNDAY) {
        this.sunday = sUNDAY;
    }

    /**
     * Gets monday.
     *
     * @return The monday
     */
    public Day getMONDAY() {
        return monday;
    }

    /**
     * Sets monday.
     *
     * @param mONDAY The MONDAY
     */
    public void setMONDAY(Day mONDAY) {
        this.monday = mONDAY;
    }

    /**
     * Gets tuesday.
     *
     * @return The tuesday
     */
    public Day getTUESDAY() {
        return tuesday;
    }

    /**
     * Sets tuesday.
     *
     * @param tUESDAY The TUESDAY
     */
    public void setTUESDAY(Day tUESDAY) {
        this.tuesday = tUESDAY;
    }

    /**
     * Gets wednesday.
     *
     * @return The wednesday
     */
    public Day getWEDNESDAY() {
        return wednesday;
    }

    /**
     * Sets wednesday.
     *
     * @param wEDNESDAY The WEDNESDAY
     */
    public void setWEDNESDAY(Day wEDNESDAY) {
        this.wednesday = wEDNESDAY;
    }

    /**
     * Gets thursday.
     *
     * @return The thursday
     */
    public Day getTHURSDAY() {
        return thursday;
    }

    /**
     * Sets thursday.
     *
     * @param tHURSDAY The THURSDAY
     */
    public void setTHURSDAY(Day tHURSDAY) {
        this.thursday = tHURSDAY;
    }

    /**
     * Gets friday.
     *
     * @return The friday
     */
    public Day getFRIDAY() {
        return friday;
    }

    /**
     * Sets friday.
     *
     * @param fRIDAY The FRIDAY
     */
    public void setFRIDAY(Day fRIDAY) {
        this.friday = fRIDAY;
    }

    /**
     * Gets saturday.
     *
     * @return The saturday
     */
    public Day getSATURDAY() {
        return saturday;
    }

    /**
     * Sets saturday.
     *
     * @param sATURDAY The SATURDAY
     */
    public void setSATURDAY(Day sATURDAY) {
        this.saturday = sATURDAY;
    }

}
