/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Href.
 */
public class Href extends BaseEntity
{

	@SerializedName("href")
	@Expose
	private String href;

	/**
	 * Gets href.
	 *
	 * @return The href
	 */
	public String getHref()
	{
		return href;
	}

	/**
	 * Sets href.
	 *
	 * @param href The href
	 */
	public void setHref(String href)
	{
		this.href = href;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
