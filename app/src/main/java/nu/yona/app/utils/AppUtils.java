/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import net.hockeyapp.android.ExceptionHandler;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Random;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;


/**
 * Created by kinnarvasa on 21/03/16.
 */
public class AppUtils {

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.BLUE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    /**
     * @return false if user has not given permission for package access so far.
     */
    public static boolean hasPermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }

    }

    public static int getDp(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * @param serviceClass Name of class to check whether running or not.
     * @return true if service already running else return false
     */
    public static boolean isYonaServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start service once user grant permission for application permission (for 5.1+ version)
     */
    public static void startService(Context context) {
//        if (!AppUtils.isYonaServiceRunning(context, ActivityMonitorService.class)) {
//            context.startService(new Intent(context, ActivityMonitorService.class));
//        }
    }

    /**
     * Generate Random String length of 20
     *
     * @return
     */
    public static String getRandomString(int charLimit) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < charLimit; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * get the two digit length if digit length is one
     *
     * @param digit
     * @return digit
     */
    public static String getHourDigit(String digit) {
        if (digit.length() != 2) {
            digit = "0" + digit;
        }
        return digit;
    }

    public static String getTimeForOTP(String time) {
        try {
            return new Period(time, PeriodType.hours()).getHours() + "";
        } catch (Exception e) {
            AppUtils.throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
        return time;
    }

    /**
     * @param className class name where exception throws
     * @param e         Error
     * @param t         Current Thread (Thread.currentThread())
     * @param listener  DataLoadListener to update UI
     */
    public static void throwException(String className, Exception e, Thread t, DataLoadListener listener) {
        if (YonaApplication.getAppContext().getResources().getBoolean(R.bool.enableHockyTracking)) {
            ExceptionHandler.saveException(e, t, YonaApplication.getYonaCustomCrashManagerListener());
        } else {
            Log.e(className, e.getMessage());
        }

        if(listener != null) {
            if (e != null && e.getMessage() != null) {
                listener.onError(new ErrorMessage(e.getMessage()));
            } else {
                listener.onError(YonaApplication.getAppContext().getString(R.string.error_message));
            }
        }
    }
}
