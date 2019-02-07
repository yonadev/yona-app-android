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

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import nu.yona.app.BuildConfig;

/**
 * A common Logger implementation to support logging mechanism in application.
 */

public class Logger
{

	public static void logi(String tag, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.INFO, tag, message);
		}
		Log.i(tag, message);
	}

	public static void loge(String tag, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.ERROR, tag, message);
		}
		Log.e(tag, message);
	}

	public static void loge(String tag, String message, Exception exception)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.ERROR, tag, message);
			Crashlytics.logException(exception);
		}
		Log.e(tag, message, exception);
	}

	public static void logd(String tag, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.DEBUG, tag, message);
		}
		Log.d(tag, message);
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
