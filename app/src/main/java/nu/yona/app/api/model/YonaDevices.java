package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YonaDevices extends BaseEntity
{
	@SerializedName("_embedded")
	@Expose
	private EmbeddedYonaDevices Embedded;
	@SerializedName("_links")
	@Expose
	private Links_ links;

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	/**
	 * Gets embedded.
	 *
	 * @return The Embedded
	 */
	public EmbeddedYonaDevices getEmbedded()
	{
		return Embedded;
	}

	/**
	 * Sets embedded.
	 *
	 * @param Embedded The _embedded
	 */
	public void setEmbedded(EmbeddedYonaDevices Embedded)
	{
		this.Embedded = Embedded;
	}

	/**
	 * Gets links.
	 *
	 * @return The links
	 */
	public Links_ getLinks()
	{
		return links;
	}

	/**
	 * Sets links.
	 *
	 * @param links The links
	 */
	public void setLinks(Links_ links)
	{
		this.links = links;
	}
}
