/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 3/31/16.
 */
public interface PasscodeManager {

    boolean validatePasscode(String passCode);

    boolean checkPasscodeLength(String passcode);

    boolean validateTwoPasscode(String passcode, String passcode2);

    void resendPasscode(DataLoadListener listener);
}
