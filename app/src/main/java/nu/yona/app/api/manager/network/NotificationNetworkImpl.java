/*
 *  Copyright (c) 2016, 2018 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public class NotificationNetworkImpl extends BaseImpl {


    /**
     * Gets message.
     *
     * @param nextURL          the url
     * @param yonaPassword the yona password
     * @param listener     the listener
     */
    public void getNextSetOfMessagesFromURL(String nextURL, String yonaPassword, boolean isUnreadStatus, DataLoadListener listener) {
        try {
            getRestApi().getMessages(nextURL, yonaPassword, Locale.getDefault().toString().replace('_', '-'), isUnreadStatus).enqueue(new Callback<YonaMessages>() {
                @Override
                public void onResponse(Call<YonaMessages> call, Response<YonaMessages> response) {
                    if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                        listener.onDataLoad(response.body());
                    } else {
                        onError(response, listener);
                    }
                }

                @Override
                public void onFailure(Call<YonaMessages> call, Throwable t) {
                    onError(t, listener);
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(NotificationNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }


    /**
     * Gets message.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void getMessage(String url, String yonaPassword, boolean isUnreadStatus, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            getRestApi().getMessages(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), isUnreadStatus, itemsPerPage, pageNo).enqueue(new Callback<YonaMessages>() {
                @Override
                public void onResponse(Call<YonaMessages> call, Response<YonaMessages> response) {
                    if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                        listener.onDataLoad(response.body());
                    } else {
                        onError(response, listener);
                    }
                }

                @Override
                public void onFailure(Call<YonaMessages> call, Throwable t) {
                    onError(t, listener);
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(NotificationNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Delete message.
     *
     * @param url      the url
     * @param password the password
     * @param listener the listener
     */
    public void deleteMessage(String url, String password, DataLoadListener listener) {
        try {
            getRestApi().deleteMessage(url, password, Locale.getDefault().toString().replace('_', '-')).enqueue(getCall(listener));
        } catch (Exception e) {
            AppUtils.throwException(NotificationNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    /**
     * Post message.
     *
     * @param url      the url
     * @param password the password
     * @param body     the body
     * @param listener the listener
     */
    public void postMessage(String url, String password, MessageBody body, DataLoadListener listener) {
        try {
            getRestApi().postMessage(url, password, Locale.getDefault().toString().replace('_', '-'), body).enqueue(getCall(listener));
        } catch (Exception e) {
            AppUtils.throwException(NotificationNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public void getComments(String url, String password, final int itemsPerPage, final int pageNo, DataLoadListener listener) {
        try {
            getRestApi().getComments(url, password, Locale.getDefault().toString().replace('_', '-')).enqueue(getCall(listener));
        } catch (Exception e) {
            AppUtils.throwException(NotificationNetworkImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

}
