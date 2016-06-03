/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

/**
 * Created by bhargavsuthar on 3/31/16.
 */
public interface PasscodeManager {

    /**
     * Validate passcode boolean.
     *
     * @param passCode the pass code
     * @return the boolean
     */
    boolean validatePasscode(String passCode);

    /**
     * Check passcode length boolean.
     *
     * @param passcode the passcode
     * @return the boolean
     */
    boolean checkPasscodeLength(String passcode);

    /**
     * Validate two passcode boolean.
     *
     * @param passcode  the passcode
     * @param passcode2 the passcode 2
     * @return the boolean
     */
    boolean validateTwoPasscode(String passcode, String passcode2);

    /**
     * Is wrong counter reached boolean.
     *
     * @return the boolean
     */
    boolean isWrongCounterReached();

    /**
     * reset counter
     */
    void resetWrongCounter();

}
