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

import java.io.IOException;
import java.lang.annotation.Annotation;

import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
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
                    listener.onDataLoad(response.body());
                } else {
                    try {
                        Converter<ResponseBody, ErrorMessage> errorConverter =
                                getRetrofit().responseBodyConverter(ErrorMessage.class, new Annotation[0]);
                        ErrorMessage errorMessage = errorConverter.convert(response.errorBody());
                        listener.onError(errorMessage);
                    } catch (IOException e) {
                        listener.onError(new ErrorMessage(e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivityCategories> call, Throwable t) {
                listener.onError(new ErrorMessage(t.getMessage()));
            }
        });

    }
}
