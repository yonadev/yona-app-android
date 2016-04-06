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
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.AuthenticateNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class AuthenticateManagerImpl implements AuthenticateManager {

    private AuthenticateDAO authenticateDao;
    private AuthenticateNetworkImpl authNetwork;

    public AuthenticateManagerImpl(Context context) {
        authenticateDao = new AuthenticateDAO(DatabaseHelper.getInstance(context), context);
        authNetwork = new AuthenticateNetworkImpl();
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
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != AppConstant.MOBILE_NUMBER_LENGTH) { // 9 digits of mobile number and '+31'
            return false;
        }
        if (!android.util.Patterns.PHONE.matcher(mobileNumber).matches()) {
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

            authNetwork.registerUser(YonaApplication.getYonaPassword(), registerUser, new DataLoadListener() {
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
            listener.onError(new ErrorMessage(e.getMessage()));
        }
    }

    /**
     * This will get response of server in case of register successful and store it in database, update on UI after that via listener.
     *
     * @param result
     * @param listener
     */
    private void updateDataForRegisterUser(Object result, final DataLoadListener listener) {
        authenticateDao.updateDataForRegisterUser(result, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                listener.onDataLoad(result);
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }

    /**
     * @param otp      OTP received in sms
     * @param listener
     */
    public void verifyMobileNumber(String otp, final DataLoadListener listener) {
        if (otp.length() == AppConstant.OTP_LENGTH) {
            authNetwork.verifyMobileNumber(YonaApplication.getYonaPassword(), authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber().getHref(),
                    new OTPVerficationCode(otp), new DataLoadListener() {

                        @Override
                        public void onDataLoad(Object result) {
                            updateDataForRegisterUser(result, listener);
                        }

                        @Override
                        public void onError(Object errorMessage) {
                            if (errorMessage instanceof ErrorMessage) {
                                listener.onError(errorMessage);
                            } else {
                                listener.onError(new ErrorMessage(errorMessage.toString()));
                            }
                        }
                    });
        } else {
            listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.invalid_otp)));
        }
    }

    public void resendOTP(DataLoadListener listener){
        // do API implementation for resend OTP
    }
}
