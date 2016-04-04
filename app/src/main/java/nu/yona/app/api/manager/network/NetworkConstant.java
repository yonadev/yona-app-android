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

    String YONA_PASSWORD = "Yona-Password";
    String BODY = "body";

    String CACHING_FILE = "apiResponse";

    long API_CONNECT_TIMEOUT_IN_SECONDS = 60;
    int API_WRITE_TIMEOUT_IN_SECONDS = 60;
    int API_READ_TIMEOUT_IN_SECONDS = 60;

    int RESPONSE_STATUS = 300;
}
