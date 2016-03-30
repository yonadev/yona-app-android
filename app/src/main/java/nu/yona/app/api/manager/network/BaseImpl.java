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
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.utils.NetworkUtils;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
            if (NetworkUtils.isOnline(YonaApplication.getAppContext())) {
                request.newBuilder().addHeader("Cache-Control", "only-if-cached").build();
            } else if(request.method().equalsIgnoreCase("GET") && !request.cacheControl().noCache()) {
                    request.newBuilder().addHeader("Cache-Control", "public, max-stale="+ maxStale).build();
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

    private Retrofit getRetrofit() {
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
}
