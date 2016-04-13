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


public class Links_ {

    @SerializedName("self")
    @Expose
    private Href self;

    /**
     * @return The self
     */
    public Href getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(Href self) {
        this.self = self;
    }

}
