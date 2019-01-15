/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.receiver;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.Logger;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by kinnarvasa on 23/03/16.
 */
public class YonaReceiver extends BroadcastReceiver
{
	private static final int INTERACTIVE_CHECK_INTERVAL = 10000;

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.context = context;
		switch (intent.getAction())
		{
			case Intent.ACTION_BOOT_COMPLETED:
				handleRebootCompletedBroadcast(context);
			case Intent.ACTION_SCREEN_ON:
				handleScreenOnBroadcast(context);
				break;
			case Intent.ACTION_SCREEN_OFF:
				handleScreenOffBroadcast(context);
				break;
			case AppConstant.WAKE_UP:
				handleWakeUpAlarm(context);
				break;
			case AppConstant.RESTART_DEVICE:
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_DEVICE_RESTART_REQUIRE, null);
				break;
			case AppConstant.RESTART_VPN:
				handleRestartVPNBroadcast(context);
				break;
			case PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED:
				handleDeviceDozeMode(context);
				break;
			default:
				break;
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void handleDeviceDozeMode(Context context)
	{
		Logger.loge("Broadcast", "ACTION_DEVICE_IDLE_MODE_CHANGED");
		PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
		if (powerManager.isDeviceIdleMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			scheduleNextAlarmToCheckIfDeviceIsInteractive(context, INTERACTIVE_CHECK_INTERVAL);
		}
	}

	private void handleRebootCompletedBroadcast(Context context)
	{
		Logger.loge("Broadcast", "ACTION_BOOT_COMPLETED");
		startService(context);
	}

	private void handleScreenOnBroadcast(Context context)
	{
		Logger.logi("Broadcast", "ACTION_SCREEN_ON");
		startService(context);
		AppUtils.startVPN(context, false);
	}

	private void handleScreenOffBroadcast(Context context)
	{
		Logger.logi("Broadcast", "ACTION_SCREEN_OFF");
		AppUtils.setNullScheduler();
		AppUtils.sendLogToServer(AppConstant.ONE_SECOND);
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void handleWakeUpAlarm(Context context)
	{
		Logger.logi("Broadcast", "WAKE_UP");
		// Device is awake from doze/sleep (it can be because of user interaction or of some silent Push notifications).
		// We should start service only when device is interactive else schedule next alarm
		if (isDeviceInteractive(context))
		{
			startService(context);
			AppUtils.startVPN(context, false);
			AppUtils.cancelPendingWakeUpAlarms(context);
		}
		else
		{
			scheduleNextAlarmToCheckIfDeviceIsInteractive(context, INTERACTIVE_CHECK_INTERVAL);
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private boolean isDeviceInteractive(Context context)
	{
		PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
		return powerManager.isInteractive();
	}

	@TargetApi(Build.VERSION_CODES.O)
	public static void scheduleNextAlarmToCheckIfDeviceIsInteractive(Context context, long delay)
	{
		Intent alarmIntent = new Intent(context, YonaReceiver.class);
		alarmIntent.setAction(AppConstant.WAKE_UP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
	}

	private void handleRestartVPNBroadcast(Context context)
	{
		Logger.logi("Broadcast", "Restart VPN Broadcast received");
		showRestartVPN(context.getString(R.string.vpn_disconnected));
	}

	private void startService(Context context)
	{
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && AppUtils.hasPermission(YonaApplication.getAppContext()))
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
		{
			AppUtils.startService(context);
		}
	}


	private void showRestartVPN(String message)
	{
		Intent intent = new Intent(context.getApplicationContext(), YonaActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		if (pendingIntent == null)
		{
			return;
		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			NotificationChannel channel = new NotificationChannel(AppConstant.YONA_VPN_CHANNEL_ID,
					context.getString(R.string.yona_vpn_notification_channel_name),
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}
		notificationManager.notify(0, getConfiguredVPNRestartNotification(message, pendingIntent));
	}


	private Notification getConfiguredVPNRestartNotification(String message, PendingIntent pendingIntent)
	{
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context.getApplicationContext(), AppConstant.YONA_VPN_CHANNEL_ID);
		NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText(message);
		mBuilder.setStyle(bigText);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setContentText(message);
		mBuilder.setTicker(context.getString(R.string.appname));
		mBuilder.setSmallIcon(R.mipmap.ic_launcher);
		mBuilder.setPriority(Notification.PRIORITY_MAX);
		return mBuilder.build();
	}
}
