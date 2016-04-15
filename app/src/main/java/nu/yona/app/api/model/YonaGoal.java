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

public class YonaGoal extends BaseEntity {

    @SerializedName("_links")
    @Expose
    private Links Links;
    @SerializedName("@type")
    @Expose
    private String Type;
    @SerializedName("activityCategoryName")
    @Expose
    private String activityCategoryName;
    @SerializedName("maxDurationMinutes")
    @Expose
    private long maxDurationMinutes;
    @SerializedName("zones")
    @Expose
    private List<String> zones = new ArrayList<>();

    /**
     * @return The Links
     */
    public Links getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(nu.yona.app.api.model.Links Links) {
        this.Links = Links;
    }

    /**
     * @return The Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type The @type
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @return The activityCategoryName
     */
    public String getActivityCategoryName() {
        return activityCategoryName;
    }

    /**
     * @param activityCategoryName The activityCategoryName
     */
    public void setActivityCategoryName(String activityCategoryName) {
        this.activityCategoryName = activityCategoryName;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
