/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
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

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ActivityMonitorService extends Service {

    private static String currentApp;
    private final Stopwatch stopWatch = new Stopwatch();
    private ActivityMonitorService self;
    private String previousAppName;
    private PowerManager powerManager;
    private ScheduledExecutorService scheduler;
    private Date startTime, endTime;

    private static String printForegroundTask(Context context) {
        currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - AppConstant.ONE_SECOND * AppConstant.ONE_SECOND, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        return currentApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        powerManager = ((PowerManager) YonaApplication.getAppContext().getSystemService(Context.POWER_SERVICE));
        self = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleMethod();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        endTime = new Date();
        updateOnServer(previousAppName);
        super.onDestroy();
        scheduler.shutdownNow();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleMethod() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // This method will check for the Running apps after every 5000ms
                checkRunningApps();
            }
        }, 0, AppConstant.FIVE_SECONDS, TimeUnit.MILLISECONDS);
    }

    private void checkRunningApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && !powerManager.isInteractive()) {
            endTime = new Date();
            stopWatch.stop();
            updateOnServer(previousAppName);
            return;
        }
        final String apppackagename = printForegroundTask(self);
        if (stopWatch.isStarted()) {
            if (!previousAppName.equalsIgnoreCase(apppackagename)) {
                endTime = new Date();
                updateOnServer(previousAppName);
                //once updated on server, start new tracking again.
                previousAppName = apppackagename;
                stopWatch.stop();
                startTime = new Date();
                stopWatch.start();
            }//else if both are same package, we don't need to track.
        } else {
            startTime = new Date();
            stopWatch.start();
            previousAppName = apppackagename;
        }
    }

    private void updateOnServer(String pkgname) {
        APIManager.getInstance().getActivityManager().postActivityToDB(previousAppName, startTime, endTime);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    private void restartService() {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AppConstant.ONE_SECOND,
                restartServicePendingIntent);

    }
}
