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

import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class AuthenticateNetworkImpl extends BaseImpl {

    public void registerUser(String password, RegisterUser object, final DataLoadListener listener) {
        getRestApi().registerUser(password, object).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
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
            public void onFailure(Call<User> call, Throwable t) {
                listener.onError(new ErrorMessage(t.getMessage()));
            }
        });
    }

    public void verifyMobileNumber(String password, String url, OTPVerficationCode otp, final DataLoadListener listener) {
        try {
            getRestApi().verifyMobileNumber(url, password, otp).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                        listener.onDataLoad(response.body());
                    } else {
                        try {
                            Converter<ResponseBody, ErrorMessage> errorConverter =
                                    getRetrofit().responseBodyConverter(ErrorMessage.class, new Annotation[0]);
                            listener.onError(errorConverter.convert(response.errorBody()));
                        } catch (IOException e) {
                            listener.onError(new ErrorMessage(e.getMessage()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    listener.onError(new ErrorMessage(t.getMessage()));
                }
            });
        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                listener.onError(new ErrorMessage(e.getMessage()));
            }
        }
    }

    public void resendOTP(String url, String password, final DataLoadListener listener) {
        try {
            getRestApi().resendOTP(url, password).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                        listener.onDataLoad(response.body());
                    } else {
                        try {
                            Converter<ResponseBody, ErrorMessage> errorConverter =
                                    getRetrofit().responseBodyConverter(ErrorMessage.class, new Annotation[0]);
                            listener.onError(errorConverter.convert(response.errorBody()));
                        } catch (IOException e) {
                            listener.onError(new ErrorMessage(e.getMessage()));
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    listener.onError(new ErrorMessage(t.getMessage()));
                }
            });
        } catch (Exception e) {

        }
    }
}
