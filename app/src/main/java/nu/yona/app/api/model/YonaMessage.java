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
 * Created by kinnarvasa on 09/05/16.
 */
public class YonaMessage {
    @SerializedName("creationTime")
    @Expose
    private String creationTime;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("_links")
    @Expose
    private Links Links;
    @SerializedName("_embedded")
    @Expose
    private Embedded Embedded;
    @SerializedName("@type")
    @Expose
    private String Type;

    /**
     *
     * @return
     * The creationTime
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     *
     * @param creationTime
     * The creationTime
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    /**
     *
     * @return
     * The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname
     * The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The Links
     */
    public nu.yona.app.api.model.Links getLinks() {
        return Links;
    }

    /**
     *
     * @param Links
     * The _links
     */
    public void setLinks(nu.yona.app.api.model.Links Links) {
        this.Links = Links;
    }

    /**
     *
     * @return
     * The Embedded
     */
    public Embedded getEmbedded() {
        return Embedded;
    }

    /**
     *
     * @param Embedded
     * The _embedded
     */
    public void setEmbedded(Embedded Embedded) {
        this.Embedded = Embedded;
    }

    /**
     *
     * @return
     * The Type
     */
    public String getType() {
        return Type;
    }

    /**
     *
     * @param Type
     * The @type
     */
    public void setType(String Type) {
        this.Type = Type;
    }
}
