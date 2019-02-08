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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.yona.app.YonaApplication;
import nu.yona.app.enums.UserStatus;

/**
 * The type User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity
{

	@SerializedName("_embedded")
	@Expose
	private Embedded Embedded;

	@SerializedName("_links")
	@Expose
	private Links Links;

	@SerializedName("firstName")
	@Expose
	private String firstName;

	@SerializedName("lastName")
	@Expose
	private String lastName;

	@SerializedName("mobileNumber")
	@Expose
	private String mobileNumber;


	@SerializedName("nickname")
	@Expose
	private String nickname;

	@SerializedName("devices")
	@Expose
	private List<String> devices = new ArrayList<>();

	@SerializedName("mobileNumberConfirmationCode")
	@Expose
	private String mobileNumberConfirmationCode;

	@SerializedName("sslRootCertCN")
	@Expose
	private String sslRootCertCN;

	@SerializedName("yonaPassword")
	@Expose
	private String yonaPassword;


	@SerializedName("status")
	@Expose
	private UserStatus status;

	@SerializedName("version")
	@Expose
	private int version;


	/**
	 * Gets mobile number confirmation code.
	 *
	 * @return the mobile number confirmation code
	 */
	public String getMobileNumberConfirmationCode()
	{
		return mobileNumberConfirmationCode;
	}

	/**
	 * Sets mobile number confirmation code.
	 *
	 * @param mobileNumberConfirmationCode the mobile number confirmation code
	 */
	public void setMobileNumberConfirmationCode(String mobileNumberConfirmationCode)
	{
		this.mobileNumberConfirmationCode = mobileNumberConfirmationCode;
	}


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
	 * Gets first name.
	 *
	 * @return The firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets first name.
	 *
	 * @param firstName The firstName
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets last name.
	 *
	 * @return The lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets last name.
	 *
	 * @param lastName The lastName
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Gets mobile number.
	 *
	 * @return The mobileNumber
	 */
	public String getMobileNumber()
	{
		return mobileNumber;
	}

	/**
	 * Sets mobile number.
	 *
	 * @param mobileNumber The mobileNumber
	 */
	public void setMobileNumber(String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}

	/**
	 * Gets vpn profile.
	 *
	 * @return The vpnProfile
	 */

	/**
	 * Gets nickname.
	 *
	 * @return The nickname
	 */
	public String getNickname()
	{
		return nickname;
	}

	/**
	 * Sets nickname.
	 *
	 * @param nickname The nickname
	 */
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	/**
	 * Gets devices.
	 *
	 * @return The devices
	 */
	public List<String> getDevices()
	{
		return devices;
	}

	/**
	 * Sets devices.
	 *
	 * @param devices The devices
	 */
	public void setDevices(List<String> devices)
	{
		this.devices = devices;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	public String getYonaPassword()
	{
		return this.yonaPassword;
	}

	public void setYonaPassword(String yonaPassword)
	{
		YonaApplication.getEventChangeManager().getSharedPreference().setYonaPassword(yonaPassword);
		this.yonaPassword = yonaPassword;
	}

	public UserStatus getStatus()
	{
		return status;
	}

	public void setStatus(UserStatus userStatus)
	{
		this.status = userStatus;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	@JsonIgnore
	public String getPostOpenAppEventLink()
	{
		return getCurrentDevice().getPostOpenAppEventLink();
	}

	@JsonIgnore
	public String getSslRootCertCN()
	{
		return getCurrentDevice().getSslRootCertCN();
	}

	@JsonIgnore
	public String getPostDeviceAppActivityLink()
	{
		return getCurrentDevice().getPostDeviceAppActivityLink();
	}

	@JsonIgnore
	public String getSslRootCertLink()
	{
		return getCurrentDevice().getSslRootCertLink();
	}

	@JsonIgnore
	public VpnProfile getVpnProfile()
	{
		return getCurrentDevice().getVpnProfile();
	}

	@JsonIgnore
	private YonaDevice getCurrentDevice()
	{
		return this.getEmbedded().getYonaDevices().getEmbedded().getCurrentDevice();
	}

	@JsonIgnore
	public List<YonaBuddy> getBuddies()
	{
		YonaBuddies buddiesContainer = this.getEmbedded().getYonaBuddies();
		if (buddiesContainer == null)
		{
			return Collections.emptyList();
		}
		return buddiesContainer.getEmbedded().getYonaBuddies();
	}

	public boolean isActive()
	{
		return this.getStatus() == UserStatus.ACTIVE;
	}
}
