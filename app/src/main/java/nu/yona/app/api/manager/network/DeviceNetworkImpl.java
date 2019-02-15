/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.api.model.NewDevice;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 13/04/16.
 */
public class DeviceNetworkImpl extends BaseImpl
{

	/**
	 * Add device on server
	 *
	 * @param url            the url
	 * @param devicePassword : random generated from devcie
	 * @param yonaPassword   : yona application password need to pass in header
	 * @param listener       the listener
	 */
	public void addDevice(String url, NewDeviceRequest devicePassword, String yonaPassword, final DataLoadListener listener)
	{
		try
		{
			getRestApi().addDevice(url, yonaPassword, Locale.getDefault().toString().replace('_', '-'), devicePassword).enqueue(getCall(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Delete device.
	 *
	 * @param url          url to send delete request
	 * @param yonaPassword applicaiton password need to pass in header
	 * @param listener     the listener
	 */
	public void deleteDevice(String url, String yonaPassword, DataLoadListener listener)
	{
		try
		{
			getRestApi().deleteDevice(url, yonaPassword, Locale.getDefault().toString().replace('_', '-')).enqueue(getCall(listener));
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceNetworkImpl.class, e, Thread.currentThread());
		}
	}

	/**
	 * Check device.
	 *
	 * @param devicePassword the device password
	 * @param mobileNumber   the mobile number
	 * @param listener       the listener
	 */
	public void checkDevice(String devicePassword, String mobileNumber, final DataLoadListener listener)
	{
		try
		{
			getRestApi().checkDevice(mobileNumber, devicePassword, Locale.getDefault().toString().replace('_', '-')).enqueue(new Callback<NewDevice>()
			{
				@Override
				public void onResponse(Call<NewDevice> call, Response<NewDevice> response)
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
				public void onFailure(Call<NewDevice> call, Throwable t)
				{
					onError(t, listener);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceNetworkImpl.class, e, Thread.currentThread());
		}
	}
}
