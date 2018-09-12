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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class EmbeddedActivityCategories extends BaseEntity
{

	@SerializedName("yona:activityCategories")
	@Expose
	private List<YonaActivityCategories> yonaActivityCategories = new ArrayList<>();

	/**
	 * Gets yona activity categories.
	 *
	 * @return the yona activity categories
	 */
	public List<YonaActivityCategories> getYonaActivityCategories()
	{
		return yonaActivityCategories;
	}

	/**
	 * Sets yona activity categories.
	 *
	 * @param yonaActivityCategories the yona activity categories
	 */
	public void setYonaActivityCategories(List<YonaActivityCategories> yonaActivityCategories)
	{
		this.yonaActivityCategories = yonaActivityCategories;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
