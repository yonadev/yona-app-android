/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.utils;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public interface PreferenceConstant {
    String USER_PREFERENCE_KEY = "userPreferenceKey";
    String YONA_PASSWORD = "yonaPassword";
    String YONA_PASSCODE = "yonaPasscode";
    String YONA_WRONG_PASSCODE_COUNTER = "wrongPasscodeCounter";

    String STEP_REGISTER = "stepRegister";
    String STEP_OTP = "stepOTP";
    String STEP_PASSCODE = "stepPasscode";
    String STEP_CHALLENGES = "stepChallenges";
}
