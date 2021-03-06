/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import org.joda.time.Period;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.User;
import nu.yona.app.api.receiver.YonaReceiver;
import nu.yona.app.api.service.ActivityMonitorService;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeManager;
import nu.yona.timepicker.time.Timepoint;

import static nu.yona.app.YonaApplication.getAppUser;
import static nu.yona.app.YonaApplication.getSharedAppPreferences;
import static nu.yona.app.YonaApplication.getSharedUserPreferences;
import static nu.yona.app.utils.AppConstant.VPN_CONNECT_NOTIFICATION_ID;
import static nu.yona.app.utils.Logger.loge;
import static nu.yona.app.utils.Logger.logi;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class AppUtils
{
	private static InputFilter filter;
	private static boolean submitPressed;
	private static Intent activityMonitorIntent;
	private static ScheduledExecutorService scheduler;
	private static final YonaReceiver receiver = new YonaReceiver();
	private static int certificateDownloadAttempts = 0;
	private static int vpnConnectionAttempts = 0;
	private static final Handler uiTaskHandler = new Handler();

	/**
	 * Has permission boolean.
	 *
	 * @param context the context
	 * @return false if user has not given permission for package access so far.
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean hasPermission(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
			AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
			int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
			return (mode == AppOpsManager.MODE_ALLOWED);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return true;
		}

	}

	/**
	 * Start service once user grant permission for application permission (for 5.1+ version)
	 *
	 * @param context the context
	 */
	public static void startService(Context context)
	{
		try
		{
			activityMonitorIntent = new Intent(context, ActivityMonitorService.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			{
				startForegroundService(context, activityMonitorIntent);
				cancelPendingWakeUpAlarms(context);
			}
			else
			{
				context.startService(activityMonitorIntent);
			}
		}
		catch (Exception e)
		{
			reportException(AppUtils.class, e, Thread.currentThread());
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	public static void cancelPendingWakeUpAlarms(Context context)
	{
		Intent alarmIntent = new Intent(context, YonaReceiver.class);
		alarmIntent.setAction(AppConstant.WAKE_UP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	/*
		Checks if application is not permitted to show notifications as this permission is necessary to show foreground notifications and Toasts
		Starts foreground service.
		Post Alert message if app is not permitted to do so.
	 */
	@TargetApi(Build.VERSION_CODES.O)
	private static void startForegroundService(Context context, Intent activityMonitorIntent)
	{
		if (!NotificationManagerCompat.from(context).areNotificationsEnabled())
		{
			return; // Notification permission is required for starting a ForegroundService
		}
		context.startForegroundService(activityMonitorIntent);
	}

	/**
	 * Stop service.
	 *
	 * @param context the context
	 */
	public static void stopService(Context context)
	{
		try
		{
			if (activityMonitorIntent != null)
			{
				context.stopService(activityMonitorIntent);
				activityMonitorIntent = null;
			}
		}
		catch (Exception e)
		{
			reportException(AppUtils.class, e, Thread.currentThread());
		}
	}

	/**
	 * Generate Random String length of 20
	 *
	 * @param charLimit the char limit
	 * @return random string
	 */
	public static String getRandomString(int charLimit)
	{
		char[] chars = "abcdefghijkmnopqrstuvwxyz0123456789ABCDEFGHJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < charLimit; i++)
		{
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * This will register receiver for different events like screen on-off, boot, connectivity etc.
	 *
	 * @param context the context
	 */
	public static void registerReceiver(Context context)
	{
		loge(AppUtils.class, "Register Receiver");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_BOOT_COMPLETED);
		filter.addAction(AppConstant.RESTART_DEVICE);
		filter.addAction(AppConstant.RESTART_VPN);
		filter.addAction(AppConstant.CONNECT_VPN);
		filter.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);

		context.registerReceiver(receiver, filter);
	}

	/**
	 * Gets time for otp.
	 *
	 * @param time the time
	 * @return the time for otp
	 */
	public static Pair<String, Long> getTimeForOTP(String time)
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			long totalTime = 0;
			Period period = new Period(time);
			totalTime = getTotalTimeForOTP(period, totalTime, buffer);
			return Pair.create(buffer.toString(), totalTime);
		}
		catch (Exception e)
		{
			AppUtils.reportException(AppUtils.class, e, Thread.currentThread());
		}
		return Pair.create(time, (long) 0);
	}

	private static long getTotalTimeForOTP(Period period, long totalTime, StringBuffer buffer)
	{
		if (period.getHours() > 0)
		{
			totalTime += period.getHours() * AppConstant.ONE_SECOND * 60 * 60;
			buffer.append(YonaApplication.getAppContext().getString(R.string.hours, period.getHours() + ""));
		}
		if (period.getMinutes() > 0)
		{
			totalTime += period.getMinutes() * AppConstant.ONE_SECOND * 60;
			buffer.append(YonaApplication.getAppContext().getString(R.string.minute, period.getMinutes() + ""));
		}
		if (period.getSeconds() > 0)
		{
			totalTime += period.getSeconds() * AppConstant.ONE_SECOND;
			buffer.append(YonaApplication.getAppContext().getString(R.string.seconds, period.getSeconds() + ""));
		}
		return totalTime;
	}


	public static void reportException(Class<?> originClass, Exception exception, Thread t, DataLoadListener listener)
	{
		reportException(originClass, exception, t, listener, true);
	}

	/**
	 * Report exception.
	 *
	 * @param originClass class reporting the exception
	 * @param exception   Error
	 * @param t           Current Thread (Thread.currentThread())
	 * @param listener    DataLoadListener to update UI
	 * @param showToast   Shows error toast in UI if possible.
	 */
	public static void reportException(Class<?> originClass, Exception exception, Thread t, DataLoadListener listener, boolean showToast)
	{
		ErrorMessage errorMessage = getErrorMessageFromException(exception);
		if (listener != null)
		{
			listener.onError(errorMessage);
		}
		else if (showToast)
		{
			showErrorToast(errorMessage);
		}
		Logger.loge(originClass, errorMessage.getMessage(), exception);
	}

	/**
	 * getErrorMessage from give Exception or return a generic error message
	 */

	private static ErrorMessage getErrorMessageFromException(Exception exception)
	{
		if (exception != null && exception.getMessage() != null)
		{
			return new ErrorMessage(exception.getMessage());
		}
		return new ErrorMessage(YonaApplication.getAppContext().getString(R.string.generic_exception_message));
	}

	/**
	 * Report exception.
	 *
	 * @param originClass class reporting the exception
	 * @param exception   Error
	 * @param t           Current Thread (Thread.currentThread())
	 */
	public static void reportException(Class<?> originClass, Exception exception, Thread t)
	{
		AppUtils.reportException(originClass, exception, t, null);
	}


	/**
	 * Display Error toast
	 */

	private static void showErrorToast(ErrorMessage errorMessage)
	{
		runOnUiThread(() -> Toast.makeText(YonaApplication.getAppContext(), errorMessage.getMessage(), Toast.LENGTH_LONG).show());
	}

	public static void displayErrorAlert(Context context, ErrorMessage errorMessage)
	{
		runOnUiThread(() -> getGenericAlertDialogWithErrorMessage(context, errorMessage, null).show());
	}

	public static void displayErrorAlert(Context context, ErrorMessage errorMessage, DialogInterface.OnClickListener onClickListener)
	{
		runOnUiThread(() -> getGenericAlertDialogWithErrorMessage(context, errorMessage, onClickListener).show());
	}

	private static AlertDialog getGenericAlertDialogWithErrorMessage(Context context, ErrorMessage errorMessage, DialogInterface.OnClickListener onClickListener)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setMessage(errorMessage.getMessage());
		alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), onClickListener);
		return alertDialogBuilder.create();
	}

	public static void displayInfoAlert(Context context, String title, String message,
										boolean cancelable, DialogInterface.OnClickListener okButtonListener, DialogInterface.OnClickListener cancelButtonListener)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setCancelable(cancelable);
		alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), okButtonListener);
		alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel), cancelButtonListener);
		alertDialogBuilder.create().show();
	}

	public static boolean canPerformIntent(Context context, Intent intent)
	{
		PackageManager mgr = context.getPackageManager();
		List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	@TargetApi(Build.VERSION_CODES.O)
	public static boolean arePersistentNotificationsEnabled(Context context)
	{
		if (getPersistentNotificationChannel(context) == null) // Channel will be null on first launch of the application.
		{
			createPersistentNotificationChannel(context);
		}
		return getPersistentNotificationChannel(context).getImportance() != android.app.NotificationManager.IMPORTANCE_NONE;
	}

	@TargetApi(Build.VERSION_CODES.O)
	private static void createPersistentNotificationChannel(Context context)
	{
		removeOldPersistentNotificationChannel(context);
		NotificationChannel channel = new NotificationChannel(AppConstant.YONA_SERVICE_CHANNEL_ID,
				context.getString(R.string.yona_service_notification_channel_name),
				NotificationManager.IMPORTANCE_MIN);
		channel.setShowBadge(false);
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
	}

	@TargetApi(Build.VERSION_CODES.O)
	private static void removeOldPersistentNotificationChannel(Context context)
	{
		android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
		notificationManager.deleteNotificationChannel(AppConstant.OLD_YONA_SERVICE_CHANNEL_ID);
	}

	@TargetApi(Build.VERSION_CODES.O)
	private static NotificationChannel getPersistentNotificationChannel(Context context)
	{
		android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
		return notificationManager.getNotificationChannel(AppConstant.YONA_SERVICE_CHANNEL_ID);
	}

	private static void runOnUiThread(Runnable runnable)
	{
		boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Looper.getMainLooper().isCurrentThread()
				: Thread.currentThread() == Looper.getMainLooper().getThread();
		if (isUiThread)
		{
			runnable.run();
		}
		else
		{
			uiTaskHandler.post(runnable);
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	public static void removeVPNConnectNotification(Context context)
	{
		android.app.NotificationManager notificationManager;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			notificationManager = context.getSystemService(android.app.NotificationManager.class);
			notificationManager.deleteNotificationChannel(AppConstant.YONA_VPN_CHANNEL_ID);
			return;
		}
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(VPN_CONNECT_NOTIFICATION_ID);
	}

	/**
	 * Get splited time. ex: 21:00 - 23:54 whill return 21:00 and 23:54
	 *
	 * @param time the time
	 * @return string [ ]
	 */
	public static String[] getSplitedTime(String time)
	{
		return time.split("-", 2);
	}

	/**
	 * Gets time in milliseconds.
	 *
	 * @param time the time
	 * @return the time in milliseconds
	 */
	public static Timepoint getTimeInMilliseconds(String time)
	{
		if (!TextUtils.isEmpty(time) && time.contains(":"))
		{
			String[] min = time.split(":");
			return new Timepoint(Integer.parseInt(min[0]), Integer.parseInt(min[1]), 0);
		}
		else
		{
			return new Timepoint(0, 0, 0);
		}
	}

	/**
	 * convert one digit number to two digit number appending with 0 if its length is one else return same
	 *
	 * @param time the time
	 * @return the time digit
	 */
	public static String getTimeDigit(int time)
	{
		String timeDigit = String.valueOf(time);
		if (timeDigit.length() == 1)
		{
			timeDigit = "0" + timeDigit;
		}
		return timeDigit;
	}

	/**
	 * Is submit pressed boolean.
	 *
	 * @return the boolean
	 */
	public static boolean isSubmitPressed()
	{
		return AppUtils.submitPressed;
	}

	/**
	 * Sets submit pressed.
	 *
	 * @param submitPressed the submit pressed
	 */
	public static void setSubmitPressed(boolean submitPressed)
	{
		AppUtils.submitPressed = submitPressed;
	}

	/**
	 * Send log to server.
	 *
	 * @param delayMilliseconds the delay milliseconds
	 */
	public static void sendLogToServer(long delayMilliseconds)
	{
		new Handler().postDelayed(() -> APIManager.getInstance().getActivityManager().postAllDBActivities(), delayMilliseconds);
	}

	/**
	 * Gets initialize scheduler.
	 *
	 * @return the initialize scheduler
	 */
	public static ScheduledExecutorService getInitializeScheduler()
	{
		if (scheduler == null)
		{
			scheduler = Executors.newSingleThreadScheduledExecutor();
		}
		return scheduler;
	}

	/**
	 * Gets scheduler.
	 *
	 * @return the scheduler
	 */
	public static ScheduledExecutorService getScheduler()
	{
		return AppUtils.scheduler;
	}

	/**
	 * Sets null scheduler.
	 */
	public static void setNullScheduler()
	{
		if (scheduler != null)
		{
			scheduler.shutdown();
		}
		scheduler = null;
	}

	public static boolean isVPNConnected(Context context)
	{
		String profileUUID = getSharedUserPreferences().getString(PreferenceConstant.PROFILE_UUID, "");
		VpnProfile profile = ProfileManager.get(context, profileUUID);
		return (VpnStatus.isVPNActive() && ProfileManager.getLastConnectedVpn() == profile);
	}

	public static Intent startVPN(Context context, boolean returnIntent)
	{

		String profileUUID = getSharedUserPreferences().getString(PreferenceConstant.PROFILE_UUID, "");
		VpnProfile profile = ProfileManager.get(context, profileUUID);
		User user = getAppUser();
		if (profile == null || VpnStatus.isVPNActive() || user == null || getAppUser().getVpnProfile() == null)
		{
			return null;
		}
		AppUtils.removeVPNConnectNotification(context);
		profile.mUsername = !TextUtils.isEmpty(user.getVpnProfile().getVpnLoginID()) ? user.getVpnProfile().getVpnLoginID() : "";
		profile.mPassword = !TextUtils.isEmpty(user.getVpnProfile().getVpnPassword()) ? user.getVpnProfile().getVpnPassword() : "";
		if (returnIntent)
		{
			return getVPNIntent(profile, context);
		}
		startVPN(profile, context);
		return null;
	}

	private static void startVPN(VpnProfile profile, Context context)
	{
		context.startActivity(getVPNIntent(profile, context));
	}

	private static Intent getVPNIntent(VpnProfile profile, Context context)
	{
		ProfileManager.getInstance(context).saveProfile(context, profile);
		Intent intent = new Intent(context, LaunchVPN.class);
		intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
		intent.setAction(Intent.ACTION_MAIN);
		intent.putExtra(AppConstant.FROM_LOGIN, true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		boolean showOpenVpnLog = getSharedAppPreferences().getBoolean(AppConstant.SHOW_VPN_WINDOW, false);
		intent.putExtra(LaunchVPN.EXTRA_HIDELOG, !showOpenVpnLog);
		return intent;
	}

	public static void stopVPN(Context context)
	{
		String profileUUID = getSharedUserPreferences().getString(PreferenceConstant.PROFILE_UUID, "");
		VpnProfile profile = ProfileManager.get(context, profileUUID);
		if (!VpnStatus.isVPNActive() || !(ProfileManager.getLastConnectedVpn() == profile))
		{
			return;
		}
		YonaApplication.getAppContext().stopOpenVPNService();
		Logger.loge(AppUtils.class, "VPN stop called");
	}

	public static void downloadCertificates()
	{
		User user = getAppUser();
		if (!user.isActive() || YonaApplication.getEventChangeManager().getSharedPreference().getRootCertPath() != null)
		{
			return;
		}
		DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result) -> handleDownloadFileFromUrlSuccess(result), (result -> handleDownloadFileFromUrlFailure()), null);
		new DownloadFileFromURL(user.getSslRootCertLink(), dataLoadListener);
	}

	private static Object handleDownloadFileFromUrlSuccess(Object result)
	{
		if (!TextUtils.isEmpty(result.toString()))
		{
			YonaApplication.getEventChangeManager().getSharedPreference().setRootCertPath(result.toString());
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_ROOT_CERTIFICATE_DOWNLOADED, null);
		}
		logi(AppUtils.class, "Download successful: " + result.toString());
		return null; // Dummy return value, to allow use as data load handler
	}

	private static Object handleDownloadFileFromUrlFailure()
	{
		loge(AppUtils.class, "Download fail");
		certificateDownloadAttempts++;
		if (certificateDownloadAttempts < 3)
		{
			downloadCertificates();
		}
		return null; // Dummy return value, to allow use as data error handler
	}

	public static void downloadVPNProfile()
	{
		User user = getAppUser();
		if (user.getVpnProfile() != null && YonaApplication.getEventChangeManager().getSharedPreference().getVPNProfilePath() == null)
		{
			DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result) -> handleDownloadVpnFromUrlSuccess(result), (error) -> handleDownloadVpnFromUrlFailure(), null);
			new DownloadFileFromURL(user.getVpnProfile().getLinks().getOvpnProfile().getHref(), dataLoadListener);
		}
	}

	private static Object handleDownloadVpnFromUrlSuccess(Object result)
	{
		if (result != null && !TextUtils.isEmpty(result.toString()))
		{
			YonaApplication.getEventChangeManager().getSharedPreference().setVPNProfilePath(result.toString());
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_VPN_CERTIFICATE_DOWNLOADED, null);
			logi(AppUtils.class, "Download successful: " + result.toString());
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private static Object handleDownloadVpnFromUrlFailure()
	{
		loge(AppUtils.class, "Download fail");
		vpnConnectionAttempts++;
		if (vpnConnectionAttempts < 3)
		{
			downloadVPNProfile();
		}
		return null;// Dummy return value, to allow use as data error handler
	}

	public static byte[] getCACertificate(String path)
	{
		if (path != null && !TextUtils.isEmpty(path))
		{
			File file = new File(path);
			int size = (int) file.length();
			byte[] bytes = new byte[size];
			try
			{
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
				buf.read(bytes, 0, bytes.length);
				buf.close();
				return bytes;
			}
			catch (java.io.IOException e)
			{
				AppUtils.reportException(AppUtils.class, e, Thread.currentThread());
			}
		}
		return null;
	}

	public static boolean checkCACertificate()
	{
		boolean isCertExist = false;
		try
		{
			KeyStore ks = KeyStore.getInstance("AndroidCAStore");
			if (ks == null)
			{
				return false;
			}
			ks.load(null, null);
			Enumeration aliases = ks.aliases();
			if (getAppUser() == null || getAppUser().getSslRootCertCN() == null)
			{
				return false;
			}
			String caCertName = getAppUser().getSslRootCertCN();
			if (!TextUtils.isEmpty(caCertName))
			{
				while (aliases.hasMoreElements())
				{
					String alias = (String) aliases.nextElement();
					java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);
					if (cert.getIssuerDN().getName().contains(caCertName))
					{
						isCertExist = true;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			reportException(AppUtils.class, e, Thread.currentThread());
		}
		return isCertExist;
	}

	public static boolean checkKeyboardOpen(View view)
	{
		int defaultKeyboardDp = 100;
		Rect r = new Rect();
		view.getWindowVisibleDisplayFrame(r);
		int estimatedKeyboardHeight = (int) TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultKeyboardDp, view.getResources().getDisplayMetrics());
		view.getWindowVisibleDisplayFrame(r);
		int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
		return heightDiff > estimatedKeyboardHeight;
	}

	/**
	 * Check for network availability.
	 *
	 * @param context requested context object.
	 * @return true if available else false.
	 */
	public static boolean isNetworkAvailable(final Context context)
	{
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
	}

	/**
	 * It convert requested @{@link StatusEnum} to Local based string.
	 *
	 * @return Local based string for current @{@link StatusEnum}.
	 * Using by @{@link nu.yona.app.api.model.YonaBuddy} object.
	 */
	public static String getSendingStatusToDisplay(String sendingStatus)
	{
		if (sendingStatus != null)
		{
			if (sendingStatus.equals(StatusEnum.NOT_REQUESTED.getStatus()))
			{
				return YonaApplication.getAppContext().getResources().getString(R.string.not_requested);
			}
			else if (sendingStatus.equals(StatusEnum.REQUESTED.getStatus()))
			{
				return YonaApplication.getAppContext().getResources().getString(R.string.requested);
			}
			else if (sendingStatus.equals(StatusEnum.ACCEPTED.getStatus()))
			{
				return YonaApplication.getAppContext().getResources().getString(R.string.accepted);
			}
			else if (sendingStatus.equals(StatusEnum.REJECTED.getStatus()))
			{
				return YonaApplication.getAppContext().getResources().getString(R.string.rejected);
			}
		}
		return StatusEnum.NOT_REQUESTED.getStatus(); //Considered as default.
	}

	public static void moveSharedPreferences(SharedPreferences fromPreferences, SharedPreferences toPreferences)
	{

		SharedPreferences.Editor editor = toPreferences.edit();
		editor.clear();
		copySharedPreferences(fromPreferences, editor);
		editor.commit();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	private static void copySharedPreferences(SharedPreferences fromPreferences, SharedPreferences.Editor toEditor)
	{

		for (Map.Entry<String, ?> entry : fromPreferences.getAll().entrySet())
		{
			Object value = entry.getValue();
			String key = entry.getKey();
			if (value instanceof String)
			{
				toEditor.putString(key, ((String) value));
			}
			else if (value instanceof Set)
			{
				toEditor.putStringSet(key, (Set<String>) value); // EditorImpl.putStringSet already creates a copy of the set
			}
			else if (value instanceof Integer)
			{
				toEditor.putInt(key, (Integer) value);
			}
			else if (value instanceof Long)
			{
				toEditor.putLong(key, (Long) value);
			}
			else if (value instanceof Float)
			{
				toEditor.putFloat(key, (Float) value);
			}
			else if (value instanceof Boolean)
			{
				toEditor.putBoolean(key, (Boolean) value);
			}
		}
	}
}
