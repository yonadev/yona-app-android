/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

/**
 * Created by kinnarvasa on 21/04/16.
 */
public enum ChallengesEnum
{

	/**
	 * Credit tab challenges enum.
	 */
	CREDIT_TAB(0),
	/**
	 * Zone tab challenges enum.
	 */
	ZONE_TAB(1),
	/**
	 * No go tab challenges enum.
	 */
	NO_GO_TAB(2);

	private int tab;

	ChallengesEnum(int tab)
	{
		this.tab = tab;
	}

	/**
	 * Gets enum.
	 *
	 * @param value the value
	 * @return the enum
	 */
	public static ChallengesEnum getEnum(int value)
	{
		for (ChallengesEnum v : values())
		{
			if (v.getTab() == value)
			{
				return v;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Gets tab.
	 *
	 * @return the tab
	 */
	public int getTab()
	{
		return tab;
	}

	/**
	 * Sets tab.
	 *
	 * @param tab the tab
	 */
	public void setTab(int tab)
	{
		this.tab = tab;
	}
}
