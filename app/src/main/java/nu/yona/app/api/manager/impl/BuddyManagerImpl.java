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
import nu.yona.app.api.manager.BuddyManager;
import nu.yona.app.api.manager.network.BuddyNetworkImpl;
import nu.yona.app.api.model.AddBuddy;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 28/04/16.
 */
public class BuddyManagerImpl implements BuddyManager {

    private BuddyNetworkImpl buddyNetwork;
    private Context mContext;

    public BuddyManagerImpl(Context context) {
        buddyNetwork = new BuddyNetworkImpl();
        this.mContext = context;
    }

    /**
     * To get buddies list from server
     *
     * @param listener
     */
    @Override
    public void getBuddies(final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(YonaApplication.getUser().getEmbedded().getYonaBuddies().get(0).getLinks().getSelf().getHref())) {
                buddyNetwork.getBuddies(YonaApplication.getUser().getEmbedded().getYonaBuddies().get(0).getLinks().getSelf().getHref(), YonaApplication.getYonaPassword(),
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
     * Add Buddy
     *
     * @param buddy    AddBuddy object
     * @param listener
     */
    @Override
    public void addBuddy(AddBuddy buddy, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(YonaApplication.getUser().getEmbedded().getYonaBuddies().get(0).getLinks().getSelf().getHref())) {
                buddyNetwork.addBuddy(YonaApplication.getUser().getEmbedded().getYonaBuddies().get(0).getLinks().getSelf().getHref(), YonaApplication.getYonaPassword(),
                        buddy, new DataLoadListener() {
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
     * @param buddy    Yona Buddy
     * @param listener
     */
    @Override
    public void deleteBuddy(YonaBuddy buddy, final DataLoadListener listener) {
        try {
            if (buddy != null && !TextUtils.isEmpty(buddy.getLinks().getSelf().getHref())) {
                buddyNetwork.deleteBuddy(buddy.getLinks().getSelf().getHref(), YonaApplication.getYonaPassword(), new DataLoadListener() {
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
}
