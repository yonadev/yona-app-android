/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public interface ActivityCategoryManager {

    /**
     * Gets activity categories by id.
     *
     * @param listener the listener
     */
    void getActivityCategoriesById(DataLoadListener listener);

    /**
     * Gets list of activity categories.
     *
     * @return the list of activity categories
     */
    ActivityCategories getListOfActivityCategories();


    /**
     * Validates the new environment by requesting the activityCategoryList api. Hence the function is added in this class.
     *  @param url the new Environment URL entered by user
     * @param listener the listener
     * @return the list of activity categories
     */
     void validateNewEnvironment(String url,final DataLoadListener listener);

    /**
     *
     *
     *
     */
     void updateNetworkAPIHost();
}
