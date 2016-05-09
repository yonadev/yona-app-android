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

/**
 * Created by kinnarvasa on 09/05/16.
 */
public enum NotificationEnum {

    BUDDYCONNECTREQUESTMESSAGE("BuddyConnectRequestMessage"),
    BUDDYCONNECTRESPONSEMESSAGE("BuddyConnectResponseMessage"),
    BUDDYDISCONNECTMESSAGE("BuddyDisconnectMessage"),
    GOALCONFLICTMESSAGE("GoalConflictMessage"),
    GOALCHANGEMESSAGE("GoalChangeMessage"),
    DISCLOSUREREQUESTMESSAGE("DisclosureRequestMessage"),
    DISCLOSURERESPONSEMESSAGE("DisclosureResponseMessage");

    String notificationType;

    NotificationEnum(String type) {
        this.notificationType = type;
    }
}
