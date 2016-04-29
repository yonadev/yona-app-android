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
 * Created by kinnarvasa on 28/04/16.
 */
public enum FriendStatusEnum {

    NOT_REQUESTED("NOT_REQUESTED"), REQUESTED("REQUESTED"), ACCEPTED("ACCEPTED"), REJECTED("REJECTED");

    final String status;

    FriendStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
