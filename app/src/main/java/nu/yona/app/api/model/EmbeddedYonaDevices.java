package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonIgnore;

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

	@JsonIgnore
	public YonaDevice getCurrentDevice()
	{
		for (YonaDevice device : yonaDevices)
		{
			if (device.isRequestingDevice())
			{
				return device;
			}
		}
		throw new IllegalStateException("Current device not found. Number of devices: " + yonaDevices.size());
	}
}
