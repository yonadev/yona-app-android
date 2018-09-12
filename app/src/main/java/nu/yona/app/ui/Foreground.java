/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 01/06/16.
 */
public class Foreground implements Application.ActivityLifecycleCallbacks
{

	private static Foreground instance;

	private boolean foreground;

	private boolean homeActivityFirstTime = true;

	private int resumed = 0;
	private int paused = 0;
	private boolean inForeground = true;

	private Foreground()
	{
	}

	/**
	 * Init.
	 *
	 * @param app the app
	 */
	public static void init(Application app)
	{
		if (instance == null)
		{
			instance = new Foreground();
			app.registerActivityLifecycleCallbacks(instance);
		}
	}

	/**
	 * Get foreground.
	 *
	 * @return the foreground
	 */
	public static Foreground get()
	{
		return instance;
	}

	/**
	 * Is foreground boolean.
	 *
	 * @return the boolean
	 */
	public boolean isForeground()
	{
		return foreground;
	}

	/**
	 * Is background boolean.
	 *
	 * @return the boolean
	 */
	public boolean isBackground()
	{
		return !foreground;
	}

	/**
	 * Is home activity first time boolean.
	 *
	 * @return the boolean
	 */
	public boolean isHomeActivityFirstTime()
	{
		return homeActivityFirstTime;
	}

	/**
	 * Sets home activity first time.
	 *
	 * @param homeActivityFirstTime the home activity first time
	 */
	public void setHomeActivityFirstTime(boolean homeActivityFirstTime)
	{
		this.homeActivityFirstTime = homeActivityFirstTime;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle)
	{
	}

	@Override
	public void onActivityStarted(Activity activity)
	{

	}

	@Override
	public void onActivityResumed(Activity activity)
	{
		++resumed;
		foreground = true;
		foregroundOrBackground();
	}

	@Override
	public void onActivityPaused(Activity activity)
	{
		++paused;
		if (activity instanceof YonaActivity)
		{
			if (isHomeActivityFirstTime())
			{
				homeActivityFirstTime = false;
			}
		}
		foreground = false;
		foregroundOrBackground();
	}

	@Override
	public void onActivityStopped(Activity activity)
	{
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
	{

	}

	@Override
	public void onActivityDestroyed(Activity activity)
	{

	}

	/**
	 * Foreground or background.
	 */
	public void foregroundOrBackground()
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			public void run()
			{
				if (paused >= resumed && inForeground)
				{
					inForeground = false;
				}
				else if (resumed > paused && !inForeground)
				{
					inForeground = true;
				}
			}
		}, AppConstant.TIMER_DELAY);
	}
}

