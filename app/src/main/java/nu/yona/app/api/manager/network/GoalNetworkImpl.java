/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.io.IOException;
import java.lang.annotation.Annotation;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.listener.DataLoadListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by bhargavsuthar on 15/04/16.
 */
public class GoalNetworkImpl extends BaseImpl {

    public void getUserGoals(String url, DataLoadListener listener) {
        getRestApi().getUserGoals(url, YonaApplication.getYonaPassword()).enqueue(getGoals(listener));

    }

    public void putUserBudgetGoals(String url, String yonaPassword, PostBudgetYonaGoal goal, DataLoadListener listener) {
        getRestApi().putUserGoals(url, yonaPassword, goal).enqueue(getGoals(listener));
    }

    public void putUserTimeZoneGoals(String url, String yonaPassword, PostTimeZoneYonaGoal goal, DataLoadListener listener) {
        getRestApi().putUserGoals(url, yonaPassword, goal).enqueue(getGoals(listener));
    }

    public void deleteGoal(String url, String yonaPassword, DataLoadListener listener) {
        getRestApi().deleteUserGoal(url, yonaPassword).enqueue(getCall(listener));
    }

    private Callback<Goals> getGoals(final DataLoadListener listener) {
        return new Callback<Goals>() {
            @Override
            public void onResponse(Call<Goals> call, Response<Goals> response) {
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
            public void onFailure(Call<Goals> call, Throwable t) {
                listener.onError(new ErrorMessage(t.getMessage()));
            }
        };
    }
}
