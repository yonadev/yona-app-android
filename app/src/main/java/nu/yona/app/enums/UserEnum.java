/*
 * <?xml version="1.0" encoding="utf-8"?><!--
 * ~ Copyright (c) 2018 Stichting Yona Foundation
 *   ~
 *   ~ This Source Code Form is subject to the terms of the Mozilla Public
 *   ~ License, v. 2.0. If a copy of the MPL was not distributed with this
 *   ~ file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *   -->
 */

package nu.yona.app.enums;

public enum UserEnum
{
	/**
	 * Not requested friend status enum.
	 */
	NO_USER("NONE"),
	/**
	 * Requested friend status enum.
	 */
	EMBEDDED_USER("EMBEDDED_USER"),
	/**
	 * Accepted friend status enum.
	 */
	LINKED_USER("LINKED_USER");
	/**
	 * The Status.
	 */
	final String userEnumValue;

	UserEnum(String userEnumValue)
	{
		this.userEnumValue = userEnumValue;
	}

	/**
	 * Gets User enum.
	 *
	 * @param userEnum the status
	 * @return the UserEnum enum
	 */
	public static UserEnum getUserEnum(String userEnum)
	{
		for (UserEnum v : values())
		{
			if (v.getUserEnumValue().equalsIgnoreCase(userEnum))
			{
				return v;
			}
		}
		return NO_USER;
	}


	/**
	 * Gets user enum.
	 *
	 * @return the enum
	 */
	public String getUserEnumValue()
	{
		return userEnumValue;
	}

}
