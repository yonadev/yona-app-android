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
import android.os.Build;
import android.text.TextUtils;

import java.util.Arrays;

import javax.crypto.spec.IvParameterSpec;

import nu.yona.app.YonaApplication;
import nu.yona.app.security.MyCipher;
import nu.yona.app.security.MyCipherData;
import nu.yona.app.security.PRNGFixes;
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
            yonaPwd = getUserPreferences().getString(PreferenceConstant.YONA_DATA, "");
            if (TextUtils.isEmpty(yonaPwd)) {
                MyCipherData myCipherData = generateKey(AppUtils.getRandomString(AppConstant.YONA_PASSWORD_CHAR_LIMIT));
                if (null != myCipherData) {
                    yonaPwd = getDecryptedKey();
                }
            } else {
                yonaPwd = getDecryptedKey();
            }
        }
        return yonaPwd;
    }

    private MyCipherData generateKey(String key) {
        if (TextUtils.isEmpty(YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword())) {
            //According to http://android-developers.blogspot.com.es/2013/08/some-securerandom-thoughts.html
            try {
                PRNGFixes.apply();
                MyCipherData cipherData = new MyCipher(Build.SERIAL).encryptUTF8(key);
                userPreferences.edit().putString(PreferenceConstant.YONA_DATA, Arrays.toString(cipherData.getData())).putString(PreferenceConstant.YONA_IV, Arrays.toString(cipherData.getIV())).commit();
                return cipherData;
            } catch (Exception e) {
                AppUtils.throwException(SharedPreference.class.getSimpleName(), e, Thread.currentThread(), null);
            }
        }
        return null;
    }

    private String getDecryptedKey() {
        byte[] encrypted_data = byteToString(userPreferences.getString(PreferenceConstant.YONA_DATA, ""));
        byte[] dataIV = byteToString(userPreferences.getString(PreferenceConstant.YONA_IV, ""));
        IvParameterSpec iv = new IvParameterSpec(dataIV);
        return new MyCipher(Build.SERIAL).decryptUTF8(encrypted_data, iv);
    }

    /**
     * Sets yona password.
     *
     * @param password yona password
     */
    public void setYonaPassword(String password, boolean override) {
        yonaPwd = null;
        if (TextUtils.isEmpty(YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword()) || override) {
            //According to http://android-developers.blogspot.com.es/2013/08/some-securerandom-thoughts.html
            try {
                PRNGFixes.apply();
                MyCipherData cipherData = new MyCipher(Build.SERIAL).encryptUTF8(password);
                userPreferences.edit().putString(PreferenceConstant.YONA_DATA, Arrays.toString(cipherData.getData())).putString(PreferenceConstant.YONA_IV, Arrays.toString(cipherData.getIV())).commit();
            } catch (Exception e) {
                AppUtils.throwException(SharedPreference.class.getSimpleName(), e, Thread.currentThread(), null);
            }
        }
    }

    private byte[] byteToString(String response) {
        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] encrypted_data = new byte[byteValues.length];

        for (int i = 0, len = encrypted_data.length; i < len; i++) {
            encrypted_data[i] = Byte.parseByte(byteValues[i].trim());
        }
        return encrypted_data;
    }
}
