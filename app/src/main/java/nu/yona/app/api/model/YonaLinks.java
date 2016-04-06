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


public class YonaLinks {

    @SerializedName("yona:confirmMobileNumber")
    @Expose
    private YonaConfirmMobileNumber yonaConfirmMobileNumber;
    @SerializedName("yona:messages")
    @Expose
    private YonaMessages yonaMessages;
    @SerializedName("yona:weeklyActivityReports")
    @Expose
    private YonaWeeklyActivityReports yonaWeeklyActivityReports;
    @SerializedName("yona:dailyActivityReports")
    @Expose
    private YonaDailyActivityReports yonaDailyActivityReports;
    @SerializedName("yona:newDeviceRequest")
    @Expose
    private YonaNewDeviceRequest yonaNewDeviceRequest;
    @SerializedName("yona:appActivity")
    @Expose
    private YonaAppActivity yonaAppActivity;
    @SerializedName("curies")
    @Expose
    private List<Cury> curies = new ArrayList<Cury>();
    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("edit")
    @Expose
    private Edit edit;

    /**
     * @return The yonaConfirmMobileNumber
     */
    public YonaConfirmMobileNumber getYonaConfirmMobileNumber() {
        return yonaConfirmMobileNumber;
    }

    /**
     * @param yonaConfirmMobileNumber The yona:confirmMobileNumber
     */
    public void setYonaConfirmMobileNumber(YonaConfirmMobileNumber yonaConfirmMobileNumber) {
        this.yonaConfirmMobileNumber = yonaConfirmMobileNumber;
    }

    /**
     * @return The yonaMessages
     */
    public YonaMessages getYonaMessages() {
        return yonaMessages;
    }

    /**
     * @param yonaMessages The yona:messages
     */
    public void setYonaMessages(YonaMessages yonaMessages) {
        this.yonaMessages = yonaMessages;
    }

    /**
     * @return The yonaWeeklyActivityReports
     */
    public YonaWeeklyActivityReports getYonaWeeklyActivityReports() {
        return yonaWeeklyActivityReports;
    }

    /**
     * @param yonaWeeklyActivityReports The yona:weeklyActivityReports
     */
    public void setYonaWeeklyActivityReports(YonaWeeklyActivityReports yonaWeeklyActivityReports) {
        this.yonaWeeklyActivityReports = yonaWeeklyActivityReports;
    }

    /**
     * @return The yonaDailyActivityReports
     */
    public YonaDailyActivityReports getYonaDailyActivityReports() {
        return yonaDailyActivityReports;
    }

    /**
     * @param yonaDailyActivityReports The yona:dailyActivityReports
     */
    public void setYonaDailyActivityReports(YonaDailyActivityReports yonaDailyActivityReports) {
        this.yonaDailyActivityReports = yonaDailyActivityReports;
    }

    /**
     * @return The yonaNewDeviceRequest
     */
    public YonaNewDeviceRequest getYonaNewDeviceRequest() {
        return yonaNewDeviceRequest;
    }

    /**
     * @param yonaNewDeviceRequest The yona:newDeviceRequest
     */
    public void setYonaNewDeviceRequest(YonaNewDeviceRequest yonaNewDeviceRequest) {
        this.yonaNewDeviceRequest = yonaNewDeviceRequest;
    }

    /**
     * @return The yonaAppActivity
     */
    public YonaAppActivity getYonaAppActivity() {
        return yonaAppActivity;
    }

    /**
     * @param yonaAppActivity The yona:appActivity
     */
    public void setYonaAppActivity(YonaAppActivity yonaAppActivity) {
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

    /**
     * @return The self
     */
    public Self getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(Self self) {
        this.self = self;
    }

    /**
     * @return The edit
     */
    public Edit getEdit() {
        return edit;
    }

    /**
     * @param edit The edit
     */
    public void setEdit(Edit edit) {
        this.edit = edit;
    }

}
