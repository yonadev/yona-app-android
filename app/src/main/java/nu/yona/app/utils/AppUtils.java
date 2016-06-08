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
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.Random;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.receiver.YonaReceiver;
import nu.yona.app.api.service.ActivityMonitorService;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.timepicker.time.Timepoint;


/**
 * Created by kinnarvasa on 21/03/16.
 */
public class AppUtils {
    private static InputFilter filter;
    private static boolean submitPressed;
    private static Intent activityMonitorIntent;

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
     * Start service once user grant permission for application permission (for 5.1+ version)
     *
     * @param context the context
     */
    private static void startService(Context context) {
        try {
            activityMonitorIntent = new Intent(context, ActivityMonitorService.class);
            context.startService(activityMonitorIntent);
        } catch (Exception e) {
            throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    public static void stopService(Context context) {
        try {
            if (activityMonitorIntent != null) {
                context.stopService(activityMonitorIntent);
                activityMonitorIntent = null;
            }
        } catch (Exception e) {
            throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    public static void restartService(Context context) {
        stopService(context);
        startService(context);
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
     * This will register receiver for different events like screen on-off, boot, connectivity etc.
     */
    public static void registerReceiver(Context context) {
        YonaReceiver receiver = new YonaReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        context.registerReceiver(receiver, filter);
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
            StringBuffer buffer = new StringBuffer();
            Period period = new Period(time);
            if (period.getHours() > 0) {
                buffer.append(YonaApplication.getAppContext().getString(R.string.hours, period.getHours() + ""));
            }
            if (period.getMinutes() > 0) {
                buffer.append(YonaApplication.getAppContext().getString(R.string.minute, period.getMinutes() + ""));
            }
            if (period.getSeconds() > 0) {
                buffer.append(YonaApplication.getAppContext().getString(R.string.seconds, period.getSeconds() + ""));
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

    /**
     * Is submit pressed boolean.
     *
     * @return the boolean
     */
    public static boolean isSubmitPressed() {
        return AppUtils.submitPressed;
    }

    /**
     * Sets submit pressed.
     *
     * @param submitPressed the submit pressed
     */
    public static void setSubmitPressed(boolean submitPressed) {
        AppUtils.submitPressed = submitPressed;
    }
}
