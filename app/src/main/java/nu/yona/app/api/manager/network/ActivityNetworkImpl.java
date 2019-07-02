/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.api.model.AppActivity;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.Message;
import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The type Activity network.
 */
public class ActivityNetworkImpl extends BaseImpl
{

	/**
	 * Gets Next days activity.
	 *
	 * @param nextDayActivittUrl the url
	 * @param yonaPassword       the yona password
	 * @param listener           the listener
	 */
	public void getNextDayActivity(String nextDayActivittUrl, String yonaPassword, DataLoadListener listener)
	{
		try
		{
			getRestApi().getActivities(nextDayActivittUrl, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getEmbeddedYonaActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets day detail activity.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param listener     the listener
	 */
	public void getDayDetailActivity(String url, String yonaPassword, DataLoadListener listener)
	{
		try
		{
			getRestApi().getDayDetailActivity(url, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getDayDetailActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets Next weeks activity.
	 *
	 * @param nextWeeksActivityUrl the url
	 * @param password             the password
	 * @param listener             the listener
	 */
	public void getNextWeeksActivity(String nextWeeksActivityUrl, String password, DataLoadListener listener)
	{
		try
		{
			getRestApi().getActivities(nextWeeksActivityUrl, password, Locale.getDefault().toString().replace('_', '-')).enqueue(getEmbeddedYonaActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets weeks detail activity.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param listener     the listener
	 */
	public void getWeeksDetailActivity(String url, String yonaPassword, DataLoadListener listener)
	{
		try
		{
			getRestApi().getWeekDetailActivity(url, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getweekDetailActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Gets with buddy activity.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param itemPerPage  the item per page
	 * @param pageNo       the page no
	 * @param listener     the listener
	 */
	public void getWithBuddyActivity(String url, String yonaPassword, int itemPerPage, int pageNo, DataLoadListener listener)
	{
		try
		{
			getRestApi().getWithBuddyActivity(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), itemPerPage, pageNo).enqueue(getEmbeddedYonaActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
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
	public void postAppActivity(String url, String yonaPassword, AppActivity appActivity, DataLoadListener listener)
	{
		try
		{
			getRestApi().postAppActivity(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), appActivity).enqueue(getCall(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	public void getComments(String url, String yonaPassword, final DataLoadListener listener)
	{
		try
		{
			getRestApi().getComments(url, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getEmbeddedYonaActivity(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	public void addComment(String url, String yonaPassword, Message message, final DataLoadListener listener)
	{
		try
		{
			getRestApi().addComment(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), message).enqueue(new Callback<YonaMessage>()
			{
				@Override
				public void onResponse(Call<YonaMessage> call, Response<YonaMessage> response)
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
				public void onFailure(Call<YonaMessage> call, Throwable t)
				{
					onError(t, listener);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	public void replyComment(String url, String yonaPasswrod, MessageBody messageBody, final DataLoadListener listener)
	{
		try
		{
			getRestApi().replyComment(url, yonaPasswrod, Locale.getDefault().toString().replace('_', '-'), messageBody).enqueue(new Callback<YonaMessage>()
			{
				@Override
				public void onResponse(Call<YonaMessage> call, Response<YonaMessage> response)
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
				public void onFailure(Call<YonaMessage> call, Throwable t)
				{
					onError(t, listener);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateNetworkImpl.class, e, Thread.currentThread(), listener);
		}
	}

	private Callback<EmbeddedYonaActivity> getEmbeddedYonaActivity(final DataLoadListener listener)
	{
		return new Callback<EmbeddedYonaActivity>()
		{
			@Override
			public void onResponse(Call<EmbeddedYonaActivity> call, Response<EmbeddedYonaActivity> response)
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
			public void onFailure(Call<EmbeddedYonaActivity> call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}

	private Callback<DayActivity> getDayDetailActivity(final DataLoadListener listener)
	{
		return new Callback<DayActivity>()
		{
			@Override
			public void onResponse(Call<DayActivity> call, Response<DayActivity> response)
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
			public void onFailure(Call<DayActivity> call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}

	private Callback<WeekActivity> getweekDetailActivity(final DataLoadListener listener)
	{
		return new Callback<WeekActivity>()
		{
			@Override
			public void onResponse(Call<WeekActivity> call, Response<WeekActivity> response)
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
			public void onFailure(Call<WeekActivity> call, Throwable t)
			{
				onError(t, listener);
			}
		};
	}
}
