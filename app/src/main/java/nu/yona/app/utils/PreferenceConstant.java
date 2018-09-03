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
    /**
     * The constant USER_PREFERENCE_KEY.
     */
    String USER_PREFERENCE_KEY = "userPreferenceKey";
    /**
     * The constant APP_PREFERENCE_KEY.
     */
    String APP_PREFERENCE_KEY = "appPreferenceKey";
    /**
     * The constant YONA_PASSWORD.
     */
    String YONA_DATA = "yonaPassword";

    String YONA_IV = "yona_key";
    /**
     * The constant YONA_PASSCODE.
     */
    String YONA_PASSCODE = "yonaPasscode";
    /**
     * The constant YONA_WRONG_PASSCODE_COUNTER.
     */
    String YONA_WRONG_PASSCODE_COUNTER = "wrongPasscodeCounter";

    /**
     * The constant STEP_TOUR.
     */
    String STEP_TOUR = "stepTour";
    /**
     * The constant STEP_REGISTER.
     */
    String STEP_REGISTER = "stepRegister";
    /**
     * The constant STEP_OTP.
     */
    String STEP_OTP = "stepOTP";
    /**
     * The constant STEP_PASSCODE.
     */
    String STEP_PASSCODE = "stepPasscode";
    /**
     * The constant STEP_CHALLENGES.
     */
    String STEP_CHALLENGES = "stepChallenges";
    /**
     * The constant USER_BLOCKED.
     */
    String USER_BLOCKED = "userBlocked";

    String USER_WAIT_TIME_IN_LONG = "userWaitTime";

    String USER_WAIT_TIME_IN_STRING = "userWaitTimeTxt";

    String VPN_PROFILE_PATH = "vpnProfilePath";

    String VPN_PROFILE_ACTIVE = "vpnProfileActive";

    String ROOT_CERTIFICATE = "rootCertificate";

    String ROOT_CERTIFICATE_ACTIVE = "rootCertificateActive";

    String PROFILE_UUID = "profileUUID";

    String PROFILE_OTP_STEP = "profileOtpStep";

    String YONA_ENCRYPTION_METHOD = "yonaEncryptionMethod";
}
