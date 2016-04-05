/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public interface AuthenticateManager {

    boolean validateText(String string);

    boolean validateMobileNumber(String mobileNumber);

    void registerUser(String password, RegisterUser user, DataLoadListener listener);

    void verifyMobileNumber(String password, String otp, DataLoadListener listener);
}
