package nu.yona.app.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import nu.yona.app.YonaApplication;
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
                Log.e("REStart Service", "ReStart service");
                restartService(context);
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.e("Stop Service", "Stop service");
                AppUtils.stopService(context);
                break;
        }
    }

    private void restartService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && AppUtils.hasPermission(YonaApplication.getAppContext())) {
            AppUtils.restartService(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            AppUtils.restartService(context);
        }
    }
}
