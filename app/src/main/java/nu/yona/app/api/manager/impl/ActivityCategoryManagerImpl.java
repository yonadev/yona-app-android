/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;

import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.dao.ActivityCategoriesDAO;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.ActivityCategoriesNetworkImpl;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class ActivityCategoryManagerImpl implements ActivityCategoryManager {

    private ActivityCategoriesNetworkImpl activityCategoriesNetwork;
    private ActivityCategoriesDAO activityCategoriesDAO;
    private AuthenticateDAO authenticateDao;

    public ActivityCategoryManagerImpl(Context context) {
        activityCategoriesNetwork = new ActivityCategoriesNetworkImpl();
        activityCategoriesDAO = new ActivityCategoriesDAO(DatabaseHelper.getInstance(context), context);
        authenticateDao = new AuthenticateDAO(DatabaseHelper.getInstance(context), context);
    }

    /**
     * Get list of Categories and also can get categories by Id
     *
     * @param listener
     */
    @Override
    public void getActivityCategoriesById(final DataLoadListener listener) {
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
    }

}
