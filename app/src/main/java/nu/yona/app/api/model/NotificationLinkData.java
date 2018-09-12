/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

/**
 * Created by spatni on 15/09/17.
 * Data class written to hold link & event time for NoGo notifications.
 */

public class NotificationLinkData
{

	public String eventTime;
	public String url;

	public String getEventTime()
	{
		return eventTime;
	}

	public void setEventTime(String eventTime)
	{
		this.eventTime = eventTime;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
