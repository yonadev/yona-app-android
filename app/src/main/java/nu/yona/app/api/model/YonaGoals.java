/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YonaGoals {

    @SerializedName("_embedded")
    @Expose
    private Embedded Embedded;
    @SerializedName("links")
    @Expose
    private Links links;

    /**
     * @return The Embedded
     */
    public Embedded getEmbedded() {
        return Embedded;
    }

    /**
     * @param Embedded The _embedded
     */
    public void setEmbedded(Embedded Embedded) {
        this.Embedded = Embedded;
    }

    /**
     * @return The links
     */
    public Links getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(Links links) {
        this.links = links;
    }

}
