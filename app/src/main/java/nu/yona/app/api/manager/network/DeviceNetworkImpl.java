/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.network;

import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 13/04/16.
 */
public class DeviceNetworkImpl extends BaseImpl {

    /**
     * Add device on server
     *
     * @param devicePassword : random generated from devcie
     * @param yonaPassword   : yona application password need to pass in header
     */
    public void addDevice(String url, NewDeviceRequest devicePassword, String yonaPassword, final DataLoadListener listener) {
        try {
            getRestApi().addDevice(url, yonaPassword, devicePassword).enqueue(getCall(listener));
        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                listener.onError(new ErrorMessage(e.getMessage()));
            }
        }
    }

    /**
     *
     * @param url url to send delete request
     * @param yonaPassword applicaiton password need to pass in header
     * @param listener
     */
    public void deleteDevice(String url, String yonaPassword, DataLoadListener listener) {
        try {
            getRestApi().deleteDevice(url, yonaPassword).enqueue(getCall(listener));
        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                listener.onError(new ErrorMessage(e.getMessage()));
            }
        }
    }
}
