/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.DeviceManager;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.DeviceNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 13/04/16.
 */
public class DeviceManagerImpl implements DeviceManager {

    private DeviceNetworkImpl deviceNetwork;
    private AuthenticateDAO authenticateDAO;

    public DeviceManagerImpl(Context context) {
        deviceNetwork = new DeviceNetworkImpl();
        authenticateDAO = new AuthenticateDAO(DatabaseHelper.getInstance(context), context);
    }

    /**
     * Add another device
     *
     * @param devicePassword password generated from device
     * @param listener
     */
    public void addDevice(String devicePassword, final DataLoadListener listener) {
        deviceNetwork.addDevice(authenticateDAO.getUser().getLinks().getYonaNewDeviceRequest().getHref(),
                new NewDeviceRequest(devicePassword), YonaApplication.getYonaPassword(),
                new DataLoadListener() {

                    @Override
                    public void onDataLoad(Object result) {
                        listener.onDataLoad(result);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (errorMessage instanceof ErrorMessage) {
                            listener.onError(errorMessage);
                        } else {
                            listener.onError(new ErrorMessage(errorMessage.toString()));
                        }
                    }
                });
    }

    public void deleteDevice(final DataLoadListener listener) {
        deviceNetwork.deleteDevice(authenticateDAO.getUser().getLinks().getYonaNewDeviceRequest().getHref(), YonaApplication.getYonaPassword(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                listener.onDataLoad(result);
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }
}
