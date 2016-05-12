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
public enum StatusEnum {

    /**
     * Not requested friend status enum.
     */
    NOT_REQUESTED("NOT_REQUESTED"), /**
     * Requested friend status enum.
     */
    REQUESTED("REQUESTED"), /**
     * Accepted friend status enum.
     */
    ACCEPTED("ACCEPTED"), /**
     * Rejected friend status enum.
     */
    REJECTED("REJECTED"),

    /**
     * None status enum.
     */
    NONE("NONE");
    /**
     * The Status.
     */
    final String status;

    StatusEnum(String status) {
        this.status = status;
    }

    /**
     * Gets status enum.
     *
     * @param status the status
     * @return the status enum
     */
    public static StatusEnum getStatusEnum(String status) {
        for (StatusEnum v : values()) {
            if (v.getStatus().equalsIgnoreCase(status)) {
                return v;
            }
        }
        return NONE;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }
}
