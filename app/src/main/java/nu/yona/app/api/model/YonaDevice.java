package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class YonaDevice extends BaseEntity
{
	@SerializedName("_links")
	@Expose
	private Links links;
	@SerializedName("sslRootCertCN")
	@Expose
	private String sslRootCertCN;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("requestingDevice")
	@Expose
	private boolean requestingDevice;

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	/**
	 * Gets links.
	 *
	 * @return The Links
	 */
	public Links getLinks()
	{
		return links;
	}

	/**
	 * Sets links.
	 *
	 * @param links The _links
	 */
	public void setLinks(Links links)
	{
		this.links = links;
	}

	public String getSslRootCertCN()
	{
		return this.sslRootCertCN;
	}

	public void setSslRootCertCN(String sslRootCertCN)
	{
		this.sslRootCertCN = sslRootCertCN;
	}

	public String getName()
	{
		return name;
	}

	public boolean isRequestingDevice()
	{
		return requestingDevice;
	}

	public String retrevePostOpenAppEventLink()
	{
		return this.links.getYonaPostOpenAppEvent().getHref();
	}

	public String retreiveLinkForPostingDeviceAppActivity()
	{
		return this.links.getYonaAppActivity().getHref();
	}

	public String retreiveYonaSslRootCert()
	{
		return this.links.getSslRootCert().getHref();
	}
}
