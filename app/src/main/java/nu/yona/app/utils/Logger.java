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

	public static void logi(Class<?> originClass, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.INFO, originClass.getName(), message);
		}
		Log.i(originClass.getName(), message);
	}

	public static void loge(Class<?> originClass, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.ERROR, originClass.getName(), message);
		}
		Log.e(originClass.getName(), message);
	}

	public static void loge(Class<?> originClass, String message, Exception exception)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.ERROR, originClass.getName(), message);
			Crashlytics.logException(exception);
		}
		Log.e(originClass.getName(), message, exception);
	}

	public static void logd(Class<?> originClass, String message)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.log(Log.DEBUG, originClass.getName(), message);
		}
		Log.d(originClass.getName(), message);
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
