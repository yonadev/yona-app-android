package nu.yona.app.api.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 23/03/16.
 */
public class YonaReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_SCREEN_ON:
                Log.e("Screen On", "Screen On");
                startService(context);
                AppUtils.startVPN(context, false);
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.e("Screen Off", "Screen Off");
                AppUtils.setNullScheduler();
                AppUtils.sendLogToServer(AppConstant.ONE_SECOND);
                AppUtils.stopService(context);
                break;
            case AppConstant.RESTART_DEVICE:
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_DEVICE_RESTART_REQUIRE, null);
                break;
            case AppConstant.RESTART_VPN:
                Log.e("Show restart VPN calll", "Show restart vpn call......");
                showRestartVPN(mContext.getString(R.string.vpn_disconnected));
                break;
            default:
                break;
        }
    }

    private void startService(Context context) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && AppUtils.hasPermission(YonaApplication.getAppContext()))
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            AppUtils.startService(context);
        }
    }

    private void showRestartVPN(final String message) {
        Intent intent = AppUtils.startVPN(mContext, true);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis() + 10000, intent, 0);

        Notification notification = new Notification.Builder(mContext).setContentTitle(mContext.getString(R.string.appname))
                .setContentText(message)
                .setTicker(mContext.getString(R.string.appname))
                .setWhen(0)
                .setVibrate(new long[]{1, 1, 1})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
