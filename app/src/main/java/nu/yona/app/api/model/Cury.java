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
 * The type Cury.
 */
public class Cury extends BaseEntity {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("templated")
    @Expose
    private Boolean templated;

    /**
     * Gets name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets href.
     *
     * @return The href
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets href.
     *
     * @param href The href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Gets templated.
     *
     * @return The templated
     */
    public Boolean getTemplated() {
        return templated;
    }

    /**
     * Sets templated.
     *
     * @param templated The templated
     */
    public void setTemplated(Boolean templated) {
        this.templated = templated;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
