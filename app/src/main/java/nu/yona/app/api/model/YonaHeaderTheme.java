/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

/**
 * Created by bhargavsuthar on 23/06/16.
 */
public class YonaHeaderTheme extends BaseEntity
{

	private int header_rightIcon;

	private int header_leftIcon;

	private String header_title;

	private int headercolor;

	private Href dayActivityUrl;

	private Href weekActivityUrl;

	private boolean isBuddyFlow;

	private int toolbar;

	public YonaHeaderTheme()
	{

	}

	/**
	 * Instantiates a new Yona header theme.
	 *
	 * @param isBuddyFlow     the is buddy flow
	 * @param dayActivityUrl  the day activity url
	 * @param weekActivityUrl the week activity url
	 * @param leftIcon        the left icon
	 * @param rightIcon       the right icon
	 * @param title           the title
	 * @param headerColor     the header color
	 * @param toolbar         the toolbar
	 */
	public YonaHeaderTheme(boolean isBuddyFlow, Href dayActivityUrl, Href weekActivityUrl, int leftIcon, int rightIcon, String title, int headerColor, int toolbar)
	{
		this.header_rightIcon = rightIcon;
		this.header_leftIcon = leftIcon;
		this.header_title = title;
		this.headercolor = headerColor;
		this.dayActivityUrl = dayActivityUrl;
		this.weekActivityUrl = weekActivityUrl;
		this.isBuddyFlow = isBuddyFlow;
		this.toolbar = toolbar;
	}

	/**
	 * Is buddy flow boolean.
	 *
	 * @return the boolean
	 */
	public boolean isBuddyFlow()
	{
		return isBuddyFlow;
	}

	/**
	 * Sets buddy flow.
	 *
	 * @param buddyFlow the buddy flow
	 */
	public void setBuddyFlow(boolean buddyFlow)
	{
		isBuddyFlow = buddyFlow;
	}

	/**
	 * Gets day activity url.
	 *
	 * @return the day activity url
	 */
	public Href getDayActivityUrl()
	{
		return dayActivityUrl;
	}

	/**
	 * Sets day activity url.
	 *
	 * @param dayActivityUrl the day activity url
	 */
	public void setDayActivityUrl(Href dayActivityUrl)
	{
		this.dayActivityUrl = dayActivityUrl;
	}

	/**
	 * Gets week activity url.
	 *
	 * @return the week activity url
	 */
	public Href getWeekActivityUrl()
	{
		return weekActivityUrl;
	}

	/**
	 * Sets week activity url.
	 *
	 * @param weekActivityUrl the week activity url
	 */
	public void setWeekActivityUrl(Href weekActivityUrl)
	{
		this.weekActivityUrl = weekActivityUrl;
	}

	/**
	 * Gets header right icon.
	 *
	 * @return the header right icon
	 */
	public int getHeader_rightIcon()
	{
		return header_rightIcon;
	}

	/**
	 * Sets header right icon.
	 *
	 * @param header_rightIcon the header right icon
	 */
	public void setHeader_rightIcon(int header_rightIcon)
	{
		this.header_rightIcon = header_rightIcon;
	}

	/**
	 * Gets header left icon.
	 *
	 * @return the header left icon
	 */
	public int getHeader_leftIcon()
	{
		return header_leftIcon;
	}

	/**
	 * Sets header left icon.
	 *
	 * @param header_leftIcon the header left icon
	 */
	public void setHeader_leftIcon(int header_leftIcon)
	{
		this.header_leftIcon = header_leftIcon;
	}

	/**
	 * Gets header title.
	 *
	 * @return the header title
	 */
	public String getHeader_title()
	{
		return header_title;
	}

	/**
	 * Sets header title.
	 *
	 * @param header_title the header title
	 */
	public void setHeader_title(String header_title)
	{
		this.header_title = header_title;
	}

	/**
	 * Gets headercolor.
	 *
	 * @return the headercolor
	 */
	public int getHeadercolor()
	{
		return headercolor;
	}

	/**
	 * Sets headercolor.
	 *
	 * @param headercolor the headercolor
	 */
	public void setHeadercolor(int headercolor)
	{
		this.headercolor = headercolor;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	/**
	 * Gets toolbar.
	 *
	 * @return the toolbar
	 */
	public int getToolbar()
	{
		return toolbar;
	}

	/**
	 * Sets toolbar.
	 *
	 * @param toolbar the toolbar
	 */
	public void setToolbar(int toolbar)
	{
		this.toolbar = toolbar;
	}
}
