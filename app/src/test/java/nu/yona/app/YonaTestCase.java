/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app;

import android.test.AndroidTestCase;

import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import nu.yona.app.support.YonaRunner;

/**
 * Created by kinnarvasa on 30/03/16.
 */

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(YonaRunner.class)
public class YonaTestCase extends AndroidTestCase{
}
