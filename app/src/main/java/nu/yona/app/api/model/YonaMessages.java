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
 * Created by kinnarvasa on 09/05/16.
 */
public class YonaMessages extends BaseEntity
{

	@SerializedName("_embedded")
	@Expose
	private nu.yona.app.api.model.Embedded Embedded;
	@SerializedName("_links")
	@Expose
	private Links Links;
	@SerializedName("page")
	@Expose
	private Page page;

	/**
	 * Gets embedded.
	 *
	 * @return The Embedded
	 */
	public nu.yona.app.api.model.Embedded getEmbedded()
	{
		return Embedded;
	}

	/**
	 * Sets embedded.
	 *
	 * @param Embedded The _embedded
	 */
	public void setEmbedded(nu.yona.app.api.model.Embedded Embedded)
	{
		this.Embedded = Embedded;
	}

	/**
	 * Gets links.
	 *
	 * @return The Links
	 */
	public Links getLinks()
	{
		return Links;
	}

	/**
	 * Sets links.
	 *
	 * @param Links The _links
	 */
	public void setLinks(Links Links)
	{
		this.Links = Links;
	}

	/**
	 * Gets page.
	 *
	 * @return The page
	 */
	public Page getPage()
	{
		return page;
	}

	/**
	 * Sets page.
	 *
	 * @param page The page
	 */
	public void setPage(Page page)
	{
		this.page = page;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
