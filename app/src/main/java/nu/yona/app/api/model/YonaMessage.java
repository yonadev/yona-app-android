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

import nu.yona.app.enums.NotificationMessageEnum;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public class YonaMessage extends BaseEntity {
    @SerializedName("creationTime")
    @Expose
    private String creationTime;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("change")
    @Expose
    private String change;
    @SerializedName("reason")
    @Expose
    private String dropBuddyReason;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("_links")
    @Expose
    private Links Links;
    @SerializedName("_embedded")
    @Expose
    private Embedded Embedded;
    @SerializedName("@type")
    @Expose
    private String Type;
    @SerializedName("@notificationMessageEnum")
    @Expose
    private NotificationMessageEnum notificationMessageEnum;
    @SerializedName("@stickyTitle")
    @Expose
    private String stickyTitle;
    @SerializedName("@Buddy")
    @Expose
    private YonaBuddy yonaBuddy;
    @SerializedName("isRead")
    @Expose
    private boolean isRead;
    @SerializedName("threadHeadMessageID")
    @Expose
    private String threadMessageId;

    /**
     * Gets creation time.
     *
     * @return The creationTime
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Sets creation time.
     *
     * @param creationTime The creationTime
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Gets nickname.
     *
     * @return The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets nickname.
     *
     * @param nickname The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets message.
     *
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets status.
     *
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets links.
     *
     * @return The Links
     */
    public nu.yona.app.api.model.Links getLinks() {
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
     * Gets embedded.
     *
     * @return The Embedded
     */
    public Embedded getEmbedded() {
        return Embedded;
    }

    /**
     * Sets embedded.
     *
     * @param Embedded The _embedded
     */
    public void setEmbedded(Embedded Embedded) {
        this.Embedded = Embedded;
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
     * Get Notification enum
     *
     * @return notification message enum
     */
    public NotificationMessageEnum getNotificationMessageEnum() {
        return notificationMessageEnum;
    }

    /**
     * Set Notification Enum
     *
     * @param notificationMessageEnum the notification message enum
     */
    public void setNotificationMessageEnum(NotificationMessageEnum notificationMessageEnum) {
        this.notificationMessageEnum = notificationMessageEnum;
    }

    /**
     * Get Sticky Title
     *
     * @return sticky title
     */
    public String getStickyTitle() {
        return stickyTitle;
    }

    /**
     * Set Sticky Title
     *
     * @param stickyTitle the sticky title
     */
    public void setStickyTitle(String stickyTitle) {
        this.stickyTitle = stickyTitle;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

    public String getChange() {
        return this.change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getDropBuddyReason() {
        return this.dropBuddyReason;
    }

    public void setDropBuddyReason(String dropBuddyReason) {
        this.dropBuddyReason = dropBuddyReason;
    }

    public YonaBuddy getYonaBuddy() {
        return this.yonaBuddy;
    }

    public void setYonaBuddy(YonaBuddy yonaBuddy) {
        this.yonaBuddy = yonaBuddy;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public String getThreadMessageId() {
        return threadMessageId;
    }

    public void setThreadMessageId(String threadMessageId) {
        this.threadMessageId = threadMessageId;
    }
}
