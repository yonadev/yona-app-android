package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nu.yona.app.BuildConfig;

public class AppMetaInfo
{

	@SerializedName("operatingSystem")
	@Expose
	private String operatingSystem;

	@SerializedName("appVersion")
	@Expose
	private String appVersion;

	@SerializedName("appVersionCode")
	@Expose
	private int appVersionCode;

	private static final AppMetaInfo theInstance = new AppMetaInfo("ANDROID", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

	private AppMetaInfo(String operatingSystem, String appVersion, int appVersionCode)
	{
		this.operatingSystem = operatingSystem;
		this.appVersion = appVersion;
		this.appVersionCode = appVersionCode;
	}

	public static AppMetaInfo getInstance()
	{
		return theInstance;
	}

	public int getAppVersionCode()
	{
		return appVersionCode;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public String getOperatingSystem()
	{
		return operatingSystem;
	}


}
