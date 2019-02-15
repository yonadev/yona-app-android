/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.api.model.AddBuddy;
import nu.yona.app.api.model.YonaBuddies;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 28/04/16.
 */
public class BuddyNetworkImpl extends BaseImpl
{

	/**
	 * Add Buddy
	 *
	 * @param url          the url
	 * @param yonaPassowrd the yona passowrd
	 * @param buddy        the buddy
	 * @param listener     the listener
	 */
	public void addBuddy(String url, String yonaPassowrd, AddBuddy buddy, final DataLoadListener listener)
	{
		try
		{
			getRestApi().addBuddy(url, yonaPassowrd, Locale.getDefault().toString().replace('_', '-'), buddy).enqueue(new Callback<YonaBuddy>()
			{
				@Override
				public void onResponse(Call<YonaBuddy> call, Response<YonaBuddy> response)
				{
					if (response.code() < NetworkConstant.RESPONSE_STATUS)
					{
						listener.onDataLoad(response.body());
					}
					else
					{
						onError(response, listener);
					}
				}

				@Override
				public void onFailure(Call<YonaBuddy> call, Throwable t)
				{
					onError(t, listener);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(BuddyNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets buddies.
	 *
	 * @param url      the url
	 * @param password the password
	 * @param listener the listener
	 */
	public void getBuddies(String url, String password, final DataLoadListener listener)
	{
		try
		{
			getRestApi().getBuddy(url, password, Locale.getDefault().toString().replace('_', '-')).enqueue(new Callback<YonaBuddies>()
			{
				@Override
				public void onResponse(Call<YonaBuddies> call, Response<YonaBuddies> response)
				{
					if (response.code() < NetworkConstant.RESPONSE_STATUS)
					{
						listener.onDataLoad(response.body());
					}
					else
					{
						onError(response, listener);
					}
				}

				@Override
				public void onFailure(Call<YonaBuddies> call, Throwable t)
				{
					onError(t, listener);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(BuddyNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Delete buddy.
	 *
	 * @param url      the url
	 * @param passwrod the passwrod
	 * @param listener the listener
	 */
	public void deleteBuddy(String url, String passwrod, DataLoadListener listener)
	{
		try
		{
			getRestApi().deleteBuddy(url, passwrod, Locale.getDefault().toString().replace('_', '-')).enqueue(getCall(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(BuddyNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}
}
