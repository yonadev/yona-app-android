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

import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 28/04/16.
 */
public interface BuddyManager {

    boolean validateText(String string);

    boolean validateEmail(String email);

    boolean validateMobileNumber(String mobileNumber);

    void getBuddies(DataLoadListener listener);

    void addBuddy(String firstName, String lastName, String email, String mobileNumber, final DataLoadListener listener);

    void deleteBuddy(YonaBuddy buddy, DataLoadListener listener);

}
