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

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.test.MoreAsserts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.Links;
import nu.yona.app.api.model.User;
import nu.yona.app.state.EventChangeManager;

import static junit.framework.Assert.assertNotNull;


/**
 * Created by kinnarvasa on 30/03/16.
 */
public class YonaApplicationTest extends YonaTestCase{
    private YonaApplication yonaApplication;

    @Before
    public  void setUp() throws Exception {
        prepareMockedSetupForApplication();
    }

    public void prepareMockedSetupForApplication(){
        yonaApplication = (YonaApplication) RuntimeEnvironment.application;
        User user = new User();
        user.setNickname("Mocked User");
        Href userSelfHref = new Href();
        userSelfHref.setHref("Url");
        Links links = new Links();
        links.setSelf(userSelfHref);
        user.setLinks(links);
        user.setMobileNumberConfirmationCode("4444");
        yonaApplication.getEventChangeManager().getSharedPreference().setYonaPassword("testYonaPwd");
        yonaApplication.getEventChangeManager().getDataState().setUser(user);
    }

    @Test
    public void testCorrectVersion() throws Exception {
        PackageInfo info = yonaApplication.getPackageManager().getPackageInfo(yonaApplication.getPackageName(), 0);
        assertNotNull(info);
        MoreAsserts.assertMatchesRegex("\\d\\.\\d", info.versionName);
    }

    @After
    public void tearDown() {
        yonaApplication = null;
    }


}
