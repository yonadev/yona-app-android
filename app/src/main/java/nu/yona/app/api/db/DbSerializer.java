/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.db;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public interface DbSerializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] data, Class<T> type);
}
