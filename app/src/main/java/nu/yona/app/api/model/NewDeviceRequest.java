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

/**
 * Created by kinnarvasa on 13/04/16.
 */
public class NewDeviceRequest {

    private String newDeviceRequestPassword;

    /**
     * Instantiates a new New device request.
     *
     * @param newDeviceRequestPassword the new device request password
     */
    public NewDeviceRequest(String newDeviceRequestPassword) {
        this.newDeviceRequestPassword = newDeviceRequestPassword;
    }

    /**
     * Gets new device request password.
     *
     * @return the new device request password
     */
    public String getNewDeviceRequestPassword() {
        return newDeviceRequestPassword;
    }

    /**
     * Sets new device request password.
     *
     * @param newDeviceRequestPassword the new device request password
     */
    public void setNewDeviceRequestPassword(String newDeviceRequestPassword) {
        this.newDeviceRequestPassword = newDeviceRequestPassword;
    }

}
