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

import java.util.ArrayList;
import java.util.List;

public class Links extends BaseEntity {

    @SerializedName("self")
    @Expose
    private Href self;
    @SerializedName("edit")
    @Expose
    private Href edit;
    @SerializedName("yona:messages")
    @Expose
    private Href yonaMessages;
    @SerializedName("yona:dailyActivityReports")
    @Expose
    private Href yonaDailyActivityReports;
    @SerializedName("yona:weeklyActivityReports")
    @Expose
    private Href yonaWeeklyActivityReports;
    @SerializedName("yona:newDeviceRequest")
    @Expose
    private Href yonaNewDeviceRequest;
    @SerializedName("yona:appActivity")
    @Expose
    private Href yonaAppActivity;
    @SerializedName("curies")
    @Expose
    private List<Cury> curies = new ArrayList<Cury>();
    @SerializedName("yona:confirmMobileNumber")
    @Expose
    private Href yonaConfirmMobileNumber;
    @SerializedName("yona:resendMobileNumberConfirmationCode")
    @Expose
    private Href resendMobileNumberConfirmationCode;
    @SerializedName("yona:requestPinReset")
    @Expose
    private Href requestPinReset;
    @SerializedName("yona:clearPinReset")
    @Expose
    private Href clearPinReset;
    @SerializedName("yona:verifyPinReset")
    @Expose
    private Href verifyPinReset;
    @SerializedName("yona:user")
    @Expose
    private Href yonaUser;
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
     * @return The yonaMessages
     */
    public Href getYonaMessages() {
        return yonaMessages;
    }

    /**
     * @param yonaMessages The yona:messages
     */
    public void setYonaMessages(Href yonaMessages) {
        this.yonaMessages = yonaMessages;
    }

    /**
     * @return The yonaDailyActivityReports
     */
    public Href getYonaDailyActivityReports() {
        return yonaDailyActivityReports;
    }

    /**
     * @param yonaDailyActivityReports The yona:dailyActivityReports
     */
    public void setYonaDailyActivityReports(Href yonaDailyActivityReports) {
        this.yonaDailyActivityReports = yonaDailyActivityReports;
    }

    /**
     * @return The yonaWeeklyActivityReports
     */
    public Href getYonaWeeklyActivityReports() {
        return yonaWeeklyActivityReports;
    }

    /**
     * @param yonaWeeklyActivityReports The yona:weeklyActivityReports
     */
    public void setYonaWeeklyActivityReports(Href yonaWeeklyActivityReports) {
        this.yonaWeeklyActivityReports = yonaWeeklyActivityReports;
    }

    /**
     * @return The yonaNewDeviceRequest
     */
    public Href getYonaNewDeviceRequest() {
        return yonaNewDeviceRequest;
    }

    /**
     * @param yonaNewDeviceRequest The yona:newDeviceRequest
     */
    public void setYonaNewDeviceRequest(Href yonaNewDeviceRequest) {
        this.yonaNewDeviceRequest = yonaNewDeviceRequest;
    }

    /**
     * @return The yonaAppActivity
     */
    public Href getYonaAppActivity() {
        return yonaAppActivity;
    }

    /**
     * @param yonaAppActivity The yona:appActivity
     */
    public void setYonaAppActivity(Href yonaAppActivity) {
        this.yonaAppActivity = yonaAppActivity;
    }

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

    public Href getYonaConfirmMobileNumber() {
        return yonaConfirmMobileNumber;
    }

    public void setYonaConfirmMobileNumber(Href yonaConfirmMobileNumber) {
        this.yonaConfirmMobileNumber = yonaConfirmMobileNumber;
    }

    public Href getResendMobileNumberConfirmationCode() {
        return resendMobileNumberConfirmationCode;
    }

    public void setResendMobileNumberConfirmationCode(Href resendMobileNumberConfirmationCode) {
        this.resendMobileNumberConfirmationCode = resendMobileNumberConfirmationCode;
    }

    public Href getRequestPinReset() {
        return requestPinReset;
    }

    public void setRequestPinReset(Href requestPinReset) {
        this.requestPinReset = requestPinReset;
    }

    public Href getVerifyPinReset() {
        return verifyPinReset;
    }

    public void setVerifyPinReset(Href verifyPinReset) {
        this.verifyPinReset = verifyPinReset;
    }

    public Href getClearPinReset() {
        return clearPinReset;
    }

    public void setClearPinReset(Href clearPinReset) {
        this.clearPinReset = clearPinReset;
    }

    /**
     *
     * @return
     * The yonaUser
     */
    public Href getYonaUser() {
        return yonaUser;
    }

    /**
     *
     * @param yonaUser
     * The yona:user
     */
    public void setYonaUser(Href yonaUser) {
        this.yonaUser = yonaUser;
    }
    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
