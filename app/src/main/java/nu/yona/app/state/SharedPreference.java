/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import nu.yona.app.YonaApplication;
import nu.yona.app.security.DecryptUser;
import nu.yona.app.security.EncryptUser;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 29/06/16.
 */

public class SharedPreference {
    private SharedPreferences userPreferences;
    private String yonaPwd = null;


    /**
     * Gets user preferences.
     *
     * @return the user preferences
     */
    public synchronized SharedPreferences getUserPreferences() {
        if (userPreferences == null) {
            userPreferences = YonaApplication.getAppContext().getSharedPreferences(PreferenceConstant.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        }
        return userPreferences;
    }

    /**
     * Gets yona password.
     *
     * @return return yona password
     */
    public String getYonaPassword() {
        if (yonaPwd == null) {
            yonaPwd = getUserPreferences().getString(PreferenceConstant.YONA_PASSWORD, "");
            if (TextUtils.isEmpty(yonaPwd)) {
                setYonaPassword(AppUtils.getRandomString(AppConstant.YONA_PASSWORD_CHAR_LIMIT));
                getYonaPassword();
            } else if (yonaPwd.length() > AppConstant.YONA_PASSWORD_CHAR_LIMIT) {
                yonaPwd = new DecryptUser().decryptString(yonaPwd);
            }
        }
        return yonaPwd;
    }

    /**
     * Sets yona password.
     *
     * @param password yona password
     */
    public void setYonaPassword(String password) {
        if (TextUtils.isEmpty(getUserPreferences().getString(PreferenceConstant.YONA_PASSWORD, ""))) {
            try {
                getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, new EncryptUser().encryptString(password)).commit();
            } catch (Exception e) {
                getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, password).commit();
            }
        }
    }

}
