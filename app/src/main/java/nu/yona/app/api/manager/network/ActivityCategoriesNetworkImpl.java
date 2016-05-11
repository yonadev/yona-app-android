/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.network;

import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.listener.DataLoadListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 14/04/16.
 */
public class ActivityCategoriesNetworkImpl extends BaseImpl {

    /**
     * Gets activity categories.
     *
     * @param url      the url
     * @param listener the listener
     */
    public void getActivityCategories(String url, final DataLoadListener listener) {
        getRestApi().getActivityCategories().enqueue(new Callback<ActivityCategories>() {
            @Override
            public void onResponse(Call<ActivityCategories> call, Response<ActivityCategories> response) {
                if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                    if (listener != null) {
                        listener.onDataLoad(response.body());
                    }
                } else {
                    onError(response, listener);
                }
            }

            @Override
            public void onFailure(Call<ActivityCategories> call, Throwable t) {
                onError(t, listener);
            }
        });

    }
}
