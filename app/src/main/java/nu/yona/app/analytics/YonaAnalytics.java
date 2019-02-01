/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.analytics;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import nu.yona.app.YonaApplication;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.PauseResumeHook;

import static nu.yona.app.YonaApplication.getUserFromDB;
import static nu.yona.app.utils.Logger.loge;
import static nu.yona.app.utils.Logger.logi;

/**
 * Created by kinnarvasa on 02/09/16.
 */

public class YonaAnalytics
{
	private static YonaAnalytics instance;
	private Categorizable category = null;
	private static final String TAG = "YonaAnalytics";

	public static YonaAnalytics getInstance()
	{
		if (instance == null)
		{
			instance = new YonaAnalytics();
		}
		return instance;
	}


	public static void updateScreen(Categorizable cat)
	{
		getInstance().category = cat;
		trackScreen(cat.getAnalyticsCategory());
	}

	public static void trackScreen(String screenName, String... extraDimensions)
	{
		getInstance().createEventInternal(screenName, "Track", 0, extraDimensions);
	}

	//google's code
	public static void trackCategoryScreen(String category, String screenName, String... extraDimensions)
	{
		getInstance().createEventInternal(category, screenName, "Track", 0, extraDimensions);
	}

	private void createEventInternal(String label, String action, int metrics, String... extraDimensions)
	{
		createEventInternal(getCurrentCategoryId(), label, action, metrics, extraDimensions);
	}

	private void createEventInternal(String category, String label, String action, int metrics, String... extraDimensions)
	{
		if (category == null || category.equals("null") || category.equals(AnalyticsConstant.SCREEN_BASE_FRAGMENT))
		{
			return;
		}
		logi(TAG, "Analytics: Category: [" + category + "] Label: [" + label + "] Action: [" + action + "]");
		//commented out until things are probably working
		Tracker t = YonaApplication.getTracker();
		if (getUserFromDB() != null
				&& getUserFromDB().getLinks() != null
				&& getUserFromDB().getLinks().getSelf() != null
				&& !TextUtils.isEmpty(getUserFromDB().getLinks().getSelf().getHref()))
		{
			t.set("&uid", getUserFromDB().getLinks().getSelf().getHref());
		}
		if (action.equals("Track"))
		{
			t.setScreenName(category);
			t.send(new HitBuilders.ScreenViewBuilder().build());
		}
		else
		{
			HitBuilders.EventBuilder eb = new HitBuilders.EventBuilder()
					.setCategory(getCurrentCategoryId())
					.setAction(action)
					.setLabel(label);
			t.send(eb.build());
		}
	}

	private String getCurrentCategoryId()
	{
		if (category == null)
		{
			loge(TAG, "Attempt to fire analytics event prior to initializing current Categorizable");
			return "null";
		}
		return category.getAnalyticsCategory();
	}

	public static class Metrics
	{

		private static Metrics instance;
		public static final int COUNT = 6;
		int[] metrics = new int[COUNT];

		private Metrics()
		{
			initMetricCount();
		}

		private void initMetricCount()
		{
			for (int i = 0; i < COUNT; i++)
			{
				metrics[i] = 0;
			}
		}

		public static Metrics getInstance()
		{
			if (instance == null)
			{
				instance = new Metrics();
			}

			return instance;
		}

		public static void incrementMetrics(int flags, int amount)
		{
			for (int i = 0; i < COUNT; i++)
			{
				if (((flags >> i) & 1) == 1)
				{
					getInstance().metrics[i] += amount;
				}
			}
		}

		public static int getMetric(int metric)
		{
			int index = Integer.numberOfTrailingZeros(metric); //returns 32 with the value 0 passed in
			if (index >= COUNT)
			{
				throw new IllegalArgumentException("Invalid flag parameter " + metric + " passed to getMetric");
			}
			return getInstance().metrics[index];
		}


		public static void registerMetrics(HitBuilders.EventBuilder eb, int flags)
		{
			for (int i = 0; i < COUNT; i++)
			{
				if (((flags >> i) & 1) == 1)
				{
					eb.setCustomMetric(i, getInstance().metrics[i]);
				}
			}
		}

	}

	public static void createTrackEventWithCategory(String category, String label, String... extraDimensions)
	{
		getInstance().createEventInternal(category, label, "Track", 0, extraDimensions);
	}

	public static void createTapEventWithCategory(String category, String label, String... extraDimensions)
	{
		getInstance().createEventInternal(category, label, "Tap", 0, extraDimensions);
	}

	public static void createTapEvent(String label, String... extraDimensions)
	{
		getInstance().createEventInternal(label, "Tap", 0, extraDimensions);
	}

	public static void flushMetrics(int flags)
	{
		getInstance().internalFlushMetrics(flags);
	}

	public void internalFlushMetrics(int flags)
	{
		Tracker t = YonaApplication.getTracker();

		HitBuilders.EventBuilder eb = new HitBuilders.EventBuilder()
				.setCategory(getCurrentCategoryId())
				.setAction("Update")
				.setLabel("Metrics");

		Metrics.registerMetrics(eb, flags);

		t.send(eb.build());
	}

	public static class BackHook implements PauseResumeHook
	{

		String eventName;

		public BackHook(String eventName)
		{
			this.eventName = eventName;
		}

		@Override
		public void onResume(Fragment fragment)
		{
			if (fragment.getActivity() instanceof BaseActivity)
			{
				((BaseActivity) fragment.getActivity()).addBackPressListener(new Runnable()
				{
					@Override
					public void run()
					{
						YonaAnalytics.createTapEvent(eventName);
					}
				});
			}
		}

		@Override
		public void onPause(Activity activity)
		{

		}

		@Override
		public void onResume(Activity activity)
		{
			if (activity instanceof BaseActivity)
			{
				((BaseActivity) activity).addBackPressListener(new Runnable()
				{
					@Override
					public void run()
					{
						YonaAnalytics.createTapEvent(eventName);
					}
				});

			}
		}

		@Override
		public void onPause(Fragment fragment)
		{
			if (fragment.getActivity() instanceof BaseActivity)
			{
				((BaseActivity) fragment.getActivity()).clearBackPressListener();
			}
		}
	}
}
