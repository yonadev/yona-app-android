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

import nu.yona.app.api.model.AppActivity;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class ActivityNetworkImpl extends BaseImpl {

    /**
     * Gets buddy days activity.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void getDaysActivity(String url, String yonaPassword, int itemsPerPage, int pageNo, DataLoadListener listener) {
        try {
            getRestApi().getActivity(url, yonaPassword, itemsPerPage, pageNo).enqueue(getEmbeddedYonaActivity(listener));
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Gets day detail activity.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param listener     the listener
     */
    public void getDayDetailActivity(String url, String yonaPassword, DataLoadListener listener) {
        try {
            getRestApi().getDayDetailActivity(url, yonaPassword).enqueue(getDayDetailActivity(listener));
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Gets weeks activity.
     *
     * @param url          the url
     * @param password     the password
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void getWeeksActivity(String url, String password, int itemsPerPage, int pageNo, DataLoadListener listener) {
        try {
            getRestApi().getActivity(url, password, itemsPerPage, pageNo).enqueue(getEmbeddedYonaActivity(listener));
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Gets weeks detail activity.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param listener     the listener
     */
    public void getWeeksDetailActivity(String url, String yonaPassword, DataLoadListener listener) {
        try {
            getRestApi().getWeekDetailActivity(url, yonaPassword).enqueue(getweekDetailActivity(listener));
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Post app activity.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param appActivity  the app activity
     * @param listener     the listener
     */
    public void postAppActivity(String url, String yonaPassword, AppActivity appActivity, DataLoadListener listener) {
        try {
            getRestApi().postAppActivity(url, yonaPassword, appActivity).enqueue(getCall(listener));
        } catch (Exception e) {
            AppUtils.throwException(AuthenticateNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private Callback<EmbeddedYonaActivity> getEmbeddedYonaActivity(final DataLoadListener listener) {
        return new Callback<EmbeddedYonaActivity>() {
            @Override
            public void onResponse(Call<EmbeddedYonaActivity> call, Response<EmbeddedYonaActivity> response) {
                if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                    listener.onDataLoad(response.body());
                } else {
                    onError(response, listener);
                }
            }

            @Override
            public void onFailure(Call<EmbeddedYonaActivity> call, Throwable t) {
                onError(t, listener);
            }
        };
    }

    private Callback<DayActivity> getDayDetailActivity(final DataLoadListener listener) {
        return new Callback<DayActivity>() {
            @Override
            public void onResponse(Call<DayActivity> call, Response<DayActivity> response) {
                if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                    listener.onDataLoad(response.body());
                } else {
                    onError(response, listener);
                }
            }

            @Override
            public void onFailure(Call<DayActivity> call, Throwable t) {
                onError(t, listener);
            }
        };
    }

    private Callback<WeekActivity> getweekDetailActivity(final DataLoadListener listener) {
        return new Callback<WeekActivity>() {
            @Override
            public void onResponse(Call<WeekActivity> call, Response<WeekActivity> response) {
                if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                    listener.onDataLoad(response.body());
                } else {
                    onError(response, listener);
                }
            }

            @Override
            public void onFailure(Call<WeekActivity> call, Throwable t) {
                onError(t, listener);
            }
        };
    }
}
