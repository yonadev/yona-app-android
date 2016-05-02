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

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * The type Yona buddies.
 */
public class YonaBuddies extends BaseEntity {

    @SerializedName("_embedded")
    @Expose
    private EmbeddedYonaBuddies Embedded;
    @SerializedName("_links")
    @Expose
    private Links_ links;

    /**
     * Gets embedded.
     *
     * @return The Embedded
     */
    public EmbeddedYonaBuddies getEmbedded() {
        return Embedded;
    }

    /**
     * Sets embedded.
     *
     * @param Embedded The _embedded
     */
    public void setEmbedded(EmbeddedYonaBuddies Embedded) {
        this.Embedded = Embedded;
    }

    /**
     * Gets links.
     *
     * @return The links
     */
    public Links_ getLinks() {
        return links;
    }

    /**
     * Sets links.
     *
     * @param links The links
     */
    public void setLinks(Links_ links) {
        this.links = links;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

}
