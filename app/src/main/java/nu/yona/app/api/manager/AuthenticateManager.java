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
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public interface AuthenticateManager {

    boolean validateText(String string);

    boolean validateMobileNumber(String mobileNumber);

    void registerUser(RegisterUser user, DataLoadListener listener);

    void verifyOTP(String otp, DataLoadListener listener);

    void resendOTP(DataLoadListener listener);

    void requestPinReset(DataLoadListener listener);

    User getUser();

    void getUser(final String url, final DataLoadListener listener);

    void requestUserOverride(String mobileNumber, final DataLoadListener listener);
}
