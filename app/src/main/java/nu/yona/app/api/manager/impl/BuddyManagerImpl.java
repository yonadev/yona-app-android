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

import java.util.regex.Pattern;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.BuddyManager;
import nu.yona.app.api.manager.network.BuddyNetworkImpl;
import nu.yona.app.api.model.AddBuddy;
import nu.yona.app.api.model.Embedded;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.MobileNumberFormatter;

/**
 * Created by kinnarvasa on 28/04/16.
 */
public class BuddyManagerImpl implements BuddyManager {

    private final BuddyNetworkImpl buddyNetwork;
    private final Context mContext;

    /**
     * Instantiates a new Buddy manager.
     *
     * @param context the context
     */
    public BuddyManagerImpl(Context context) {
        buddyNetwork = new BuddyNetworkImpl();
        this.mContext = context;
    }

    /**
     * Validate user's first and last name
     *
     * @return true if first name and last name are correct.
     */
    @Override
    public boolean validateText(String string) {
        // do validation for first name and last name
        return !TextUtils.isEmpty(string);
    }

    @Override
    public boolean validateEmail(String email) {
        return !TextUtils.isEmpty(toString()) && (Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")).matcher(email).matches();
    }

    /**
     * @param mobileNumber
     * @return true if number is in expected format
     */
    @Override
    public boolean validateMobileNumber(String mobileNumber) {
        // do validation for mobile number
        if (TextUtils.isEmpty(mobileNumber)) { // 9 digits of mobile number and '+31'
            return false;
        }
        String phonenumber = mobileNumber.replace(" ", "");
        return android.util.Patterns.PHONE.matcher(phonenumber).matches() && phonenumber.length() >= AppConstant.MOBILE_NUMBER_LENGTH;
    }

    /**
     * To get buddies list from server
     *
     * @param listener
     */
    @Override
    public void getBuddies(final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaBuddies().getLinks().getSelf().getHref())) {
                buddyNetwork.getBuddies(YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaBuddies().getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(),
                        new DataLoadListener() {
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
            AppUtils.throwException(BuddyManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * @param firstName    First Name of Friend
     * @param lastName     last name of friend
     * @param email        email address
     * @param mobileNumber mobile number
     * @param listener
     */
    @Override
    public void addBuddy(String firstName, String lastName, String email, String mobileNumber, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaBuddies().getLinks().getSelf().getHref())) {
                buddyNetwork.addBuddy(YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaBuddies().getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(),
                        getBuddy(firstName, lastName, email, mobileNumber), new DataLoadListener() {
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
            AppUtils.throwException(BuddyManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private AddBuddy getBuddy(String firstName, String lastName, String email, String mobileNumber) {
        String number = mobileNumber;
        String phonenumber = number.replace(" ", "");
        AddBuddy addBuddy = new AddBuddy();
        String status = StatusEnum.REQUESTED.getStatus();
        addBuddy.setSendingStatus(status);
        addBuddy.setReceivingStatus(status);
        addBuddy.setMessage("");
        Embedded embedded = new Embedded();
        RegisterUser user = new RegisterUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailAddress(email);
        user.setMobileNumber(phonenumber);
        embedded.setYonaUser(user);
        addBuddy.setEmbedded(embedded);
        return addBuddy;
    }

    /**
     * @param buddy    Yona Buddy
     * @param listener
     */
    @Override
    public void deleteBuddy(YonaBuddy buddy, final DataLoadListener listener) {
        try {
            if (buddy != null && !TextUtils.isEmpty(buddy.getLinks().getSelf().getHref())) {
                buddyNetwork.deleteBuddy(buddy.getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        APIManager.getInstance().getAuthenticateManager().getUserFromServer();
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
            AppUtils.throwException(BuddyManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }
}
