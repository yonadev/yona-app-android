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

import android.content.pm.PackageInfo;
import android.test.ApplicationTestCase;
import android.test.MoreAsserts;

/**
 * Created by kinnarvasa on 30/03/16.
 */
public class YonaApplicationTest extends ApplicationTestCase<YonaApplication> {
    private YonaApplication yonaApplication;

    public YonaApplicationTest(){
        super(YonaApplication.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        yonaApplication = getApplication();

    }

    public void testCorrectVersion() throws Exception {
        PackageInfo info = yonaApplication.getPackageManager().getPackageInfo(yonaApplication.getPackageName(), 0);
        assertNotNull(info);
        MoreAsserts.assertMatchesRegex("\\d\\.\\d", info.versionName);
    }
}
