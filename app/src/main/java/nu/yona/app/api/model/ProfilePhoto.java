package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePhoto extends BaseEntity
{

	@SerializedName("_links")
	@Expose
	private LinksProfilePhoto links;

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

	public LinksProfilePhoto getLinks()
	{
		return links;
	}

	public void setLinks(LinksProfilePhoto links)
	{
		this.links = links;
	}
}
