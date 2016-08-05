/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import java.util.List;

import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public interface NotificationManager {
    /**
     * Gets message.
     *
     * @param listener the listener
     */
    void getMessage(DataLoadListener listener);

    /**
     * Gets message.
     *
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void getMessage(int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Post message.
     *
     * @param url          the url
     * @param body         the body
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void postMessage(String url, MessageBody body, int itemsPerPage, int pageNo, DataLoadListener listener);

    /**
     * Delete Notification Message
     *
     * @param url          the url
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    void deleteMessage(String url, final int itemsPerPage, final int pageNo, DataLoadListener listener);

    void setReadMessage(List<YonaMessage> yonaMessageList, YonaMessage message, DataLoadListener listener);
}
