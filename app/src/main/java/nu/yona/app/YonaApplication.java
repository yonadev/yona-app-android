/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app;

import android.app.Application;
import android.os.StrictMode;

import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.Foreground;

/**
 * Created by kinnarvasa on 16/03/16.
 */
public class YonaApplication extends Application {

    private static YonaApplication mContext;
    private static EventChangeManager eventChangeManager;

    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static synchronized YonaApplication getAppContext() {
        return mContext;
    }

    /**
     * Gets event change manager.
     *
     * @return the event change manager
     */
    public static EventChangeManager getEventChangeManager() {
        return eventChangeManager;
    }

    @Override
    public void onCreate() {
        enableStickMode();
        super.onCreate();
        mContext = this;
        eventChangeManager = new EventChangeManager();
        Foreground.init(this);
    }

    private void enableStickMode() {
        if (getResources().getBoolean(R.bool.developerMode)) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
    }
}
