/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class ActivityCategories extends BaseEntity {
    @SerializedName("_embedded")
    @Expose
    private EmbeddedActivityCategories embeddedActivityCategories;

    @SerializedName("_links")
    @Expose
    private Links links;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public EmbeddedActivityCategories getEmbeddedActivityCategories() {
        return embeddedActivityCategories;
    }

    public void setEmbeddedActivityCategories(EmbeddedActivityCategories embeddedActivityCategories) {
        this.embeddedActivityCategories = embeddedActivityCategories;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
