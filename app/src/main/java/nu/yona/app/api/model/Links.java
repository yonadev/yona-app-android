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

import java.util.ArrayList;
import java.util.List;

public class Links {

    @SerializedName("curies")
    @Expose
    private List<Cury> curies = new ArrayList<Cury>();
    @SerializedName("self")
    @Expose
    private Href self;
    @SerializedName("edit")
    @Expose
    private Href edit;
    @SerializedName("yona:confirmMobileNumber")
    @Expose
    private Href yonaConfirmMobileNumber;
    @SerializedName("yona:resendMobileNumberConfirmationCode")
    @Expose
    private Href resendMobileNumberConfirmationCode;

    /**
     * @return The curies
     */
    public List<Cury> getCuries() {
        return curies;
    }

    /**
     * @param curies The curies
     */
    public void setCuries(List<Cury> curies) {
        this.curies = curies;
    }

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

    /**
     * @return The edit
     */
    public Href getEdit() {
        return edit;
    }

    /**
     * @param edit The edit
     */
    public void setEdit(Href edit) {
        this.edit = edit;
    }

    /**
     * @return The yonaConfirmMobileNumber
     */
    public Href getYonaConfirmMobileNumber() {
        return yonaConfirmMobileNumber;
    }

    /**
     * @param yonaConfirmMobileNumber The yona:confirmMobileNumber
     */
    public void setYonaConfirmMobileNumber(Href yonaConfirmMobileNumber) {
        this.yonaConfirmMobileNumber = yonaConfirmMobileNumber;
    }

    public Href getResendMobileNumberConfirmationCode() {
        return resendMobileNumberConfirmationCode;
    }

    public void setResendMobileNumberConfirmationCode(Href resendMobileNumberConfirmationCode) {
        this.resendMobileNumberConfirmationCode = resendMobileNumberConfirmationCode;
    }

}
