/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 13/04/16.
 */
public interface DeviceManager {

    /**
     * Add device.
     *
     * @param devicePassword the device password
     * @param listener       the listener
     */
    void addDevice(String devicePassword, DataLoadListener listener);

    /**
     * Delete device.
     *
     * @param listener the listener
     */
    void deleteDevice(DataLoadListener listener);

    /**
     * Validate mobile number boolean.
     *
     * @param mobileNumber the mobile number
     * @return the boolean
     */
    boolean validateMobileNumber(String mobileNumber);

    /**
     * Validate passcode boolean.
     *
     * @param passcode the passcode
     * @return the boolean
     */
    boolean validatePasscode(String passcode);

    /**
     * Validate device.
     *
     * @param devicePassword the device password
     * @param mobileNumber   the mobile number
     * @param listener       the listener
     */
    void validateDevice(String devicePassword, String mobileNumber, final DataLoadListener listener);
}
