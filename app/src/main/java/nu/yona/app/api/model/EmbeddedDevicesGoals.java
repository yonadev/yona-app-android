/*
 * <?xml version="1.0" encoding="utf-8"?><!--
 * ~ Copyright (c) 2018 Stichting Yona Foundation
 *   ~
 *   ~ This Source Code Form is subject to the terms of the Mozilla Public
 *   ~ License, v. 2.0. If a copy of the MPL was not distributed with this
 *   ~ file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *   -->
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmbeddedDevicesGoals extends BaseEntity
{


	@SerializedName("yona:goals")
	@Expose
	private YonaGoals yonaGoals;

	@SerializedName("yona:devices")
	@Expose
	private YonaDevices yonaDevices;

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	public YonaGoals getYonaGoals()
	{
		return yonaGoals;
	}

	public void setYonaGoals(YonaGoals yonaGoals)
	{
		this.yonaGoals = yonaGoals;
	}

	public YonaDevices getYonaDevices()
	{
		return yonaDevices;
	}

	public void setYonaDevices(YonaDevices yonaDevices)
	{
		this.yonaDevices = yonaDevices;
	}
}
