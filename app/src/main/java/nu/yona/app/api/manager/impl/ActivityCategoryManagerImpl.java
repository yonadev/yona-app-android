/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.dao.ActivityCategoriesDAO;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.ActivityCategoriesNetworkImpl;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class ActivityCategoryManagerImpl implements ActivityCategoryManager {

    private final ActivityCategoriesNetworkImpl activityCategoriesNetwork;
    private final ActivityCategoriesDAO activityCategoriesDAO;
    private final AuthenticateDAO authenticateDao;
    private final Context mContext;

    /**
     * Instantiates a new Activity category manager.
     *
     * @param context the context
     */
    public ActivityCategoryManagerImpl(Context context) {
        activityCategoriesNetwork = new ActivityCategoriesNetworkImpl();
        activityCategoriesDAO = new ActivityCategoriesDAO(DatabaseHelper.getInstance(context), context);
        authenticateDao = new AuthenticateDAO(context);
        this.mContext = context;
    }

    /**
     * Get list of Categories and also can get categories by Id
     *
     * @param listener
     */
    @Override
    public void getActivityCategoriesById(final DataLoadListener listener) {
        try {
            User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (user != null && user.getLinks() != null && user.getLinks().getYonaAppActivity() != null && !TextUtils.isEmpty(user.getLinks().getYonaAppActivity().getHref())) {
                activityCategoriesNetwork.getActivityCategories(authenticateDao.getUser().getLinks().getYonaAppActivity().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        updateActivityCategories(result, null);
                        if (listener != null) {
                            listener.onDataLoad(result);
                        }
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (listener != null) {
                            listener.onError(errorMessage);
                        }
                    }
                });
            } else {
                if (listener != null) {
                    listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
                }
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Get ActivityCategories from Database
     *
     * @return
     */
    @Override
    public ActivityCategories getListOfActivityCategories() {
        return activityCategoriesDAO.getActivityCategories();
    }

    /**
     * storing categories into database
     *
     * @param result
     * @param listener
     */
    private void updateActivityCategories(Object result, final DataLoadListener listener) {
        try {
            activityCategoriesDAO.saveActivityCategories(((ActivityCategories) result), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (listener != null) {
                        listener.onDataLoad(result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (listener != null) {
                        listener.onError(errorMessage);
                    }
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public void updateNetworkAPIHost(){
        activityCategoriesNetwork.updateNetworkHost();;
    }

    public void validateNewEnvironment(String url,final DataLoadListener listener) {
        try {
            activityCategoriesNetwork.getActivityCategories(url, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (listener != null) {
                        listener.onDataLoad(result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (listener != null) {
                        listener.onError(errorMessage);
                    }
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }
}
