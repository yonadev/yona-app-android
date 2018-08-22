package nu.yona.app.api.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nu.yona.app.BuildConfig;
import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.settings.SettingsFragment;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.ui.YonaActivity.getActivity;

public class AppMetaInfo {

    @SerializedName("operatingSystem")
    @Expose
    private String operatingSystem = YonaApplication.getAppContext().getString(R.string.operating_system);

    @SerializedName("appVersion")
    @Expose
    private String appVersion  =  BuildConfig.VERSION_NAME;

    @SerializedName("appVersionCode")
    @Expose
    private Integer appVersionCode = BuildConfig.VERSION_CODE;


    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

}
