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

import org.json.JSONArray;
import org.json.JSONObject;

import nu.yona.app.api.ApiKeys;
import nu.yona.app.api.manager.SignupManager;
import nu.yona.app.api.manager.dao.SignupDAO;
import nu.yona.app.api.manager.network.SignupNetworkImpl;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class SignupManagerImpl implements SignupManager {

    private SignupDAO signupDAO;
    private SignupNetworkImpl signupNetwork;
    private Context mContext;

    public SignupManagerImpl(SQLiteOpenHelper openHelper, Context context) {
        signupDAO = new SignupDAO(openHelper, context);
        signupNetwork = new SignupNetworkImpl(signupDAO.getBaseUrl(), context);
        this.mContext = context;
    }

    /**
     * Validate user's first and last name
     *
     * @param firstName
     * @param lastName
     * @return true if first name and last name are correct.
     */
    public boolean validateUserName(String firstName, String lastName) {
        // do validation for first name and last name
        return true;
    }

    /**
     * @param mobileNumber user's mobile number
     * @return true if number is in expected format
     */
    public boolean validateMobileNumber(String mobileNumber) {
        // do validation for mobile number
        return true;
    }

    /**
     * This will register user on server
     *
     * @param firstName Name of user
     * @param lastName  last name of user
     * @param mobileNo  mobile number of user
     * @param nickName  nick name of user (optional)
     */
    public void registerUser(String firstName, String lastName, String mobileNo, String nickName, final DataLoadListener listener) {
        // do registration of user on server and save response in database.
        try {
            signupNetwork.registerUser(getJSONRequestObject(firstName, lastName, mobileNo, nickName), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateDataForRegisterUser(result, listener);
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

    private JSONObject getJSONRequestObject(String firstName, String lastName, String mobileNo, String nickName) throws Exception {
        JSONObject object = new JSONObject();
        object.put(ApiKeys.FIRST_NAME, firstName);
        object.put(ApiKeys.LAST_NAME, lastName);
        object.put(ApiKeys.NICK_NAME, nickName);
        object.put(ApiKeys.MOBILE_NUMBER, mobileNo);

        JSONArray array = new JSONArray();
        array.put(android.os.Build.MODEL);
        object.put(ApiKeys.DEVICES, array);

        return object;
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
