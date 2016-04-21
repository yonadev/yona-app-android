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
 * Author @@MobiquityInc
 */
public interface ApiList {
    String USER = "/users/";
    String USER_OVERRIDE =  "/users/?overwriteUserConfirmationCode={otp}";
    String ADMIN_OVERRIDE_USER = "/admin/requestUserOverwrite/";
    String NEW_DEVICE_REQUEST = "newDeviceRequests/{mobileNumber}";
    String ACTIVITY_CATEGORIES = "activityCategories/";
}
