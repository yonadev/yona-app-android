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
 * The type Yona links.
 */
public class YonaLinks extends BaseEntity
{

	@SerializedName("yona:confirmMobileNumber")
	@Expose
	private Href yonaConfirmMobileNumber;
	@SerializedName("yona:messages")
	@Expose
	private Href yonaMessages;
	@SerializedName("yona:weeklyActivityReports")
	@Expose
	private Href yonaWeeklyActivityReports;
	@SerializedName("yona:dailyActivityReports")
	@Expose
	private Href yonaDailyActivityReports;
	@SerializedName("yona:newDeviceRequest")
	@Expose
	private Href yonaNewDeviceRequest;
	@SerializedName("yona:appActivity")
	@Expose
	private Href yonaAppActivity;
	@SerializedName("curies")
	@Expose
	private List<Cury> curies = new ArrayList<>();
	@SerializedName("self")
	@Expose
	private Href self;
	@SerializedName("edit")
	@Expose
	private Href edit;

	/**
	 * Gets yona confirm mobile number.
	 *
	 * @return The yonaConfirmMobileNumber
	 */
	public Href getYonaConfirmMobileNumber()
	{
		return yonaConfirmMobileNumber;
	}

	/**
	 * Sets yona confirm mobile number.
	 *
	 * @param yonaConfirmMobileNumber The yona:confirmMobileNumber
	 */
	public void setYonaConfirmMobileNumber(Href yonaConfirmMobileNumber)
	{
		this.yonaConfirmMobileNumber = yonaConfirmMobileNumber;
	}

	/**
	 * Gets yona messages.
	 *
	 * @return The yonaMessages
	 */
	public Href getYonaMessages()
	{
		return yonaMessages;
	}

	/**
	 * Sets yona messages.
	 *
	 * @param yonaMessages The yona:messages
	 */
	public void setYonaMessages(Href yonaMessages)
	{
		this.yonaMessages = yonaMessages;
	}

	/**
	 * Gets yona weekly activity reports.
	 *
	 * @return The yonaWeeklyActivityReports
	 */
	public Href getYonaWeeklyActivityReports()
	{
		return yonaWeeklyActivityReports;
	}

	/**
	 * Sets yona weekly activity reports.
	 *
	 * @param yonaWeeklyActivityReports The yona:weeklyActivityReports
	 */
	public void setYonaWeeklyActivityReports(Href yonaWeeklyActivityReports)
	{
		this.yonaWeeklyActivityReports = yonaWeeklyActivityReports;
	}

	/**
	 * Gets yona daily activity reports.
	 *
	 * @return The yonaDailyActivityReports
	 */
	public Href getYonaDailyActivityReports()
	{
		return yonaDailyActivityReports;
	}

	/**
	 * Sets yona daily activity reports.
	 *
	 * @param yonaDailyActivityReports The yona:dailyActivityReports
	 */
	public void setYonaDailyActivityReports(Href yonaDailyActivityReports)
	{
		this.yonaDailyActivityReports = yonaDailyActivityReports;
	}

	/**
	 * Gets yona new device request.
	 *
	 * @return The yonaNewDeviceRequest
	 */
	public Href getYonaNewDeviceRequest()
	{
		return yonaNewDeviceRequest;
	}

	/**
	 * Sets yona new device request.
	 *
	 * @param yonaNewDeviceRequest The yona:newDeviceRequest
	 */
	public void setYonaNewDeviceRequest(Href yonaNewDeviceRequest)
	{
		this.yonaNewDeviceRequest = yonaNewDeviceRequest;
	}

	/**
	 * Gets yona app activity.
	 *
	 * @return The yonaAppActivity
	 */
	public Href getYonaAppActivity()
	{
		return yonaAppActivity;
	}

	/**
	 * Sets yona app activity.
	 *
	 * @param yonaAppActivity The yona:appActivity
	 */
	public void setYonaAppActivity(Href yonaAppActivity)
	{
		this.yonaAppActivity = yonaAppActivity;
	}

	/**
	 * Gets curies.
	 *
	 * @return The curies
	 */
	public List<Cury> getCuries()
	{
		return curies;
	}

	/**
	 * Sets curies.
	 *
	 * @param curies The curies
	 */
	public void setCuries(List<Cury> curies)
	{
		this.curies = curies;
	}

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

	/**
	 * Gets edit.
	 *
	 * @return The edit
	 */
	public Href getEdit()
	{
		return edit;
	}

	/**
	 * Sets edit.
	 *
	 * @param edit The edit
	 */
	public void setEdit(Href edit)
	{
		this.edit = edit;
	}


	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
