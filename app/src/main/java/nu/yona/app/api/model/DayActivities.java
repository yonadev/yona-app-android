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
    private Day sUNDAY;
    @SerializedName("MONDAY")
    @Expose
    private Day mONDAY;
    @SerializedName("TUESDAY")
    @Expose
    private Day tUESDAY;
    @SerializedName("WEDNESDAY")
    @Expose
    private Day wEDNESDAY;
    @SerializedName("THURSDAY")
    @Expose
    private Day tHURSDAY;
    @SerializedName("FRIDAY")
    @Expose
    private Day fRIDAY;
    @SerializedName("SATURDAY")
    @Expose
    private Day sATURDAY;

    /**
     * Gets sunday.
     *
     * @return The sUNDAY
     */
    public Day getSUNDAY() {
        return sUNDAY;
    }

    /**
     * Sets sunday.
     *
     * @param sUNDAY The SUNDAY
     */
    public void setSUNDAY(Day sUNDAY) {
        this.sUNDAY = sUNDAY;
    }

    /**
     * Gets monday.
     *
     * @return The mONDAY
     */
    public Day getMONDAY() {
        return mONDAY;
    }

    /**
     * Sets monday.
     *
     * @param mONDAY The MONDAY
     */
    public void setMONDAY(Day mONDAY) {
        this.mONDAY = mONDAY;
    }

    /**
     * Gets tuesday.
     *
     * @return The tUESDAY
     */
    public Day getTUESDAY() {
        return tUESDAY;
    }

    /**
     * Sets tuesday.
     *
     * @param tUESDAY The TUESDAY
     */
    public void setTUESDAY(Day tUESDAY) {
        this.tUESDAY = tUESDAY;
    }

    /**
     * Gets wednesday.
     *
     * @return The wEDNESDAY
     */
    public Day getWEDNESDAY() {
        return wEDNESDAY;
    }

    /**
     * Sets wednesday.
     *
     * @param wEDNESDAY The WEDNESDAY
     */
    public void setWEDNESDAY(Day wEDNESDAY) {
        this.wEDNESDAY = wEDNESDAY;
    }

    /**
     * Gets thursday.
     *
     * @return The tHURSDAY
     */
    public Day getTHURSDAY() {
        return tHURSDAY;
    }

    /**
     * Sets thursday.
     *
     * @param tHURSDAY The THURSDAY
     */
    public void setTHURSDAY(Day tHURSDAY) {
        this.tHURSDAY = tHURSDAY;
    }

    /**
     * Gets friday.
     *
     * @return The fRIDAY
     */
    public Day getFRIDAY() {
        return fRIDAY;
    }

    /**
     * Sets friday.
     *
     * @param fRIDAY The FRIDAY
     */
    public void setFRIDAY(Day fRIDAY) {
        this.fRIDAY = fRIDAY;
    }

    /**
     * Gets saturday.
     *
     * @return The sATURDAY
     */
    public Day getSATURDAY() {
        return sATURDAY;
    }

    /**
     * Sets saturday.
     *
     * @param sATURDAY The SATURDAY
     */
    public void setSATURDAY(Day sATURDAY) {
        this.sATURDAY = sATURDAY;
    }

}
