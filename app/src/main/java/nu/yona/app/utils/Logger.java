/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import nu.yona.app.BuildConfig;

/**
 * Created by spatni on 15/09/17.
 * A common Logger implementation to support loggin mechanism in application.
 */

public class Logger
{

	public static void logi(String TAG, String message)
	{
		if (BuildConfig.DEBUG)
		{
			Log.i(TAG, message);
		}
	}

	public static void loge(String TAG, String message)
	{
		if (BuildConfig.DEBUG)
		{
			Log.e(TAG, message);
		}
	}

	public static void loge(String TAG, String message, Exception e)
	{
		if (BuildConfig.DEBUG)
		{
			Log.e(TAG, message, e);
		}
	}

	public static void logd(String TAG, String message)
	{
		if (BuildConfig.DEBUG)
		{
			Log.d(TAG, message);
		}
	}

	public static void printStackTrace(Exception e)
	{
		if (BuildConfig.DEBUG)
		{
			e.printStackTrace();
		}
	}

	public static void toast(Context context, String message)
	{
		if (BuildConfig.DEBUG)
		{
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}
}
