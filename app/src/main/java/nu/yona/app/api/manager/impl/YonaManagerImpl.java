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

import nu.yona.app.api.manager.YonaManager;
import nu.yona.app.api.manager.network.AppNetworkImpl;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;

/**
 * Created by kinnarvasa on 30/03/16.
 */
public class YonaManagerImpl implements YonaManager {

    private AppNetworkImpl appNetworkImpl;

    /**
     * Instantiates a new Activity manager.
     *
     */
    public  YonaManagerImpl() {
        appNetworkImpl = new AppNetworkImpl();
    }

    /**
     * Validating the passcode which user has entered
     *
     * @param listener
     * @return
     */
    @Override
    public void postOpenAppEvent(String url, String yonaPassword,DataLoadListenerImpl listener){
        appNetworkImpl.postYonaOpenAppEvent(url, yonaPassword,listener);
    }

}
