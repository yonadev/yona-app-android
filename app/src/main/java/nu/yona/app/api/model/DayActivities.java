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
     * @return The sUNDAY
     */
    public Day getSUNDAY() {
        return sUNDAY;
    }

    /**
     * @param sUNDAY The SUNDAY
     */
    public void setSUNDAY(Day sUNDAY) {
        this.sUNDAY = sUNDAY;
    }

    /**
     * @return The mONDAY
     */
    public Day getMONDAY() {
        return mONDAY;
    }

    /**
     * @param mONDAY The MONDAY
     */
    public void setMONDAY(Day mONDAY) {
        this.mONDAY = mONDAY;
    }

    /**
     * @return The tUESDAY
     */
    public Day getTUESDAY() {
        return tUESDAY;
    }

    /**
     * @param tUESDAY The TUESDAY
     */
    public void setTUESDAY(Day tUESDAY) {
        this.tUESDAY = tUESDAY;
    }

    /**
     * @return The wEDNESDAY
     */
    public Day getWEDNESDAY() {
        return wEDNESDAY;
    }

    /**
     * @param wEDNESDAY The WEDNESDAY
     */
    public void setWEDNESDAY(Day wEDNESDAY) {
        this.wEDNESDAY = wEDNESDAY;
    }

    /**
     * @return The tHURSDAY
     */
    public Day getTHURSDAY() {
        return tHURSDAY;
    }

    /**
     * @param tHURSDAY The THURSDAY
     */
    public void setTHURSDAY(Day tHURSDAY) {
        this.tHURSDAY = tHURSDAY;
    }

    /**
     * @return The fRIDAY
     */
    public Day getFRIDAY() {
        return fRIDAY;
    }

    /**
     * @param fRIDAY The FRIDAY
     */
    public void setFRIDAY(Day fRIDAY) {
        this.fRIDAY = fRIDAY;
    }

    /**
     * @return The sATURDAY
     */
    public Day getSATURDAY() {
        return sATURDAY;
    }

    /**
     * @param sATURDAY The SATURDAY
     */
    public void setSATURDAY(Day sATURDAY) {
        this.sATURDAY = sATURDAY;
    }

}
