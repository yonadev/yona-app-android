/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import org.junit.Before;
import org.junit.Test;

import nu.yona.app.YonaApplication;
import nu.yona.app.YonaTestCase;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 4/4/16.
 */
public class PasscodeManagerImplTest extends YonaTestCase {

    private PasscodeManagerImpl passcodeManager;

    @Before
    public void setUp() throws Exception {
        passcodeManager = new PasscodeManagerImpl();
    }

    @Test
    public void checkValidatePasscode() {
        YonaApplication.getUserPreferences().edit().putInt(PreferenceConstant.YONA_PASSCODE, 1111).commit();
        assertTrue(passcodeManager.validatePasscode(1111));
    }

    @Test
    public void checkPasscodeLength() {
        assertTrue(passcodeManager.checkPasscodeLength("2345"));
    }

    @Test
    public void verifyPasscodeEnter() {
        assertTrue(passcodeManager.validateTwoPasscode(1234, 1234));
    }

    @Test
    public void isWrongPasscodeCountReachedLimit() {

        passcodeManager.validatePasscode(1234);
        passcodeManager.validatePasscode(1234);
        passcodeManager.validatePasscode(1234);
        passcodeManager.validatePasscode(1234);
        passcodeManager.validatePasscode(1234);

        assertTrue(passcodeManager.isWrongCounterReached());
    }

}
