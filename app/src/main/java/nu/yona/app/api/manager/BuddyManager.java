/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 28/04/16.
 */
public interface BuddyManager
{

	/**
	 * Validate text boolean.
	 *
	 * @param string the string
	 * @return the boolean
	 */
	boolean validateText(String string);

	/**
	 * Validate email boolean.
	 *
	 * @param email the email
	 * @return the boolean
	 */
	boolean validateEmail(String email);

	/**
	 * Validate mobile number boolean.
	 *
	 * @param mobileNumber the mobile number
	 * @return the boolean
	 */
	boolean validateMobileNumber(String mobileNumber);

	/**
	 * Gets buddies.
	 *
	 * @param listener the listener
	 */
	void getBuddies(DataLoadListener listener);

	/**
	 * Add buddy.
	 *
	 * @param firstName    the first name
	 * @param lastName     the last name
	 * @param email        the email
	 * @param mobileNumber the mobile number
	 * @param listener     the listener
	 */
	void addBuddy(String firstName, String lastName, String email, String mobileNumber, final DataLoadListener listener);

	/**
	 * Delete buddy.
	 *
	 * @param buddy    the buddy
	 * @param listener the listener
	 */
	void deleteBuddy(YonaBuddy buddy, DataLoadListener listener);

}
