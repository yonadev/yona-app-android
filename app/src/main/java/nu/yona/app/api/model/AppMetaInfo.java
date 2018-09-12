/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nu.yona.app.BuildConfig;

public class AppMetaInfo
{

	@SerializedName("operatingSystem")
	@Expose
	private final String operatingSystem;

	@SerializedName("appVersion")
	@Expose
	private final String appVersion;

	@SerializedName("appVersionCode")
	@Expose
	private final int appVersionCode;

	private static final AppMetaInfo theInstance = new AppMetaInfo("ANDROID", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

	private AppMetaInfo(String operatingSystem, String appVersion, int appVersionCode)
	{
		this.operatingSystem = operatingSystem;
		this.appVersion = appVersion;
		this.appVersionCode = appVersionCode;
	}

	public static AppMetaInfo getInstance()
	{
		return theInstance;
	}

	public int getAppVersionCode()
	{
		return appVersionCode;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public String getOperatingSystem()
	{
		return operatingSystem;
	}


}
