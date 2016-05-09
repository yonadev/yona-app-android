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
 * The type Embedded.
 */
public class Embedded extends BaseEntity {

    @SerializedName("yona:goals")
    @Expose
    private YonaGoals yonaGoals;
    @SerializedName("yona:buddies")
    @Expose
    private YonaBuddies yonaBuddies;
    @SerializedName("yona:user")
    @Expose
    private RegisterUser yonaUser;
    @SerializedName("yona:messages")
    @Expose
    private List<YonaMessage> yonaMessages = new ArrayList<>();
    @SerializedName("sendingStatus")
    @Expose
    private String sendingStatus;
    @SerializedName("receivingStatus")
    @Expose
    private String receivingStatus;
    @SerializedName("_links")
    @Expose
    private Links Links;

    /**
     * Gets yona goals.
     *
     * @return The yonaGoals
     */
    public YonaGoals getYonaGoals() {
        return yonaGoals;
    }

    /**
     * Sets yona goals.
     *
     * @param yonaGoals The yona:goals
     */
    public void setYonaGoals(YonaGoals yonaGoals) {
        this.yonaGoals = yonaGoals;
    }

    /**
     * Gets yona buddies.
     *
     * @return The yonaBuddies
     */
    public YonaBuddies getYonaBuddies() {
        return yonaBuddies;
    }

    /**
     * Sets yona buddies.
     *
     * @param yonaBuddies The yona:buddies
     */
    public void setYonaBuddies(YonaBuddies yonaBuddies) {
        this.yonaBuddies = yonaBuddies;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

    /**
     * Gets yona user.
     *
     * @return The RegisterUser
     */
    public RegisterUser getYonaUser() {
        return yonaUser;
    }

    /**
     * Sets yona user.
     *
     * @param yonaUser The yona:user
     */
    public void setYonaUser(RegisterUser yonaUser) {
        this.yonaUser = yonaUser;
    }

    /**
     * Gets sending status.
     *
     * @return The sendingStatus
     */
    public String getSendingStatus() {
        return sendingStatus;
    }

    /**
     * Sets sending status.
     *
     * @param sendingStatus The sendingStatus
     */
    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    /**
     * Gets receiving status.
     *
     * @return The receivingStatus
     */
    public String getReceivingStatus() {
        return receivingStatus;
    }

    /**
     * Sets receiving status.
     *
     * @param receivingStatus The receivingStatus
     */
    public void setReceivingStatus(String receivingStatus) {
        this.receivingStatus = receivingStatus;
    }

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
    public void setLinks(Links Links) {
        this.Links = Links;
    }

    /**
     *
     * @return
     * The yonaMessages
     */
    public List<YonaMessage> getYonaMessages() {
        return yonaMessages;
    }

    /**
     *
     * @param yonaMessages
     * The yona:messages
     */
    public void setYonaMessages(List<YonaMessage> yonaMessages) {
        this.yonaMessages = yonaMessages;
    }

}
