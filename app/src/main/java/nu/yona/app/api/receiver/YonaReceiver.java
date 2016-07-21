package nu.yona.app.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import nu.yona.app.YonaApplication;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 23/03/16.
 */
public class YonaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_SCREEN_ON:
                Log.e("Screen On", "Screen On");
                startService(context);
                AppUtils.startVPN(context);
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.e("Screen Off", "Screen Off");
                AppUtils.setNullScheduler();
                AppUtils.sendLogToServer(AppConstant.ONE_SECOND);
                AppUtils.stopService(context);
                break;
        }
    }

    private void startService(Context context) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && AppUtils.hasPermission(YonaApplication.getAppContext()))
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            AppUtils.startService(context);
        }
    }
}
