/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public enum NotificationEnum
{

	/**
	 * Buddyconnectrequestmessage notification enum.
	 */
	BUDDYCONNECTREQUESTMESSAGE("BuddyConnectRequestMessage"),
	/**
	 * Buddyconnectresponsemessage notification enum.
	 */
	BUDDYCONNECTRESPONSEMESSAGE("BuddyConnectResponseMessage"),
	/**
	 * Buddydisconnectmessage notification enum.
	 */
	BUDDYDISCONNECTMESSAGE("BuddyDisconnectMessage"),
	/**
	 * Goalconflictmessage notification enum.
	 */
	GOALCONFLICTMESSAGE("GoalConflictMessage"),
	/**
	 * Goalchangemessage notification enum.
	 */
	GOALCHANGEMESSAGE("GoalChangeMessage"),
	/**
	 * BuddyInfoChangeMessage notification enum.
	 */
	BUDDYINFOCHANGEMESSAGE("BuddyInfoChangeMessage"),
	/**
	 * Disclosurerequestmessage notification enum.
	 */
	DISCLOSUREREQUESTMESSAGE("DisclosureRequestMessage"),
	/**
	 * Disclosureresponsemessage notification enum.
	 */
	DISCLOSURERESPONSEMESSAGE("DisclosureResponseMessage"),

	ACTIVITYCOMMENTMESSAGE("ActivityCommentMessage"),

	SYSTEMMESSAGE("SystemMessage"),
	/**
	 * None notification enum.
	 */
	NONE("none");

	/**
	 * The User message.
	 */
	UserEnum userEnum;

	/**
	 * The Notification type.
	 */
	String notificationType;

	NotificationEnum(String type)
	{
		this.notificationType = type;
		setUpUserEnum();
	}

	private void setUpUserEnum()
	{
		switch (this.notificationType)
		{
			case "none":
				this.userEnum = UserEnum.NO_USER;
				break;
			case "SystemMessage":
				this.userEnum = UserEnum.NO_USER;
				break;
			case "BuddyConnectRequestMessage":
				this.userEnum = UserEnum.EMBEDDED_USER;
				break;
			default:
				this.userEnum = UserEnum.LINKED_USER;
				break;

		}
	}

	/**
	 * Gets notification enum.
	 *
	 * @param notificationType the notification type
	 * @return the notification enum
	 */
	public static NotificationEnum getNotificationEnum(String notificationType)
	{
		for (NotificationEnum v : values())
		{
			if (v.getNotificationType().equalsIgnoreCase(notificationType))
			{
				return v;
			}
		}
		return NONE;
	}

	/**
	 * Gets notification type.
	 *
	 * @return the notification type
	 */
	public String getNotificationType()
	{
		return notificationType;
	}

	/**
	 * Gets notification type.
	 *
	 * @return the notification type
	 */
	public UserEnum getUserEnum()
	{
		return userEnum;
	}
}
