/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

/**
 * Author @@MobiquityInc
 */
public interface ApiList
{
	/**
	 * The constant USER.
	 */
	String USER = "/users/";
	/**
	 * The constant ADMIN_OVERRIDE_USER.
	 */
	String ADMIN_OVERRIDE_USER = "/admin/requestUserOverwrite/";
	/**
	 * The constant NEW_DEVICE_REQUEST.
	 */
	String NEW_DEVICE_REQUEST = "newDeviceRequests/{mobileNumber}";
	/**
	 * The constant ACTIVITY_CATEGORIES.
	 */
	String ACTIVITY_CATEGORIES = "activityCategories/";

	/**
	 * The constant PRIVACY_PAGE.
	 */
	String PRIVACY_PAGE = "http://www.yona.nu/app/privacy";

}
