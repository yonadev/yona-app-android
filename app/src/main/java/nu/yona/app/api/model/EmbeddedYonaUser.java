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

/**
 * The type Embedded yona user.
 */
public class EmbeddedYonaUser extends BaseEntity {

    @SerializedName("yona:user")
    @Expose
    private YonaUser yonaUser;

    /**
     * Gets yona user.
     *
     * @return The yonaUser
     */
    public YonaUser getYonaUser() {
        return yonaUser;
    }

    /**
     * Sets yona user.
     *
     * @param yonaUser The yona:user
     */
    public void setYonaUser(YonaUser yonaUser) {
        this.yonaUser = yonaUser;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
