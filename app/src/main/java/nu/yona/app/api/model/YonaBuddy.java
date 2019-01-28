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

import java.util.List;

import nu.yona.app.enums.StatusEnum;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.DateUtility;

/**
 * The type Yona buddy.
 */
public class YonaBuddy extends BaseEntity
{

	@SerializedName("_links")
	@Expose
	private Links Links;
	@SerializedName("sendingStatus")
	@Expose
	private String sendingStatus;
	@SerializedName("receivingStatus")
	@Expose
	private String receivingStatus;
	@SerializedName("_embedded")
	@Expose
	private EmbeddedYonaUser Embedded;
	@SerializedName("lastMonitoredActivityDate")
	@Expose
	private String lastMonitoredActivityDate;


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
	 * Gets sending status.
	 *
	 * @return The sendingStatus
	 */
	public String getSendingStatus()
	{
		return sendingStatus;
	}

	/**
	 * It convert requested @{@link StatusEnum} to Local based string.
	 *
	 * @return Local based string for current @{@link StatusEnum}.
	 */
	public String getSendingStatusToDisplay()
	{
		return AppUtils.getSendingStatusToDisplay(sendingStatus);
	}

	/**
	 * Sets sending status.
	 *
	 * @param sendingStatus The sendingStatus
	 */
	public void setSendingStatus(String sendingStatus)
	{
		this.sendingStatus = sendingStatus;
	}

	/**
	 * Gets receiving status.
	 *
	 * @return The receivingStatus
	 */
	public String getReceivingStatus()
	{
		return receivingStatus;
	}

	/**
	 * Sets receiving status.
	 *
	 * @param receivingStatus The receivingStatus
	 */
	public void setReceivingStatus(String receivingStatus)
	{
		this.receivingStatus = receivingStatus;
	}

	/**
	 * Gets embedded.
	 *
	 * @return The Embedded
	 */
	public EmbeddedYonaUser getEmbedded()
	{
		return Embedded;
	}

	/**
	 * Sets embedded.
	 *
	 * @param Embedded The _embedded
	 */
	public void setEmbedded(EmbeddedYonaUser Embedded)
	{
		this.Embedded = Embedded;
	}

	public String getLastMonitoredActivityDate()
	{
		return lastMonitoredActivityDate;
	}

	public void setLastMonitoredActivityDate(String lastMonitoredActivityDate)
	{
		this.lastMonitoredActivityDate = lastMonitoredActivityDate;
	}

	/**
	 * Following used to read formatted date string for @lastMonitoredActivityDate
	 *
	 * @return formatted date string.
	 */
	public String getLastMonitoredActivityDateToDisplay()
	{
		return DateUtility.getFormattedRelativeDateDifference(lastMonitoredActivityDate);
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	public String retreiveNickname()
	{
		return this.getEmbedded().getYonaUser().getNickname();
	}

	public List<YonaGoal> retreiveYonaGoals()
	{
		return this.getEmbedded().getYonaUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
	}

}
