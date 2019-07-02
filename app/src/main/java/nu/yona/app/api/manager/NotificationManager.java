/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import java.util.List;

import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public interface NotificationManager
{
	/**
	 * Gets message.
	 *
	 * @param listener the listener
	 */
	void getMessages(DataLoadListener listener);


	void getMessages(boolean isUnreadStatus, final DataLoadListener listener);


	/**
	 * Gets a page of messages.
	 *
	 * @param urlForMessagesFetch the items per page
	 * @param isUnreadStatus      Whether to fetch only unread messages
	 * @param listener            the listener
	 */
	void getMessages(String urlForMessagesFetch, boolean isUnreadStatus, DataLoadListener listener);

	/**
	 * Post message.
	 *
	 * @param url      the url
	 * @param body     the body
	 * @param listener the listener
	 */
	void postMessage(String url, MessageBody body, DataLoadListener listener);

	/**
	 * Delete Notification Message
	 *
	 * @param url      the url
	 * @param listener the listener
	 */
	void deleteMessage(String url, DataLoadListener listener);

	void setReadMessage(List<YonaMessage> yonaMessageList, YonaMessage message, DataLoadListener listener);
}
