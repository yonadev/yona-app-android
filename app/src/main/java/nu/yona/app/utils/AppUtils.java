/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.utils;

import android.annotation.TargetApi;
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
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import net.hockeyapp.android.ExceptionHandler;

import org.joda.time.Period;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.activities.DisconnectVPN;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.User;
import nu.yona.app.api.receiver.YonaReceiver;
import nu.yona.app.api.service.ActivityMonitorService;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.timepicker.time.Timepoint;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class AppUtils {
    private static InputFilter filter;
    private static boolean submitPressed;
    private static Intent activityMonitorIntent;
    private static ScheduledExecutorService scheduler;
    private static YonaReceiver receiver;
    private static int trialCertificateCount = 0, trialVPNCount = 0;

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
    public static void startService(Context context) {
        try {
            activityMonitorIntent = new Intent(context, ActivityMonitorService.class);
            context.startService(activityMonitorIntent);
        } catch (Exception e) {
            throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    /**
     * Stop service.
     *
     * @param context the context
     */
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

    /**
     * Generate Random String length of 20
     *
     * @param charLimit the char limit
     * @return random string
     */
    public static String getRandomString(int charLimit) {
        char[] chars = "abcdefghijkmnopqrstuvwxyz0123456789ABCDEFGHJKLMNOPQRSTUVWXYZ".toCharArray();
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
     *
     * @param context the context
     */
    public static void registerReceiver(Context context) {
        Log.e("REgister", "Register REceiver from apputil 187");
        receiver = new YonaReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("com.yona.app.RESTART_DEVICE");
        filter.addAction("com.yona.app.RESTART_VPN");
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
        try {
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
        } catch (Exception x) {
            Log.e(AppUtils.class.getSimpleName(), "Exception in throwExcption method");
        }
    }

    /**
     * Gets filter.
     *
     * @return the filter
     */
//    public static InputFilter getFilter() {
//        if (filter == null) {
//            filter = new InputFilter() {
//                @Override
//                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                    String blockCharacterSet = "~#^&|$%*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-(:'(:O 1234567890";
//                    if (source != null && blockCharacterSet.contains(("" + source))) {
//                        return "";
//                    }
//                    return null;
//                }
//            };
//        }
//        return filter;
//    }

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

    /**
     * Send log to server.
     *
     * @param delayMilliseconds the delay milliseconds
     */
    public static void sendLogToServer(long delayMilliseconds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                APIManager.getInstance().getActivityManager().postAllDBActivities();
            }
        }, delayMilliseconds);
    }

    /**
     * Gets initialize scheduler.
     *
     * @return the initialize scheduler
     */
    public static ScheduledExecutorService getInitializeScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduler;
    }

    /**
     * Gets scheduler.
     *
     * @return the scheduler
     */
    public static ScheduledExecutorService getScheduler() {
        return AppUtils.scheduler;
    }

    /**
     * Sets null scheduler.
     */
    public static void setNullScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        scheduler = null;
    }

    public static void stopVPN(Context context) {
        String profileUUID = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(PreferenceConstant.PROFILE_UUID, "");
        VpnProfile profile = ProfileManager.get(context, profileUUID);
        if (VpnStatus.isVPNActive() && ProfileManager.getLastConnectedVpn() == profile) {
            Intent disconnectVPN = new Intent(context, DisconnectVPN.class);
            disconnectVPN.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(disconnectVPN);
        }
    }

    public static Intent startVPN(Context context, boolean returnIntent) {
        String profileUUID = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(PreferenceConstant.PROFILE_UUID, "");
        VpnProfile profile = ProfileManager.get(context, profileUUID);
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();


        if (profile != null && !VpnStatus.isVPNActive() && user != null && user.getVpnProfile() != null) {
            profile.mUsername = !TextUtils.isEmpty(user.getVpnProfile().getVpnLoginID()) ? user.getVpnProfile().getVpnLoginID() : "";
            profile.mPassword = !TextUtils.isEmpty(user.getVpnProfile().getVpnPassword()) ? user.getVpnProfile().getVpnPassword() : "";
            if (returnIntent) {
                return getVPNIntent(profile, context);
            } else {
                startVPN(profile, context);
            }
        }
        return null;
    }

    private static void startVPN(VpnProfile profile, Context context) {
        if (profile != null) {
            ProfileManager.getInstance(context).saveProfile(context, profile);
            Intent intent = new Intent(context, LaunchVPN.class);
            intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
            intent.setAction(Intent.ACTION_MAIN);
            intent.putExtra(AppConstant.FROM_LOGIN, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static Intent getVPNIntent(VpnProfile profile, Context context) {
        if (profile != null) {
            ProfileManager.getInstance(context).saveProfile(context, profile);
            Intent intent = new Intent(context, LaunchVPN.class);
            intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
            intent.setAction(Intent.ACTION_MAIN);
            intent.putExtra(AppConstant.FROM_LOGIN, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
        return null;
    }

    public static void downloadCertificates() {
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();
        if (user != null && user.getLinks() != null) {
            if (user.getLinks().getSslRootCert() != null
                    && YonaApplication.getEventChangeManager().getSharedPreference().getRootCertPath() == null) {
                new DownloadFileFromURL(user.getLinks().getSslRootCert().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        if (result != null && !TextUtils.isEmpty(result.toString())) {
                            YonaApplication.getEventChangeManager().getSharedPreference().setRootCertPath(result.toString());
                            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_ROOT_CERTIFICATE_DOWNLOADED, null);
                        }
                        Log.e("Download", "Download successful: " + result.toString());
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        Log.e("Download", "Download fail");
                        trialCertificateCount++;
                        if (trialCertificateCount < 3) {
                            downloadCertificates();
                        }
                    }
                });
            }
        }
    }

    public static void downloadVPNProfile() {
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();
        if (user.getVpnProfile() != null && user.getVpnProfile().getLinks() != null && user.getVpnProfile().getLinks().getOvpnProfile() != null
                && YonaApplication.getEventChangeManager().getSharedPreference().getVPNProfilePath() == null) {
            new DownloadFileFromURL(user.getVpnProfile().getLinks().getOvpnProfile().getHref(), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result != null && !TextUtils.isEmpty(result.toString())) {
                        YonaApplication.getEventChangeManager().getSharedPreference().setVPNProfilePath(result.toString());
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_VPN_CERTIFICATE_DOWNLOADED, null);
                    }

                    Log.e("Download", "Download successful: " + result.toString());
                }

                @Override
                public void onError(Object errorMessage) {
                    Log.e("Download", "Download fail");
                    trialVPNCount++;
                    if (trialVPNCount < 3) {
                        downloadVPNProfile();
                    }
                }
            });
        }
    }

    public static byte[] getCACertificate(String path) {
        if (path != null && !TextUtils.isEmpty(path)) {
            File file = new File(path);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                return bytes;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean checkCACertificate() {
        boolean isCertExist = false;
        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            if (ks != null) {
                ks.load(null, null);
                Enumeration aliases = ks.aliases();
                if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getSslRootCertCN() != null) {
                    String caCertName = YonaApplication.getEventChangeManager().getDataState().getUser().getSslRootCertCN();
                    if (!TextUtils.isEmpty(caCertName)) {
                        while (aliases.hasMoreElements()) {
                            String alias = (String) aliases.nextElement();
                            java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);
                            if (cert.getIssuerDN().getName().contains(caCertName)) {
                                isCertExist = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throwException(AppUtils.class.getSimpleName(), e, Thread.currentThread(), null);
        }
        return isCertExist;

    }

    public static boolean checkKeyboardOpen(View view) {
        int defaultKeyboardDp = 100;
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        int estimatedKeyboardHeight = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultKeyboardDp, view.getResources().getDisplayMetrics());

        view.getWindowVisibleDisplayFrame(r);
        int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
        return heightDiff > estimatedKeyboardHeight;
    }
}
