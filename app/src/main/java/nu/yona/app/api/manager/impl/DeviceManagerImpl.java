/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.DeviceManager;
import nu.yona.app.api.manager.network.DeviceNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.NewDevice;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 13/04/16.
 */
public class DeviceManagerImpl implements DeviceManager
{

	private final DeviceNetworkImpl deviceNetwork;
	private final Context mContext;

	/**
	 * Instantiates a new Device manager.
	 *
	 * @param context the context
	 */
	public DeviceManagerImpl(Context context)
	{
		mContext = context;
		deviceNetwork = new DeviceNetworkImpl();
	}

	/**
	 * @param mobileNumber user's mobile number
	 * @return true if number is in expected format
	 */
	@Override
	public boolean validateMobileNumber(String mobileNumber)
	{
		// do validation for mobile number
		// 9 digits of mobile number and '+31'
		mobileNumber = mobileNumber.replaceAll(" ", "");
		return !(TextUtils.isEmpty(mobileNumber)) && android.util.Patterns.PHONE.matcher(mobileNumber).matches();
	}

	/**
	 * @param passcode String of passcode
	 * @return
	 */
	@Override
	public boolean validatePasscode(String passcode)
	{
		if (TextUtils.isEmpty(passcode))
		{
			return false;
		}
		else if (passcode.length() != AppConstant.ADD_DEVICE_PASSWORD_CHAR_LIMIT)
		{
			return false;
		}
		return true;
	}

	/**
	 * Add another device
	 *
	 * @param devicePassword password generated from device
	 * @param listener
	 */
	@Override
	public void addDevice(final String devicePassword, final DataLoadListener listener)
	{
		if (YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(AppConstant.NEW_DEVICE_REQUESTED, false))
		{
			deleteDevice(new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					addDeviceAgain(devicePassword, listener);
				}

				@Override
				public void onError(Object errorMessage)
				{
					addDeviceAgain(devicePassword, listener); // we don't worray about delete device, we just add device again.
				}
			});
		}
		else
		{
			addDeviceAgain(devicePassword, listener);
		}
	}

	private void addDeviceAgain(String devicePassword, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaNewDeviceRequest().getHref()))
			{
				deviceNetwork.addDevice(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaNewDeviceRequest().getHref(),
						new NewDeviceRequest(devicePassword), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(),
						new DataLoadListener()
						{

							@Override
							public void onDataLoad(Object result)
							{
								YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(AppConstant.NEW_DEVICE_REQUESTED, true).commit();
								listener.onDataLoad(result);
							}

							@Override
							public void onError(Object errorMessage)
							{
								if (errorMessage instanceof ErrorMessage)
								{
									listener.onError(errorMessage);
								}
								else
								{
									listener.onError(new ErrorMessage(errorMessage.toString()));
								}
							}
						});
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	/**
	 * @param listener
	 */
	@Override
	public void deleteDevice(final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaNewDeviceRequest().getHref()))
			{
				deviceNetwork.deleteDevice(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaNewDeviceRequest().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(AppConstant.NEW_DEVICE_REQUESTED, false).commit();
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (errorMessage instanceof ErrorMessage)
						{
							listener.onError(errorMessage);
						}
						else
						{
							listener.onError(new ErrorMessage(errorMessage.toString()));
						}
					}
				});
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Add another device
	 *
	 * @param devicePassword password generated from device
	 * @param listener
	 */
	@Override
	public void validateDevice(String devicePassword, String mobileNumber, final DataLoadListener listener)
	{
		try
		{
			deviceNetwork.checkDevice(devicePassword,
					mobileNumber,
					new DataLoadListener()
					{

						@Override
						public void onDataLoad(Object result)
						{
							NewDevice device = (NewDevice) result;
							YonaApplication.getEventChangeManager().getSharedPreference().setYonaPassword(device.getYonaPassword());
							getUser(device, listener);
						}

						@Override
						public void onError(Object errorMessage)
						{
							if (errorMessage instanceof ErrorMessage)
							{
								listener.onError(errorMessage);
							}
							else
							{
								listener.onError(new ErrorMessage(errorMessage.toString()));
							}
						}
					});
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void getUser(NewDevice device, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(device.getLinks().getYonaUser().getHref()))
			{
				new AuthenticateManagerImpl(mContext).getUser(device.getLinks().getYonaUser().getHref(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
						YonaApplication.getEventChangeManager().getDataState().updateUser();
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (errorMessage instanceof ErrorMessage)
						{
							listener.onError(errorMessage);
						}
						else
						{
							listener.onError(new ErrorMessage(errorMessage.toString()));
						}
					}
				});
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(DeviceManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}

	}
}
