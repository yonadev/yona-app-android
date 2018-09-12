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
 * The type Vpn profile.
 */
public class VpnProfile extends BaseEntity
{

	@SerializedName("vpnLoginID")
	@Expose
	private String vpnLoginID;
	@SerializedName("vpnPassword")
	@Expose
	private String vpnPassword;
	@SerializedName("_links")
	@Expose
	private Links links;

	/**
	 * Gets vpn login id.
	 *
	 * @return The vpnLoginID
	 */
	public String getVpnLoginID()
	{
		return vpnLoginID;
	}

	/**
	 * Sets vpn login id.
	 *
	 * @param vpnLoginID The vpnLoginID
	 */
	public void setVpnLoginID(String vpnLoginID)
	{
		this.vpnLoginID = vpnLoginID;
	}

	/**
	 * Gets vpn password.
	 *
	 * @return The vpnPassword
	 */
	public String getVpnPassword()
	{
		return vpnPassword;
	}

	/**
	 * Sets vpn password.
	 *
	 * @param vpnPassword The vpnPassword
	 */
	public void setVpnPassword(String vpnPassword)
	{
		this.vpnPassword = vpnPassword;
	}

	/**
	 * Gets links.
	 *
	 * @return the links
	 */
	public Links getLinks()
	{
		return this.links;
	}

	/**
	 * Sets links.
	 *
	 * @param links the links
	 */
	public void setLinks(Links links)
	{
		this.links = links;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

}
