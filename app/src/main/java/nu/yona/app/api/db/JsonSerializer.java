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

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public class JsonSerializer implements DbSerializer {

    private static final String TAG = JsonSerializer.class.getCanonicalName();

    private final ObjectMapper mapper;

    /**
     * Instantiates a new Json serializer.
     */
    public JsonSerializer() {
        mapper = JsonParserHelper.getObjectMapper();
    }


    @Override
    public byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            Log.e(TAG, "Failed to serialize object!", e);
        }
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) {
        try {
            return mapper.readValue(data, type);
        } catch (IOException e) {
            Log.e(TAG, "Failed to deserialize object!", e);
        }
        return null;
    }
}
