/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import net.hockeyapp.android.ExceptionHandler;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Random;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.timepicker.time.Timepoint;


/**
 * Created by kinnarvasa on 21/03/16.
 */
public class AppUtils {
    private static InputFilter filter;

    /**
     * Gets circle bitmap.
     *
     * @param bitmap the bitmap
     * @return the circle bitmap
     */
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
     * Has permission boolean.
     *
     * @param context the context
     * @return false if user has not given permission for package access so far.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasPermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }

    }

    /**
     * Gets dp.
     *
     * @param context the context
     * @param dp      the dp
     * @return the dp
     */
    public static int getDp(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Is yona service running boolean.
     *
     * @param context      the context
     * @param serviceClass Name of class to check whether running or not.
     * @return true if service already running else return false
     */
    public static boolean isYonaServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start service once user grant permission for application permission (for 5.1+ version)
     *
     * @param context the context
     */
    public static void startService(Context context) {
//        if (!AppUtils.isYonaServiceRunning(context, ActivityMonitorService.class)) {
//            context.startService(new Intent(context, ActivityMonitorService.class));
//        }
    }

    /**
     * Generate Random String length of 20
     *
     * @param charLimit the char limit
     * @return random string
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
     * @param digit the digit
     * @return digit hour digit
     */
    public static String getHourDigit(String digit) {
        if (digit.length() != 2) {
            digit = "0" + digit;
        }
        return digit;
    }

    /**
     * Gets time for otp.
     *
     * @param time the time
     * @return the time for otp
     */
    public static String getTimeForOTP(String time) {
        try {
            int MINUTE = 60; // 60 seconds
            int HOUR = 3600; // 3600 seconds
            StringBuffer buffer = new StringBuffer();
            int seconds = new Period(time, PeriodType.seconds()).getSeconds();
            if(seconds / HOUR > 0) {
                buffer.append(seconds/HOUR + " hour(s) ");
                seconds = seconds%HOUR;
            }
            if(seconds /MINUTE >0){
                buffer.append(seconds/MINUTE + " minute(s) ");
                seconds = seconds % MINUTE;
            }
            if(seconds > 0) {
                buffer.append(seconds + " second(s) ");
            }
            return buffer.toString();
        } catch (Exception e) {
            AppUtils.throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
        return time;
    }

    /**
     * Throw exception.
     *
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

        if (listener != null) {
            if (e != null && e.getMessage() != null) {
                listener.onError(new ErrorMessage(e.getMessage()));
            } else {
                listener.onError(YonaApplication.getAppContext().getString(R.string.error_message));
            }
        }
    }

    /**
     * Gets filter.
     *
     * @return the filter
     */
    public static InputFilter getFilter() {
        if (filter == null) {
            filter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    String blockCharacterSet = "~#^&|$%*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O 1234567890";
                    if (source != null && blockCharacterSet.contains(("" + source))) {
                        return "";
                    }
                    return null;
                }
            };
        }
        return filter;
    }

    /**
     * Get splited time. ex: 21:00 - 23:54 whill return 21:00 and 23:54
     *
     * @param time the time
     * @return string [ ]
     */
    public static String[] getSplitedTime(String time) {
        return time.split("-", 2);
    }


    /**
     * Get splited hr string [ ].
     *
     * @param time the time
     * @return the string [ ]
     */
    public static String[] getSplitedHr(String time) {
        return time.split(":", 2);
    }


    /**
     * Gets time in milliseconds.
     *
     * @param time the time
     * @return the time in milliseconds
     */
    public static Timepoint getTimeInMilliseconds(String time) {
        if (!TextUtils.isEmpty(time) && time.contains(":")) {
            String[] min = time.split(":");
            return new Timepoint(Integer.parseInt(min[0]), Integer.parseInt(min[1]), 0);
        } else {
            return new Timepoint(0, 0, 0);
        }
    }

    /**
     * convert one digit number to two digit number appending with 0 if its length is one else return same
     *
     * @param time the time
     * @return the time digit
     */
    public static String getTimeDigit(int time) {
        String timeDigit = String.valueOf(time);
        if (timeDigit.length() == 1) {
            timeDigit = "0" + timeDigit;
        }
        return timeDigit;
    }
}
