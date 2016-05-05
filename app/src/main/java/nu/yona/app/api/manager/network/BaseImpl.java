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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.utils.NetworkUtils;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kinnarvasa on 28/03/16.
 */
class BaseImpl {
    private final Cache cache;
    private final Interceptor getInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            chain.request().newBuilder().addHeader(NetworkConstant.ACCEPT_LAUNGUAGE, Locale.getDefault().toString().replace('_', '-'));
            chain.request().newBuilder().addHeader(NetworkConstant.CONTENT_TYPE, "application/json");
            int maxStale = 60 * 60 * 24 * 28;
            if (NetworkUtils.isOnline(YonaApplication.getAppContext())) {
                chain.request().newBuilder().addHeader("Cache-Control", "only-if-cached").build();
            } else if (request.method().equalsIgnoreCase("GET") && !request.cacheControl().noCache()) {
                chain.request().newBuilder().addHeader("Cache-Control", "public, max-stale=" + maxStale).build();
            }
            request = request.newBuilder().build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .build();
        }
    };
    private Retrofit retrofit;
    private RestApi restApi;
    private File httpCacheDirectory;

    /**
     * Instantiates a new Base.
     */
    BaseImpl() {
        try {
            httpCacheDirectory = new File(YonaApplication.getAppContext().getCacheDir(), NetworkConstant.CACHING_FILE);
        } catch (Exception e) {
            AppUtils.throwException(BaseImpl.class.getSimpleName(), e, Thread.currentThread(), null);
        }
        int cacheSize = 10 * 1024 * 1024;
        cache = new Cache(httpCacheDirectory, cacheSize);
    }

    /**
     * Gets retrofit.
     *
     * @return the retrofit
     */
    Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(YonaApplication.getServerUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(gethttpClient())
                    .build();
        }
        return retrofit;
    }

    private OkHttpClient gethttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(NetworkConstant.API_CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(NetworkConstant.API_WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(NetworkConstant.API_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(getInterceptor)
                .cache(cache)
                .build();
    }

    /**
     * Gets rest api.
     *
     * @return the rest api
     */
    RestApi getRestApi() {
        if (restApi == null) {
            restApi = getRetrofit().create(RestApi.class);
        }
        return restApi;
    }

    /**
     * Gets call.
     *
     * @param listener the listener
     * @return the call
     */
    Callback getCall(final DataLoadListener listener) {
        return new Callback() {
            @Override
            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
                if (response.code() < NetworkConstant.RESPONSE_STATUS) {
                    listener.onDataLoad(response.body());
                } else {
                    onError(response, listener);
                }
            }

            @Override
            public void onFailure(retrofit2.Call call, Throwable t) {
                onError(t, listener);
            }
        };
    }

    void onError(Throwable t, DataLoadListener listener) {
        if (listener != null) {
            if (t instanceof ConnectException) {
                listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.connectionnotavailable)));
            } else {
                listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.somethingwentwrong)));
            }
        }
    }

    void onError(retrofit2.Response response, DataLoadListener listener) {
        if (listener != null) {
            if (response.code() == NetworkConstant.RESPONSE_ERROR_CODE) {
                try {
                    Converter<ResponseBody, ErrorMessage> errorConverter =
                            getRetrofit().responseBodyConverter(ErrorMessage.class, new Annotation[0]);
                    listener.onError(errorConverter.convert(response.errorBody()));
                } catch (IOException e) {
                    listener.onError(new ErrorMessage(e.getMessage()));
                }
            } else {
                listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.somethingwentwrong)));
            }
        }
    }
}
