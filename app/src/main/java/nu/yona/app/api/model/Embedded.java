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

public class Embedded extends BaseEntity {

    @SerializedName("yona:goals")
    @Expose
    private YonaGoals yonaGoals;
    @SerializedName("yona:buddies")
    @Expose
    private YonaBuddies yonaBuddies;

    /**
     * @return The yonaGoals
     */
    public YonaGoals getYonaGoals() {
        return yonaGoals;
    }

    /**
     * @param yonaGoals The yona:goals
     */
    public void setYonaGoals(YonaGoals yonaGoals) {
        this.yonaGoals = yonaGoals;
    }

    /**
     * @return The yonaBuddies
     */
    public YonaBuddies getYonaBuddies() {
        return yonaBuddies;
    }

    /**
     * @param yonaBuddies The yona:buddies
     */
    public void setYonaBuddies(YonaBuddies yonaBuddies) {
        this.yonaBuddies = yonaBuddies;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
