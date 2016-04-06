/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 16/03/16.
 */
public class YonaApplication extends Application {

    private static YonaApplication mContext;
    private static SharedPreferences userPreferences;
    private static EventChangeManager eventChangeManager;

    public static synchronized YonaApplication getAppContext() {
        return mContext;
    }

    public static synchronized SharedPreferences getUserPreferences() {
        if (userPreferences == null) {
            userPreferences = getAppContext().getSharedPreferences(PreferenceConstant.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        }
        return userPreferences;
    }

    public static EventChangeManager getEventChangeManager() {
        return eventChangeManager;
    }

    /**
     * @return return yona password
     */
    public static String getYonaPassword() {
        String yonaPwd = getUserPreferences().getString(PreferenceConstant.YONA_PASSWORD, "");
        if (TextUtils.isEmpty(yonaPwd)) {
            yonaPwd = AppUtils.getRandomString();
            getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, yonaPwd).commit();
        }
        return yonaPwd;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        eventChangeManager = new EventChangeManager();
    }
}
