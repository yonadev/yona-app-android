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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kinnarvasa on 08/06/16.
 */
public class AppActivity {
    @SerializedName("deviceDateTime")
    @Expose
    private String deviceDateTime;
    @SerializedName("activities")
    @Expose
    private List<Activity> activities = new ArrayList<Activity>();

    /**
     * Gets device date time.
     *
     * @return The deviceDateTime
     */
    public String getDeviceDateTime() {
        return deviceDateTime;
    }

    /**
     * Sets device date time.
     *
     * @param deviceDateTime The deviceDateTime
     */
    public void setDeviceDateTime(String deviceDateTime) {
        this.deviceDateTime = deviceDateTime;
    }

    /**
     * Gets activities.
     *
     * @return The activities
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Sets activities.
     *
     * @param activities The activities
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
