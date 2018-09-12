package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class EmbeddedYonaActivity extends BaseEntity
{
	@SerializedName("_embedded")
	@Expose
	private Embedded embedded;
	@SerializedName("_links")
	@Expose
	private Links links;
	@SerializedName("page")
	@Expose
	private Page page;
	@SerializedName("@dayActivityList")
	@Expose
	private List<DayActivity> dayActivityList = new ArrayList<>();
	@SerializedName("@weekActivity")
	@Expose
	private List<WeekActivity> weekActivityList = new ArrayList<>();

	/**
	 * Gets embedded.
	 *
	 * @return The embedded
	 */
	public Embedded getEmbedded()
	{
		return embedded;
	}

	/**
	 * Sets embedded.
	 *
	 * @param embedded The _embedded
	 */
	public void setEmbedded(Embedded embedded)
	{
		this.embedded = embedded;
	}

	/**
	 * Gets links.
	 *
	 * @return The links
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

	/**
	 * Gets page.
	 *
	 * @return The page
	 */
	public Page getPage()
	{
		return page;
	}

	/**
	 * Sets page.
	 *
	 * @param page The page
	 */
	public void setPage(Page page)
	{
		this.page = page;
	}

	/**
	 * Gets day activity list.
	 *
	 * @return the day activity list
	 */
	public List<DayActivity> getDayActivityList()
	{
		return this.dayActivityList;
	}

	/**
	 * Sets day activity list.
	 *
	 * @param dayActivityList the day activity list
	 */
	public void setDayActivityList(List<DayActivity> dayActivityList)
	{
		this.dayActivityList = dayActivityList;
	}

	/**
	 * Gets week activity list.
	 *
	 * @return the week activity list
	 */
	public List<WeekActivity> getWeekActivityList()
	{
		return this.weekActivityList;
	}

	/**
	 * Sets week activity list.
	 *
	 * @param weekActivityList the week activity list
	 */
	public void setWeekActivityList(List<WeekActivity> weekActivityList)
	{
		this.weekActivityList = weekActivityList;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
