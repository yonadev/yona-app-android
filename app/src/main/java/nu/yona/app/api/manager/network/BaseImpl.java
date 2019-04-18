/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.utils.NetworkUtils;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by kinnarvasa on 28/03/16.
 */

// TODO: Revisit
class BaseImpl
{
	private final int maxStale = 60 * 60 * 24 * 28; // keep cache for 28 days.
	//    public final String localLanguage = ;
	private final Interceptor getInterceptor = chain -> {
		Request request = chain.request();
		if (NetworkUtils.isOnline(YonaApplication.getAppContext()))
		{
			chain.request().newBuilder().addHeader("Cache-Control", "only-if-cached").build();
		}
		else
		{
			throw new IllegalStateException(YonaApplication.getAppContext().getString(R.string.server_not_rechable));
		}
		chain.request().newBuilder().addHeader(NetworkConstant.CONTENT_TYPE, "application/json");
		Response response = verifyResponse(request, chain);
		return response.newBuilder()
				.header("Cache-Control", "public, max-age=" + maxStale)
				.build();
	};

	private Response verifyResponse(Request request, Interceptor.Chain chain) throws IOException
	{
		Response response = chain.proceed(request);
		if (response.priorResponse() != null && response.priorResponse().code() == HttpURLConnection.HTTP_MOVED_PERM)
		{
			throw new IllegalStateException(YonaApplication.getAppContext().getString(R.string.server_not_rechable));
		}
		else if (response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
		{
			throw new SocketTimeoutException(YonaApplication.getAppContext().getString(R.string.server_not_rechable));
		}
		request.newBuilder().build();
		return response;
	}

	// Made both variables  below as class variables to make sure all network impl classes are using same host environment serverURL.
	// If needed in future instead of using class variables we need to store all the impl instances into an array and iterate and update t
	// host serverURL in all instances when environment switch happens.

	private static Retrofit retrofit;
	private static RestApi restApi;

	/**
	 * Gets retrofit.
	 *
	 * @return the retrofit
	 */
	Retrofit getRetrofit()
	{
		if (retrofit == null)
		{
			retrofit = new Retrofit.Builder()
					.baseUrl(getSharedAppDataState().getServerUrl())
					.addConverterFactory(GsonConverterFactory.create())
					.client(gethttpClient())
					.build();
		}
		return retrofit;
	}

	/**
	 * Reinitialize retrofit.
	 */
	protected void reinitializeAPI()
	{
		retrofit = null; // this method is require when user do signout and want to change environment, it should update with new environemnt.
		restApi = null;
	}

	/**
	 * Reinitialize retrofit.
	 */
	protected void reinitializeRetrofit()
	{
		retrofit = null; // this method is require when user do signout and want to change environment, it should update with new environemnt.
	}

	private OkHttpClient gethttpClient()
	{
		return new OkHttpClient.Builder()
				.connectTimeout(NetworkConstant.API_CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
				.writeTimeout(NetworkConstant.API_WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
				.readTimeout(NetworkConstant.API_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
				.retryOnConnectionFailure(false)
				.addInterceptor(getInterceptor)
				.build();
	}

	/**
	 * Gets rest api.
	 *
	 * @return the rest api
	 */
	RestApi getRestApi()
	{
		if (restApi == null)
		{
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
	Callback getCall(final DataLoadListener listener)
	{
		return new Callback()
		{
			@Override
			public void onResponse(retrofit2.Call call, retrofit2.Response response)
			{
				if (response.code() < NetworkConstant.RESPONSE_STATUS && listener != null)
				{
					listener.onDataLoad(response.body());
				}
				else
				{
					onError(response, listener);
				}
			}

			@Override
			public void onFailure(retrofit2.Call call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}

	/**
	 * Gets call.
	 *
	 * @param listener the listener
	 * @return the call
	 */
	Callback createCallBack(final DataLoadListenerImpl listener)
	{
		return new Callback()
		{
			@Override
			public void onResponse(retrofit2.Call call, retrofit2.Response response)
			{
				if (response.code() < NetworkConstant.RESPONSE_STATUS && listener != null)
				{
					listener.onDataLoad(response.body());
				}
				else
				{
					onError(response, listener);
				}
			}

			@Override
			public void onFailure(retrofit2.Call call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}

	/**
	 * On error.
	 *
	 * @param t        the t
	 * @param listener the listener
	 */
	void onError(Throwable t, DataLoadListener listener)
	{
		if (listener != null)
		{
			AppUtils.reportException(BaseImpl.class, new Exception(t), Thread.currentThread(), null, false);
			if (t instanceof ConnectException || t instanceof SocketTimeoutException || t instanceof UnknownHostException || t instanceof IllegalStateException)
			{
				// If client causing problem.
				if (!AppUtils.isNetworkAvailable(YonaApplication.getAppContext()))
				{
					listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.connectionnotavailable)));
				}
				else
				{
					listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.server_not_rechable)));
				}
			}
			else
			{
				listener.onError(t.getMessage());
			}
		}
	}

	/**
	 * On error.
	 *
	 * @param response the response
	 * @param listener the listener
	 */
	void onError(retrofit2.Response response, DataLoadListener listener)
	{
		if (listener != null)
		{
			try
			{
				Converter<ResponseBody, ErrorMessage> errorConverter =
						getRetrofit().responseBodyConverter(ErrorMessage.class, new Annotation[0]);
				ErrorMessage errorMessage = errorConverter.convert(response.errorBody());
				if (ServerErrorCode.USER_NOT_FOUND.equals(errorMessage.getCode()))
				{
					reinitializeRetrofit();
					YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_NOT_EXIST, errorMessage);
				}
				else
				{
					listener.onError(errorMessage);
				}
			}
			catch (IOException e)
			{
				listener.onError(new ErrorMessage(e.getMessage()));
			}
		}
	}

}
