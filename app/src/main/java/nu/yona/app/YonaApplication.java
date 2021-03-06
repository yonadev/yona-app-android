/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.api.model.User;
import nu.yona.app.state.DataState;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.Foreground;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 16/03/16.
 */
public class YonaApplication extends Application
{

	private static YonaApplication mContext;
	private Tracker tracker;

	private static EventChangeManager eventChangeManager;
	private static SharedPreferences sharedAppPreferences;
	private static SharedPreferences sharedUserPreferences;
	private static DataState sharedAppDataState;


	/**
	 * Gets app context.
	 *
	 * @return the app context
	 */
	public static synchronized YonaApplication getAppContext()
	{
		return mContext;
	}

	/**
	 * Gets event change manager.
	 *
	 * @return the event change manager
	 */
	public static EventChangeManager getEventChangeManager()
	{
		return eventChangeManager;
	}

	/**
	 * Gets event change manager.
	 *
	 * @return the event change manager
	 */
	public static SharedPreferences getSharedAppPreferences()
	{
		return sharedAppPreferences;
	}

	/**
	 * Gets event change manager.
	 *
	 * @return the event change manager
	 */
	public static SharedPreferences getSharedUserPreferences()
	{
		return sharedUserPreferences;
	}

	/**
	 * Gets event change manager.
	 *
	 * @return the event change manager
	 */
	public static DataState getSharedAppDataState()
	{
		return sharedAppDataState;
	}

	public static User getAppUser()
	{
		return sharedAppDataState.getUser();
	}

	@Override
	public void onCreate()
	{
		enableStickMode();
		super.onCreate();
		mContext = this;
		initializeAppGlobals();
		validateSharedPreferences();
		Foreground.init(this);
	}

	public void initializeAppGlobals()
	{
		eventChangeManager = new EventChangeManager();
		sharedAppDataState = eventChangeManager.getDataState();
		sharedAppPreferences = eventChangeManager.getSharedPreference().getAppPreferences();
		sharedUserPreferences = eventChangeManager.getSharedPreference().getUserPreferences();
	}

	private void validateSharedPreferences()
	{
		if (sharedAppPreferences.getBoolean(PreferenceConstant.STEP_REGISTER, false))
		{
			// The user preferences apparently contain the app preferences, due to an earlier bug
			swapUserAndAppSharedPreferences();
		}
	}

	private void swapUserAndAppSharedPreferences()
	{
		nu.yona.app.utils.Logger.logi(YonaApplication.class, "Correcting user and app preferences by swapping them");
		SharedPreferences tempSharedUserPrefs = YonaApplication.getAppContext().getSharedPreferences("TEMP_USER_PREF", Context.MODE_PRIVATE);
		SharedPreferences tempSharedAppPrefs = YonaApplication.getAppContext().getSharedPreferences("TEMP_APP_PREF", Context.MODE_PRIVATE);
		AppUtils.moveSharedPreferences(sharedAppPreferences, tempSharedUserPrefs);
		AppUtils.moveSharedPreferences(sharedUserPreferences, tempSharedAppPrefs);
		AppUtils.moveSharedPreferences(tempSharedAppPrefs, sharedAppPreferences);
		AppUtils.moveSharedPreferences(tempSharedUserPrefs, sharedUserPreferences);
		nu.yona.app.utils.Logger.logi(YonaApplication.class, "Corrected user and app preferences by swapping them");
	}

	private void enableStickMode()
	{
		if (getResources().getBoolean(R.bool.developerMode))
		{
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads()
					.detectDiskWrites()
					.detectNetwork()   // or .detectAll() for all detectable problems
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects()
					.penaltyLog()
					.build());
		}
	}

	public static Tracker getTracker()
	{
		if (mContext.tracker == null)
		{

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);

			String key = AnalyticsConstant.APP_KEY;
			Tracker tracker = analytics.newTracker(key);
			tracker.setSessionTimeout(600);
			//tracker.setScreenName(getStationName() + " MainView");
			try
			{
				tracker.setLanguage(Locale.getDefault().getDisplayLanguage());
				tracker.enableExceptionReporting(true);
				if (getAppUser() != null
						&& getAppUser().getLinks() != null
						&& getAppUser().getLinks().getSelf() != null
						&& !TextUtils.isEmpty(getAppUser().getLinks().getSelf().getHref()))
				{
					tracker.set("&uid", getAppUser().getLinks().getSelf().getHref());
				}
				PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
				tracker.setAppVersion(pInfo.versionName + mContext.getString(R.string.space) + pInfo.versionCode);
				tracker.send(new HitBuilders.ScreenViewBuilder().build());
			}
			catch (PackageManager.NameNotFoundException e)
			{
				AppUtils.reportException(YonaApplication.class, e, Thread.currentThread());
			}

			GoogleAnalytics.getInstance(mContext).getLogger()
					.setLogLevel(Logger.LogLevel.VERBOSE);

			//For testing, otherwise we have to wait 30 minutes for updates-- this is a 10 minute auto-dispatch
			//setting it to 0 disables auto dispatch which means we have to manually send the data like so:
			//GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
			GoogleAnalytics.getInstance(mContext).setLocalDispatchPeriod(mContext.getResources().getInteger(R.integer.analytics_server_send_time));
			tracker.enableExceptionReporting(true); //basic exception reporting for the app
			mContext.tracker = tracker;
		}
		return mContext.tracker;
	}


	private ServiceConnection getVpnServiceStopperConnection()
	{
		return new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName className,
										   IBinder service)
			{
				// We've bound to LocalService, cast the IBinder and get LocalService instance
				OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
				stopVPN(binder.getService());
				getAppContext().unbindService(this);
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName)
			{
				// TODO: Need to show user a alert on failure to stop vpn.
				nu.yona.app.utils.Logger.loge(YonaApplication.class, "Error in binding the vpn service with " + componentName.getClassName());
			}

			private void stopVPN(OpenVPNService vpnService)
			{
				ProfileManager.setConntectedVpnProfileDisconnected(YonaActivity.getActivity());
				if (vpnService != null && vpnService.getManagement() != null)
				{
					vpnService.getManagement().stopVPN(false);
				}
			}
		};
	}

	private void bindOpenVPNService(ServiceConnection connection)
	{
		Intent intent = new Intent(YonaActivity.getActivity(), OpenVPNService.class);
		intent.setAction(OpenVPNService.START_SERVICE);
		getAppContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	public void stopOpenVPNService()
	{
		bindOpenVPNService(getVpnServiceStopperConnection());
	}

}

