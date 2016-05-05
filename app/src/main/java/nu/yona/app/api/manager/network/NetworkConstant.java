/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.network;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public interface NetworkConstant {

    /**
     * The constant YONA_PASSWORD.
     */
    String YONA_PASSWORD = "Yona-Password";
    /**
     * The constant ACCEPT_LAUNGUAGE.
     */
    String ACCEPT_LAUNGUAGE = "Accept-Language";
    /**
     * The constant CONTENT_TYPE.
     */
    String CONTENT_TYPE = "Content-Type";
    /**
     * The constant YONA_NEW_PASSWORD.
     */
    String YONA_NEW_PASSWORD = "Yona-NewDeviceRequestPassword";
    /**
     * The constant BODY.
     */
    String BODY = "body";

    /**
     * The constant CACHING_FILE.
     */
    String CACHING_FILE = "apiResponse";

    /**
     * The constant API_CONNECT_TIMEOUT_IN_SECONDS.
     */
    long API_CONNECT_TIMEOUT_IN_SECONDS = 30;
    /**
     * The constant API_WRITE_TIMEOUT_IN_SECONDS.
     */
    int API_WRITE_TIMEOUT_IN_SECONDS = 30;
    /**
     * The constant API_READ_TIMEOUT_IN_SECONDS.
     */
    int API_READ_TIMEOUT_IN_SECONDS = 30;

    /**
     * The constant RESPONSE_STATUS.
     */
    int RESPONSE_STATUS = 300;

    /**
     * The constant RESPONSE_ERROR_CODE.
     */
    int RESPONSE_ERROR_CODE = 400;
}
