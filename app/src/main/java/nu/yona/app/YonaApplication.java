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
import android.os.StrictMode;
import android.text.TextUtils;

import nu.yona.app.api.manager.impl.AuthenticateManagerImpl;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.YonaCustomCrashManagerListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 16/03/16.
 */
public class YonaApplication extends Application {

    private static YonaApplication mContext;
    private static SharedPreferences userPreferences;
    private static EventChangeManager eventChangeManager;
    private static User user;
    private static YonaCustomCrashManagerListener yonaCustomCrashManagerListener;
    private static String serverUrl;


    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static synchronized YonaApplication getAppContext() {
        return mContext;
    }

    /**
     * Gets user preferences.
     *
     * @return the user preferences
     */
    public static synchronized SharedPreferences getUserPreferences() {
        if (userPreferences == null) {
            userPreferences = getAppContext().getSharedPreferences(PreferenceConstant.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        }
        return userPreferences;
    }

    /**
     * Gets event change manager.
     *
     * @return the event change manager
     */
    public static EventChangeManager getEventChangeManager() {
        return eventChangeManager;
    }

    /**
     * Gets yona password.
     *
     * @return return yona password
     */
    public static String getYonaPassword() {
        String yonaPwd = getUserPreferences().getString(PreferenceConstant.YONA_PASSWORD, "");
        if (TextUtils.isEmpty(yonaPwd)) {
            yonaPwd = AppUtils.getRandomString(AppConstant.YONA_PASSWORD_CHAR_LIMIT);
            getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, yonaPwd).commit();
        }
        return yonaPwd;
    }

    /**
     * Sets yona password.
     *
     * @param password yona password
     */
    public static void setYonaPassword(String password) {
        getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, password).commit();
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public static User getUser() {
        if (user == null) {
            user = new AuthenticateManagerImpl(getAppContext()).getUser();
        }
        return user;
    }

    /**
     * Update user user.
     *
     * @return the user
     */
    public static User updateUser() {
        user = new AuthenticateManagerImpl(getAppContext()).getUser();
        return user;
    }

    /**
     * Gets yona custom crash manager listener.
     *
     * @return the yona custom crash manager listener
     */
    public static YonaCustomCrashManagerListener getYonaCustomCrashManagerListener() {
        if (yonaCustomCrashManagerListener == null) {
            yonaCustomCrashManagerListener = new YonaCustomCrashManagerListener();
        }
        return yonaCustomCrashManagerListener;
    }

    /**
     * Gets server url.
     *
     * @return the server url
     */
    public static String getServerUrl() {
        if (TextUtils.isEmpty(getUserPreferences().getString(AppConstant.SERVER_URL, getAppContext().getString(R.string.blank)))) {
            getUserPreferences().edit().putString(AppConstant.SERVER_URL, YonaApplication.getAppContext().getString(R.string.server_url)).commit();
        }
        serverUrl = getUserPreferences().getString(AppConstant.SERVER_URL, getAppContext().getString(R.string.blank));
        return serverUrl;
    }

    /**
     * Sets server url.
     *
     * @param serverUrl the server url
     */
    public static void setServerUrl(String serverUrl) {
        getUserPreferences().edit().putString(AppConstant.SERVER_URL, serverUrl).commit();
        YonaApplication.serverUrl = serverUrl;
    }

    @Override
    public void onCreate() {
        if (getResources().getBoolean(R.bool.developerMode)) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDeath()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
        mContext = this;
        eventChangeManager = new EventChangeManager();

    }

}
