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

/**
 * The type Yona goal.
 */
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
    @SerializedName("spreadCells")
    @Expose
    private List<Integer> spreadCells = new ArrayList<Integer>();
    @SerializedName("historyItem")
    @Expose
    private boolean historyItem;

    /**
     * Gets links.
     *
     * @return The Links
     */
    public Links getLinks() {
        return Links;
    }

    /**
     * Sets links.
     *
     * @param Links The _links
     */
    public void setLinks(nu.yona.app.api.model.Links Links) {
        this.Links = Links;
    }

    /**
     * Gets type.
     *
     * @return The Type
     */
    public String getType() {
        return Type;
    }

    /**
     * Sets type.
     *
     * @param Type The @type
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * Gets activity category name.
     *
     * @return The activityCategoryName
     */
    public String getActivityCategoryName() {
        return activityCategoryName;
    }

    /**
     * Sets activity category name.
     *
     * @param activityCategoryName The activityCategoryName
     */
    public void setActivityCategoryName(String activityCategoryName) {
        this.activityCategoryName = activityCategoryName;
    }

    /**
     * Gets max duration minutes.
     *
     * @return the max duration minutes
     */
    public long getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    /**
     * Sets max duration minutes.
     *
     * @param maxDurationMinutes the max duration minutes
     */
    public void setMaxDurationMinutes(long maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    /**
     * Gets zones.
     *
     * @return the zones
     */
    public List<String> getZones() {
        return zones;
    }

    /**
     * Sets zones.
     *
     * @param zones the zones
     */
    public void setZones(List<String> zones) {
        this.zones = zones;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

    /**
     * Gets spread cells.
     *
     * @return the spread cells
     */
    public List<Integer> getSpreadCells() {
        return this.spreadCells;
    }

    /**
     * Sets spread cells.
     *
     * @param spreadCells the spread cells
     */
    public void setSpreadCells(List<Integer> spreadCells) {
        this.spreadCells = spreadCells;
    }

    /**
     * Is history item boolean.
     *
     * @return the boolean
     */
    public boolean isHistoryItem() {
        return this.historyItem;
    }

    /**
     * Sets history item.
     *
     * @param historyItem the history item
     */
    public void setHistoryItem(boolean historyItem) {
        this.historyItem = historyItem;
    }
}
