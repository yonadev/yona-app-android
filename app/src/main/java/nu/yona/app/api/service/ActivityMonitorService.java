/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.Logger;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ActivityMonitorService extends Service
{

	private static String currentApp;
	private final Stopwatch stopWatch = new Stopwatch();
	private String previousAppName;
	private PowerManager powerManager;
	private Date startTime, endTime;
	private ScheduledFuture scheduledFuture;
	private static final int NOTIFICATION_ID = 1111;

	private static String printForegroundTask(Context context)
	{
		currentApp = "NULL";
		try
		{
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
				long time = System.currentTimeMillis();
				List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - AppConstant.ONE_SECOND * AppConstant.ONE_SECOND, time);
				if (appList != null && appList.size() > 0)
				{
					SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
					for (UsageStats usageStats : appList)
					{
						mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
					}
					if (!mySortedMap.isEmpty())
					{
						currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
					}
				}
			}
			else
			{
				ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				currentApp = am.getRunningAppProcesses().get(0).processName;
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityMonitorService.class.getSimpleName(), e, Thread.currentThread());
		}
		return currentApp;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		restartReceiver();
		powerManager = ((PowerManager) YonaApplication.getAppContext().getSystemService(Context.POWER_SERVICE));
	}

	private void restartReceiver()
	{
		if (YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(AppConstant.TERMINATED_APP, false))
		{
			YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(AppConstant.TERMINATED_APP, false).commit();
			AppUtils.registerReceiver(YonaApplication.getAppContext());
		}
		AppUtils.registerReceiver(YonaApplication.getAppContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		scheduleMethod();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			displayActivityMonitoringNotification();
		}
		return START_NOT_STICKY;
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void displayActivityMonitoringNotification()
	{
		Intent intent = new Intent(this, LaunchActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(this, AppConstant.YONA_SERVICE_CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(this.getString(R.string.yona_notification_content))
				.setContentIntent(pendingIntent)
				.setOngoing(true)
				.setPriority(0)
				.setOngoing(true)
				.build();
		startForeground(NOTIFICATION_ID, notification);
	}


	private void removeActivityMonitoringNotification()
	{
		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onDestroy()
	{
		shutdownScheduler();
		endTime = new Date();
		updateOnServer(previousAppName);
		removeActivityMonitoringNotification();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	private void scheduleMethod()
	{

		scheduledFuture = AppUtils.getInitializeScheduler().scheduleAtFixedRate(new Runnable()
		{

			@Override
			public void run()
			{
				if (AppUtils.getScheduler() == null)
				{
					scheduledFuture.cancel(true);
					return;
				}
				// This method will check for the Running apps after every 5000ms
				checkRunningApps();
			}
		}, 0, AppConstant.ONE_SECOND, TimeUnit.MILLISECONDS);
	}

	private void checkRunningApps()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && !powerManager.isInteractive())
		{
			endTime = new Date();
			stopWatch.stop();
			updateOnServer(previousAppName);
			return;
		}
		final String apppackagename = printForegroundTask(this);
		if (stopWatch.isStarted())
		{
			if (!previousAppName.equalsIgnoreCase(apppackagename))
			{
				endTime = new Date();
				updateOnServer(previousAppName);
				//once updated on server, start new tracking again.
				previousAppName = apppackagename;
				stopWatch.stop();
				startTime = new Date();
				stopWatch.start();
			}//else if both are same package, we don't need to track.
		}
		else
		{
			startTime = new Date();
			stopWatch.start();
			previousAppName = apppackagename;
		}
	}

	private void updateOnServer(String pkgname)
	{
		Logger.logi("updateOnServer", "pck: " + pkgname);
		if (previousAppName != null && !pkgname.equals("NULL") && startTime != null && endTime != null && startTime.before(endTime))
		{
			APIManager.getInstance().getActivityManager().postActivityToDB(previousAppName, startTime, endTime);
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(AppConstant.TERMINATED_APP, true).commit();
		shutdownScheduler();
		restartService();
		super.onTaskRemoved(rootIntent);
	}

	private void shutdownScheduler()
	{
		try
		{
			if (AppUtils.getScheduler() != null)
			{
				AppUtils.getScheduler().shutdownNow();
				AppUtils.setNullScheduler();
			}
			if (scheduledFuture != null)
			{
				scheduledFuture.cancel(true);
				scheduledFuture = null;
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityMonitorService.class.getSimpleName(), e, Thread.currentThread());
		}
	}

	private void restartService()
	{
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AppConstant.ONE_SECOND, restartServicePendingIntent);

	}
}
