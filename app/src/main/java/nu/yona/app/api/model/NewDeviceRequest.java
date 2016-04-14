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

    public NewDeviceRequest(String newDeviceRequestPassword) {
        this.newDeviceRequestPassword = newDeviceRequestPassword;
    }

    public String getNewDeviceRequestPassword() {
        return newDeviceRequestPassword;
    }

    public void setNewDeviceRequestPassword(String newDeviceRequestPassword) {
        this.newDeviceRequestPassword = newDeviceRequestPassword;
    }

}
