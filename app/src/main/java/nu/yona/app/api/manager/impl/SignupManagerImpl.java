/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.SignupManager;
import nu.yona.app.api.manager.dao.SignupDAO;
import nu.yona.app.api.manager.network.SignupNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.PreferenceConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class SignupManagerImpl implements SignupManager {

    private SignupDAO signupDAO;
    private SignupNetworkImpl signupNetwork;

    public SignupManagerImpl(SQLiteOpenHelper openHelper, Context context) {
        signupDAO = new SignupDAO(openHelper, context);
        signupNetwork = new SignupNetworkImpl();
    }

    /**
     * Validate user's first and last name
     *
     * @return true if first name and last name are correct.
     */
    public boolean validateText(String string) {
        // do validation for first name and last name
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        return true;
    }

    /**
     * @param mobileNumber user's mobile number
     * @return true if number is in expected format
     */
    public boolean validateMobileNumber(String mobileNumber) {
        // do validation for mobile number
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 12) { // 9 digits of mobile number and '+31'
            return false;
        }
        return true;
    }

    /**
     * This will register user on server
     */
    public void registerUser(RegisterUser registerUser, final DataLoadListener listener) {
        // do registration of user on server and save response in database.
        try {
            String yonaPwd = YonaApplication.getAppContext().getUserPreferences().getString(PreferenceConstant.YONA_PASSWORD, "");
            if (TextUtils.isEmpty(yonaPwd)) {
                yonaPwd = UUID.randomUUID().toString();
                YonaApplication.getAppContext().getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSWORD, yonaPwd).commit();
            }
            signupNetwork.registerUser(yonaPwd, registerUser, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    listener.onDataLoad((User)result);
                }

                @Override
                public void onError(Object errorMessage) {
                    listener.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }

    /**
     * This will get response of server in case of register successful and store it in database, update on UI after that via listener.
     *
     * @param result
     * @param listener
     */
    private void updateDataForRegisterUser(Object result, final DataLoadListener listener) {
        signupDAO.updateDataForRegisterUser(result, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                listener.onDataLoad(result);
            }

            @Override
            public void onError(Object errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }
}
