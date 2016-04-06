/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.PasscodeManager;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 3/31/16.
 */
public class PasscodeManagerImpl implements PasscodeManager {

    private int counter = 0;
    private int PASSCODE_LENGTH = 4;

    /**
     * Validating the passcode which user has entered
     *
     * @param passCode
     * @return
     */
    @Override
    public boolean validatePasscode(String passCode) {
        if (passCode.equalsIgnoreCase(getStoredPassCode())) {
            updateWrongPasscodeCounter(0);
            return true;
        } else {
            updateWrongPasscodeCounter(counter++);
        }
        return false;
    }

    /**
     * Validate passcode Length
     *
     * @param passcode
     * @return
     */
    @Override
    public boolean checkPasscodeLength(String passcode) {
        if (passcode.length() == PASSCODE_LENGTH) {
            return true;
        }
        return false;
    }


    /**
     * Validate passcode while creating
     *
     * @param passcode
     * @param passcode2
     * @return
     */
    @Override
    public boolean validateTwoPasscode(String passcode, String passcode2) {
        if (passcode.equalsIgnoreCase(passcode2)) {
            storedPassCode(passcode);
            return true;
        }
        return false;
    }

    /**
     * Checked the counter of wrong enter passocode limit is reached or not
     *
     * @return
     */
    public boolean isWrongCounterReached() {
        if (getWrongPasscodeCounter() >= AppConstant.MAX_COUNTER) {
            return true;
        }
        return false;
    }

    /**
     * update the counter while entering wrong passcode
     *
     * @return int
     */
    private int getWrongPasscodeCounter() {
        return YonaApplication.getAppContext().getUserPreferences().getInt(PreferenceConstant.YONA_WRONG_PASSCODE_COUNTER, 0);
    }


    /**
     * Update the passcode counter into preference storage
     *
     * @param counter
     */
    private void updateWrongPasscodeCounter(int counter) {
        YonaApplication.getAppContext().getUserPreferences().edit().putInt(PreferenceConstant.YONA_WRONG_PASSCODE_COUNTER, counter).commit();
    }


    /**
     * Get the value of passcode which is stored into preference storage
     *
     * @return int
     */
    private String getStoredPassCode() {
        return YonaApplication.getAppContext().getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, "");
    }

    /**
     * Stored User passcode into pref
     *
     * @param code
     */
    private void storedPassCode(String code) {
        YonaApplication.getAppContext().getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSCODE, code).commit();
        YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_PASSCODE, true).commit();
    }

}
