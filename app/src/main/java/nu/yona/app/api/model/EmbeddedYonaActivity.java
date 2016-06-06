package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 06/06/16.
 */

public class EmbeddedYonaActivity {
    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;
    @SerializedName("_links")
    @Expose
    private Links links;
    @SerializedName("page")
    @Expose
    private Page page;

    /**
     * @return The embedded
     */
    public Embedded getEmbedded() {
        return embedded;
    }

    /**
     * @param embedded The _embedded
     */
    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }

    /**
     * @return The links
     */
    public Links getLinks() {
        return links;
    }

    /**
     * @param links The _links
     */
    public void setLinks(Links links) {
        this.links = links;
    }

    /**
     * @return The page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Page page) {
        this.page = page;
    }

}
