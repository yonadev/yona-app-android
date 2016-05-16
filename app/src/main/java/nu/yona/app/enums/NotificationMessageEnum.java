/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.enums;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;

/**
 * Created by kinnarvasa on 12/05/16.
 */
public enum NotificationMessageEnum {

    /**
     * Buddy connect request accepted notification message enum.
     */
    BUDDY_CONNECT_REQUEST_ACCEPTED(NotificationEnum.BUDDYCONNECTREQUESTMESSAGE, StatusEnum.ACCEPTED, getString(R.string.buddyconnectrequested), 0),
    /**
     * Buddy connect request rejected notification message enum.
     */
    BUDDY_CONNECT_REQUEST_REJECTED(NotificationEnum.BUDDYCONNECTREQUESTMESSAGE, StatusEnum.REJECTED, getString(R.string.buddyconnectrequested), 0),
    /**
     * Buddy connect request requested notification message enum.
     */
    BUDDY_CONNECT_REQUEST_REQUESTED(NotificationEnum.BUDDYCONNECTREQUESTMESSAGE, StatusEnum.REQUESTED, getString(R.string.buddyconnectrequested), 0),
    /**
     * Buddy connect request not requested notification message enum.
     */
    BUDDY_CONNECT_REQUEST_NOT_REQUESTED(NotificationEnum.BUDDYCONNECTREQUESTMESSAGE, StatusEnum.NOT_REQUESTED, "BUDDY_CONNECT_REQUEST_NOT_REQUESTED", 0),
    /**
     * None none notification message enum.
     */
    NONE_NONE(NotificationEnum.NONE, StatusEnum.NONE, "NONE_NONE", 0);

    /**
     * The Notification enum.
     */
    NotificationEnum notificationEnum;
    /**
     * The Status enum.
     */
    StatusEnum statusEnum;
    /**
     * The User message.
     */
    String userMessage;
    /**
     * The Image id.
     */
    long imageId;

    NotificationMessageEnum(NotificationEnum notificationEnum, StatusEnum statusEnum, String userMessage, long imageId) {
        this.notificationEnum = notificationEnum;
        this.statusEnum = statusEnum;
        this.userMessage = userMessage;
        this.imageId = imageId;
    }

    /**
     * Gets string.
     *
     * @param id the id
     * @return the string
     */
    static String getString(int id) {
        return YonaApplication.getAppContext().getString(id);
    }

    /**
     * Gets user message.
     *
     * @return the user message
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Gets notification message enum.
     *
     * @param notificationType the notification type
     * @param statusType       the status type
     * @return the notification message enum
     */
    public static NotificationMessageEnum getNotificationMessageEnum(String notificationType, String statusType) {
        return getNotificationMessageEnum(NotificationEnum.getNotificationEnum(notificationType), StatusEnum.getStatusEnum(statusType));
    }

    /**
     * Gets notification message enum.
     *
     * @param notificationEnum the notification enum
     * @param statusEnum       the status enum
     * @return the notification message enum
     */
    public static NotificationMessageEnum getNotificationMessageEnum(NotificationEnum notificationEnum, StatusEnum statusEnum) {
        for (NotificationMessageEnum v : values()) {
            if (v.getNotificationEnum() == notificationEnum && v.getStatusEnum() == statusEnum) {
                return v;
            }
        }
        return NONE_NONE;
    }

    /**
     * Gets notification enum.
     *
     * @return the notification enum
     */
    public NotificationEnum getNotificationEnum() {
        return notificationEnum;
    }

    /**
     * Gets image id.
     *
     * @return the image id
     */
    public long getImageId() {
        return imageId;
    }

    /**
     * Gets status enum.
     *
     * @return the status enum
     */
    public StatusEnum getStatusEnum() {
        return statusEnum;
    }
}
