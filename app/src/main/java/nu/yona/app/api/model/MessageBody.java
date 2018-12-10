/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public class MessageBody extends BaseEntity
{
	@SerializedName("properties")
	@Expose
	private Properties properties;

	/**
	 * Gets properties.
	 *
	 * @return The properties
	 */
	public Properties getProperties()
	{
		return properties;
	}

	/**
	 * Sets properties.
	 *
	 * @param properties The properties
	 */
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
