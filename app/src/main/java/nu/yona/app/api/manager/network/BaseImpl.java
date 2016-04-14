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

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.utils.NetworkUtils;
import nu.yona.app.listener.DataLoadListener;
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
public class BaseImpl {
    private Retrofit retrofit;
    private RestApi restApi;
    private Cache cache;
    private File httpCacheDirectory;
    private int maxStale = 60 * 60 * 24 * 28;
    private int cacheSize = 10 * 1024 * 1024;

    private Interceptor getInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            request.newBuilder().addHeader("Content-Type", "application/json");
            if (Locale.getDefault().toString().equals("nl_NL")) {
                request.newBuilder().addHeader("Content-Language", "nl-NL");
            } else {
                request.newBuilder().addHeader("Content-Language", "en-EN");
            }
            if (NetworkUtils.isOnline(YonaApplication.getAppContext())) {
                request.newBuilder().addHeader("Cache-Control", "only-if-cached").build();
            } else if (request.method().equalsIgnoreCase("GET") && !request.cacheControl().noCache()) {
                request.newBuilder().addHeader("Cache-Control", "public, max-stale=" + maxStale).build();
            }
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .build();
        }
    };

    public BaseImpl() {
        try {
            httpCacheDirectory = new File(YonaApplication.getAppContext().getCacheDir(), NetworkConstant.CACHING_FILE);
        } catch (Exception e) {
            Log.e(BaseImpl.class.getSimpleName(), e.getMessage());
        }
        cache = new Cache(httpCacheDirectory, cacheSize);
    }

    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(YonaApplication.getAppContext().getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(gethttpClient())
                    .build();
        }
        return retrofit;
    }

    private OkHttpClient gethttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(NetworkConstant.API_CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(NetworkConstant.API_WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(NetworkConstant.API_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(getInterceptor)
                .cache(cache)
                .build();
        return okHttpClient;
    }

    public RestApi getRestApi() {
        if (restApi == null) {
            restApi = getRetrofit().create(RestApi.class);
        }
        return restApi;
    }

    public Callback getCall(final DataLoadListener listener) {
        return new Callback() {
            @Override
            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
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
            public void onFailure(retrofit2.Call call, Throwable t) {
                listener.onError(new ErrorMessage(t.getMessage()));
            }
        };
    }
}
