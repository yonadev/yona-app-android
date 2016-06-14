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
import android.content.SharedPreferences;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.AuthenticateNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class AuthenticateManagerImpl implements AuthenticateManager {

    private final AuthenticateDAO authenticateDao;
    private final AuthenticateNetworkImpl authNetwork;
    private final Context mContext;

    /**
     * Instantiates a new Authenticate manager.
     *
     * @param context the context
     */
    public AuthenticateManagerImpl(Context context) {
        authenticateDao = new AuthenticateDAO(context);
        authNetwork = new AuthenticateNetworkImpl();
        this.mContext = context;
    }

    /**
     * Validate user's first and last name
     *
     * @return true if first name and last name are correct.
     */
    public boolean validateText(String string) {
        // do validation for first name and last name
        return !TextUtils.isEmpty(string);
    }

    /**
     * @param mobileNumber user's mobile number
     * @return true if number is in expected format
     */
    public boolean validateMobileNumber(String mobileNumber) {
        // do validation for mobile number
        // 9 digits of mobile number and '+31'
        return !(TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != AppConstant.MOBILE_NUMBER_LENGTH) && android.util.Patterns.PHONE.matcher(mobileNumber).matches();
    }

    /**
     * @param registerUser RegisterUser object
     * @param listener
     */
    @Override
    public void registerUser(RegisterUser registerUser, boolean isEditMode, final DataLoadListener listener) {
        try {
            String url = null;
            if (isEditMode) {
                url = YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getEdit().getHref();
            }
            authNetwork.registerUser(url, YonaApplication.getYonaPassword(), registerUser, isEditMode, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_REGISTER, true).commit();
                    updateDataForRegisterUser(result, listener);
                }

                @Override
                public void onError(Object errorMessage) {
                    listener.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
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
                YonaApplication.getEventChangeManager().getDataState().updateUser();
                if (listener != null) {
                    listener.onDataLoad(result);
                }
            }

            @Override
            public void onError(Object errorMessage) {
                if (listener != null) {
                    if (errorMessage instanceof ErrorMessage) {
                        listener.onError(errorMessage);
                    } else {
                        listener.onError(new ErrorMessage(errorMessage.toString()));
                    }
                }
            }
        });
    }

    /**
     * @param user     Register User object
     * @param otp      OTP - Sms received value
     * @param listener
     */
    @Override
    public void verifyOTP(RegisterUser user, String otp, final DataLoadListener listener) {
        try {
            if (user == null) {
                verifyOTP(otp, listener);
            } else {
                authNetwork.registerUserOverride(YonaApplication.getYonaPassword(), user, otp, new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        SharedPreferences.Editor editor = YonaApplication.getUserPreferences().edit();
                        editor.putBoolean(PreferenceConstant.STEP_REGISTER, true);
                        editor.putBoolean(PreferenceConstant.STEP_OTP, true);
                        editor.commit();
                        updateDataForRegisterUser(result, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        listener.onError(errorMessage);
                    }
                });
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public void verifyOTP(final String otp, final DataLoadListener listener) {
        try {
            if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks() != null
                    && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf() != null && !TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref())) {
                getUser(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        verifyOTPAfterUser(otp, listener);
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
                verifyOTPAfterUser(otp, listener);
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Verify otp after user.
     *
     * @param otp      OTP received in sms
     * @param listener the listener
     */
    public void verifyOTPAfterUser(String otp, final DataLoadListener listener) {
        try {
            if (otp.length() == AppConstant.OTP_LENGTH) {
                if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
                    if (authenticateDao.getUser() != null && authenticateDao.getUser().getLinks() != null
                            && authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber() != null
                            && !TextUtils.isEmpty(authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber().getHref())) {
                        authNetwork.verifyMobileNumber(YonaApplication.getYonaPassword(), authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber().getHref(),
                                new OTPVerficationCode(otp), new DataLoadListener() {

                                    @Override
                                    public void onDataLoad(Object result) {
                                        updateDataForRegisterUser(result, listener);
                                        YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_OTP, true).commit();
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
                        listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
                    }
                } else {
                    if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getVerifyPinReset().getHref())) {
                        authNetwork.doVerifyPin(authenticateDao.getUser().getLinks().getVerifyPinReset().getHref(), otp, new DataLoadListener() {
                            @Override
                            public void onDataLoad(Object result) {
                                updatePreferenceForPinReset();
                                authNetwork.doClearPin(authenticateDao.getUser().getLinks().getClearPinReset().getHref());
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
                    } else {
                        listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
                    }
                }
            } else {
                listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.invalidotp)));
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void updatePreferenceForPinReset() {
        SharedPreferences.Editor editor = YonaApplication.getUserPreferences().edit();
        editor.putBoolean(PreferenceConstant.USER_BLOCKED, false);
        editor.putBoolean(PreferenceConstant.STEP_OTP, true);
        editor.putBoolean(PreferenceConstant.STEP_PASSCODE, false);
        editor.commit();
    }

    @Override
    public void requestPinReset(final DataLoadListener listener) {
        try {
            storedPassCode("");
            YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_OTP, false).commit();
            if (!TextUtils.isEmpty(authenticateDao.getUser().getLinks().getRequestPinReset().getHref())) {
                authNetwork.doPasscodeReset(authenticateDao.getUser().getLinks().getRequestPinReset().getHref(), YonaApplication.getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        getUser(result, listener);
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
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void getUser(final Object object, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(getUser().getLinks().getSelf().getHref())) {
                getUser(getUser().getLinks().getSelf().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        listener.onDataLoad(object);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        listener.onDataLoad(object); // because we want to carry forward object to UI part, we don't worry here about user update.
                    }
                });
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * @param url      url to fetch user
     * @param listener
     */
    @Override
    public void getUser(final String url, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                authNetwork.getUser(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        updateDataForRegisterUser(result, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (listener != null) {
                            if (errorMessage instanceof ErrorMessage) {
                                listener.onError(errorMessage);
                            } else {
                                listener.onError(new ErrorMessage(errorMessage.toString()));
                            }
                        }
                    }
                });
            } else {
                if (listener != null) {
                    listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
                }
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }

    }

    @Override
    public void getFriendProfile(final String url, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                authNetwork.getUser(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        listener.onDataLoad(result);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (listener != null) {
                            if (errorMessage instanceof ErrorMessage) {
                                listener.onError(errorMessage);
                            } else {
                                listener.onError(new ErrorMessage(errorMessage.toString()));
                            }
                        }
                    }
                });
            } else {
                if (listener != null) {
                    listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
                }
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    @Override
    public void resendOTP(final DataLoadListener listener) {
        try {
            if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks() != null
                    && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf() != null && !TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref())) {
                getUser(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        resendOTPAfterUser(listener);
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
                resendOTPAfterUser(listener);
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void resendOTPAfterUser(final DataLoadListener listener) {
        try {
            if (authenticateDao.getUser() != null && authenticateDao.getUser().getLinks() != null
                    && authenticateDao.getUser().getLinks().getResendMobileNumberConfirmationCode() != null
                    && !TextUtils.isEmpty(authenticateDao.getUser().getLinks().getResendMobileNumberConfirmationCode().getHref())) {
                authNetwork.resendOTP(authenticateDao.getUser().getLinks().getResendMobileNumberConfirmationCode().getHref(), YonaApplication.getYonaPassword(), new DataLoadListener() {
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
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    @Override
    public void requestUserOverride(String mobileNumber, final DataLoadListener listener) {
        try {
            authNetwork.requestUserOverride(mobileNumber, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks() != null
                            && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf() != null
                            && !TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref())) {
                        getUser(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref(), listener);
                    } else {
                        listener.onDataLoad(result);
                    }
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
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public User getUser() {
        return authenticateDao.getUser();
    }

    public void getUserFromServer() {
        if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf() != null) {
            getUser(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {

                }

                @Override
                public void onError(Object errorMessage) {

                }
            });
        }
    }

    /**
     * @param listener
     */
    @Override
    public void deleteUser(final DataLoadListener listener) {
        authNetwork.deleteUser(authenticateDao.getUser().getLinks().getEdit().getHref(), YonaApplication.getYonaPassword(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                SharedPreferences.Editor editor = YonaApplication.getUserPreferences().edit();
                editor.clear();
                editor.putBoolean(PreferenceConstant.STEP_TOUR, true);
                editor.commit();
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
     * Stored User passcode into pref
     *
     * @param code
     */
    private void storedPassCode(String code) {
        SharedPreferences.Editor yonaPref = YonaApplication.getUserPreferences().edit();
        yonaPref.putString(PreferenceConstant.YONA_PASSCODE, code); // remove user's passcode from device.
        yonaPref.putBoolean(PreferenceConstant.STEP_PASSCODE, true);
        yonaPref.commit();
    }
}
