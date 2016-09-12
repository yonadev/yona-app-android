/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.Foreground;

/**
 * Created by kinnarvasa on 16/03/16.
 */
public class YonaApplication extends Application {

    private static YonaApplication mContext;
    private static EventChangeManager eventChangeManager;
    private Tracker tracker;

    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static synchronized YonaApplication getAppContext() {
        return mContext;
    }

    /**
     * Gets event change manager.
     *
     * @return the event change manager
     */
    public static EventChangeManager getEventChangeManager() {
        return eventChangeManager;
    }

    @Override
    public void onCreate() {
        enableStickMode();
        super.onCreate();
        mContext = this;
        eventChangeManager = new EventChangeManager();
        Foreground.init(this);
    }

    private void enableStickMode() {
        if (getResources().getBoolean(R.bool.developerMode)) {
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

    public static Tracker getTracker() {
        if (mContext.tracker == null) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);

            String key = AnalyticsConstant.APP_KEY;
            Tracker tracker = analytics.newTracker(key);
            tracker.setSessionTimeout(600);
            //tracker.setScreenName(getStationName() + " MainView");
            try {
                tracker.setLanguage(Locale.getDefault().getDisplayLanguage());
                tracker.enableExceptionReporting(true);
                if (getEventChangeManager().getDataState().getUser() != null
                        && getEventChangeManager().getDataState().getUser().getLinks() != null
                        && getEventChangeManager().getDataState().getUser().getLinks().getSelf() != null
                        && !TextUtils.isEmpty(getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref())) {
                    tracker.set("&uid", getEventChangeManager().getDataState().getUser().getLinks().getSelf().getHref());
                }
                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                tracker.setAppVersion(pInfo.versionName + mContext.getString(R.string.space) + pInfo.versionCode);
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(YonaApplication.class.getSimpleName(), e.getMessage());
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
}
