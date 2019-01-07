package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedYonaDevices extends BaseEntity
{
	@SerializedName("yona:devices")
	@Expose
	private final List<YonaDevice> yonaDevices = new ArrayList<>();

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	/**
	 * Gets yona devices.
	 *
	 * @return The yonaDevices
	 */
	public List<YonaDevice> getYonaDevices()
	{
		return yonaDevices;
	}

	public YonaDevice getCurrentDevice()
	{
		if (this.yonaDevices.size() == 1)
		{
			return this.yonaDevices.get(0);
		}
		YonaDevice currentDevice = this.yonaDevices.get(0);
		for (int d = 0; d < yonaDevices.size(); d++)
		{
			YonaDevice device = this.yonaDevices.get(d);
			if (device.isRequestingDevice())
			{
				currentDevice = device;
				break;
			}
		}
		return currentDevice;
	}
}
