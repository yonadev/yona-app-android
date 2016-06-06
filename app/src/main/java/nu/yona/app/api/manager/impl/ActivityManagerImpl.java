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
import nu.yona.app.api.manager.ActivityManager;
import nu.yona.app.api.manager.network.ActivityNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class ActivityManagerImpl implements ActivityManager {

    private final ActivityNetworkImpl activityNetwork;
    private final Context mContext;

    /**
     * Instantiates a new Activity manager.
     *
     * @param context the context
     */
    public ActivityManagerImpl(Context context) {
        activityNetwork = new ActivityNetworkImpl();
        mContext = context;
    }

    @Override
    public void getDaysActivity(int itemsPerPage, int pageNo, DataLoadListener listener) {
        if (YonaApplication.getUser() != null && YonaApplication.getUser().getLinks() != null
                && YonaApplication.getUser().getLinks().getYonaDailyActivityReports() != null
                && !TextUtils.isEmpty(YonaApplication.getUser().getLinks().getYonaDailyActivityReports().getHref())) {
            getDailyActivity(YonaApplication.getUser().getLinks().getYonaDailyActivityReports().getHref(), itemsPerPage, pageNo, listener);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
        }
    }

    @Override
    public void getBuddyDaysActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                getDailyActivity(url, itemsPerPage, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }


    @Override
    public void getDayDetailActivity(String url, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getDayDetailActivity(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
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
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    @Override
    public void getWeeksActivity(int itemsPerPage, int pageNo, DataLoadListener listener) {
        if (YonaApplication.getUser() != null && YonaApplication.getUser().getLinks() != null
                && YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports() != null
                && !TextUtils.isEmpty(YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports().getHref())) {
            getWeeksActivity(YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports().getHref(), itemsPerPage, pageNo, listener);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
        }
    }

    @Override
    public void getBuddyWeeksActivity(String url, int itemsPerPage, int pageNo, DataLoadListener listener) {
        getWeeksActivity(url, itemsPerPage, pageNo, listener);
    }

    @Override
    public void getWeeksDetailActivity(String url, final DataLoadListener listener) {
        activityNetwork.getWeeksDetailActivity(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
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

    private void getWeeksActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getWeeksActivity(url, YonaApplication.getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
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
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void getDailyActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            activityNetwork.getBuddyDaysActivity(url, YonaApplication.getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
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
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }
}
