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
 * The type Links.
 */
public class Links_ extends BaseEntity
{

	@SerializedName("self")
	@Expose
	private Href self;

	/**
	 * Gets self.
	 *
	 * @return The self
	 */
	public Href getSelf()
	{
		return self;
	}

	/**
	 * Sets self.
	 *
	 * @param self The self
	 */
	public void setSelf(Href self)
	{
		this.self = self;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
