/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

/**
 * Created by kinnarvasa on 07/06/16.
 */
public enum ChartTypeEnum
{

	/**
	 * Time bucket control chart type enum.
	 */
	TIME_BUCKET_CONTROL("TIME_BUCKET_CONTROL", 1),
	/**
	 * Time frame control chart type enum.
	 */
	TIME_FRAME_CONTROL("TIME_FRAME_CONTROL", 2),
	/**
	 * Nogo control chart type enum.
	 */
	NOGO_CONTROL("NOGO_CONTROL", 3),
	/**
	 * Week score control chart type enum.
	 */
	WEEK_SCORE_CONTROL("WEEK_SCORE_CONTROL", 4),
	/**
	 * Spread control chart type enum.
	 */
	SPREAD_CONTROL("SPREAD_CONTROL", 5),
	/**
	 * Badge control chart type enum.
	 */
	BADGE_CONTROL("BADGE_CONTROL", 6),
	/**
	 * Encouragement control chart type enum.
	 */
	ENCOURAGEMENT_CONTROL("ENCORAGEMENT_CONTROL", 7),
	/**
	 * Chat control chart type enum.
	 */
	CHAT_CONTROL("CHAT_CONTROL", 8),
	/**
	 * None none chart type enum.
	 */
	NONE_NONE("NONE_NONE", 9),

	LINE("LINE", 10),

	TITLE("TITLE", 11);

	private final String chartType;
	private final int id;

	ChartTypeEnum(String chartType, int id)
	{
		this.chartType = chartType;
		this.id = id;
	}

	/**
	 * Gets chart type enum.
	 *
	 * @param id the id
	 * @return the chart type enum
	 */
	public static ChartTypeEnum getChartTypeEnum(int id)
	{
		for (ChartTypeEnum v : values())
		{
			if (v.getId() == id)
			{
				return v;
			}
		}
		return NONE_NONE;
	}

	/**
	 * Gets chart type enum.
	 *
	 * @param type the type
	 * @return the chart type enum
	 */
	public static ChartTypeEnum getChartTypeEnum(String type)
	{
		for (ChartTypeEnum v : values())
		{
			if (v.getChartType() == type)
			{
				return v;
			}
		}
		return NONE_NONE;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * Gets chart type.
	 *
	 * @return the chart type
	 */
	public String getChartType()
	{
		return this.chartType;
	}
}
