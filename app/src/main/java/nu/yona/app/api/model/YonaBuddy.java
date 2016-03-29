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

public class YonaBuddy {

    @SerializedName("_links")
    @Expose
    private Links Links;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("sendingStatus")
    @Expose
    private String sendingStatus;
    @SerializedName("receivingStatus")
    @Expose
    private String receivingStatus;
    @SerializedName("_embedded")
    @Expose
    private Embedded Embedded;

    /**
     * @return The Links
     */
    public Links getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(Links Links) {
        this.Links = Links;
    }

    /**
     * @return The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

}
