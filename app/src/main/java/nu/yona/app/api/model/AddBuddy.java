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

/**
 * Created by kinnarvasa on 28/04/16.
 */
public class AddBuddy {
    @SerializedName("sendingStatus")
    @Expose
    private String sendingStatus;
    @SerializedName("receivingStatus")
    @Expose
    private String receivingStatus;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("_embedded")
    @Expose
    private Embedded Embedded;

    /**
     * @return The sendingStatus
     */
    public String getSendingStatus() {
        return sendingStatus;
    }

    /**
     * @param sendingStatus The sendingStatus
     */
    public void setSendingStatus(String sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    /**
     * @return The receivingStatus
     */
    public String getReceivingStatus() {
        return receivingStatus;
    }

    /**
     * @param receivingStatus The receivingStatus
     */
    public void setReceivingStatus(String receivingStatus) {
        this.receivingStatus = receivingStatus;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The Embedded
     */
    public nu.yona.app.api.model.Embedded getEmbedded() {
        return Embedded;
    }

    /**
     * @param Embedded The _embedded
     */
    public void setEmbedded(nu.yona.app.api.model.Embedded Embedded) {
        this.Embedded = Embedded;
    }

}
