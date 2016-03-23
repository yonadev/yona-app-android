package nu.yona.app.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import nu.yona.app.YonaApplication;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 23/03/16.
 */
public class YonaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && AppUtils.hasPermission(YonaApplication.getAppContext())) {
                AppUtils.startService(context);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                AppUtils.startService(context);
            }
        }
    }
}
