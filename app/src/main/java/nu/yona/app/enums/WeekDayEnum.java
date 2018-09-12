/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhargavsuthar on 27/06/16.
 */
public enum WeekDayEnum
{

	SUNDAY("SUNDAY"),
	MONDAY("MONDAY"),
	TUESDAY("TUESDAY"),
	WEDNESDAY("WEDNESDAY"),
	THURSDAY("THURSDAY"),
	FRIDAY("FRIDAY"),
	SATURDAY("SATURDAY");

	private static final Map<String, WeekDayEnum> nameToEnumMapping = new HashMap<>();

	static
	{
		for (WeekDayEnum goalsEnum : WeekDayEnum.values())
		{
			nameToEnumMapping.put(goalsEnum.actionString, goalsEnum);
		}
	}

	private final String actionString;

	WeekDayEnum(String actionString)
	{
		this.actionString = actionString;
	}

	public static WeekDayEnum fromName(String actionString)
	{
		return nameToEnumMapping.get(actionString);
	}

	public String getActionString()
	{
		return actionString;
	}
}