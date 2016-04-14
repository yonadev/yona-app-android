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

    void addDevice(String devicePassword, DataLoadListener listener);

    void deleteDevice(DataLoadListener listener);

    boolean validateMobileNumber(String mobileNumber);

    boolean validatePasscode(String passcode);

    void validateDevice(String devicePassword, String mobileNumber, final DataLoadListener listener);
}
