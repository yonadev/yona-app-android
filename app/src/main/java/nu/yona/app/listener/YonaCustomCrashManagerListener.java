/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.listener;

import net.hockeyapp.android.CrashManagerListener;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class YonaCustomCrashManagerListener extends CrashManagerListener {

    @Override
    public boolean shouldAutoUploadCrashes() {
        return true;
    }
}
