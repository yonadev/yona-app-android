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

/**
 * The type Links.
 */
public class Links extends BaseEntity {

    @SerializedName("self")
    @Expose
    private Href self;
    @SerializedName("edit")
    @Expose
    private Href edit;
    @SerializedName("yona:activityCategory")
    @Expose
    private Href yonaActivityCategory;
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
    @SerializedName("yona:process")
    @Expose
    private Href yonaPreocess;
    @SerializedName("yona:appActivity")
    @Expose
    private Href yonaAppActivity;
    @SerializedName("curies")
    @Expose
    private List<Cury> curies = new ArrayList<>();
    @SerializedName("yona:confirmMobileNumber")
    @Expose
    private Href yonaConfirmMobileNumber;
    @SerializedName("yona:resendMobileNumberConfirmationCode")
    @Expose
    private Href resendMobileNumberConfirmationCode;
    @SerializedName("yona:requestPinReset")
    @Expose
    private Href requestPinReset;
    @SerializedName("yona:resendPinResetConfirmationCode")
    @Expose
    private Href resendPinResetConfirmationCode;
    @SerializedName("yona:clearPinReset")
    @Expose
    private Href clearPinReset;
    @SerializedName("yona:verifyPinReset")
    @Expose
    private Href verifyPinReset;
    @SerializedName("yona:user")
    @Expose
    private Href yonaUser;
    @SerializedName("yona:reject")
    @Expose
    private Href yonaReject;
    @SerializedName("yona:accept")
    @Expose
    private Href yonaAccept;
    @SerializedName("yona:goal")
    @Expose
    private Href yonaGoal;
    @SerializedName("yona:dayDetails")
    @Expose
    private Href yonaDayDetails;
    @SerializedName("next")
    @Expose
    private Href next;
    @SerializedName("prev")
    @Expose
    private Href prev;
    @SerializedName("first")
    @Expose
    private Href first;
    @SerializedName("last")
    @Expose
    private Href last;
    @SerializedName("yona:weekDetails")
    @Expose
    private Href weekDetails;
    @SerializedName("yona:buddy")
    @Expose
    private Href yonaBuddy;

    /**
     * Gets self.
     *
     * @return The self
     */
    public Href getSelf() {
        return self;
    }

    /**
     * Sets self.
     *
     * @param self The self
     */
    public void setSelf(Href self) {
        this.self = self;
    }

    /**
     * Gets edit.
     *
     * @return The edit
     */
    public Href getEdit() {
        return edit;
    }

    /**
     * Sets edit.
     *
     * @param edit The edit
     */
    public void setEdit(Href edit) {
        this.edit = edit;
    }

    /**
     * Gets yona messages.
     *
     * @return The yonaMessages
     */
    public Href getYonaMessages() {
        return yonaMessages;
    }

    /**
     * Sets yona messages.
     *
     * @param yonaMessages The yona:messages
     */
    public void setYonaMessages(Href yonaMessages) {
        this.yonaMessages = yonaMessages;
    }

    /**
     * Gets yona daily activity reports.
     *
     * @return The yonaDailyActivityReports
     */
    public Href getYonaDailyActivityReports() {
        return yonaDailyActivityReports;
    }

    /**
     * Sets yona daily activity reports.
     *
     * @param yonaDailyActivityReports The yona:dailyActivityReports
     */
    public void setYonaDailyActivityReports(Href yonaDailyActivityReports) {
        this.yonaDailyActivityReports = yonaDailyActivityReports;
    }

    /**
     * Gets yona weekly activity reports.
     *
     * @return The yonaWeeklyActivityReports
     */
    public Href getYonaWeeklyActivityReports() {
        return yonaWeeklyActivityReports;
    }

    /**
     * Sets yona weekly activity reports.
     *
     * @param yonaWeeklyActivityReports The yona:weeklyActivityReports
     */
    public void setYonaWeeklyActivityReports(Href yonaWeeklyActivityReports) {
        this.yonaWeeklyActivityReports = yonaWeeklyActivityReports;
    }

    /**
     * Gets yona new device request.
     *
     * @return The yonaNewDeviceRequest
     */
    public Href getYonaNewDeviceRequest() {
        return yonaNewDeviceRequest;
    }

    /**
     * Sets yona new device request.
     *
     * @param yonaNewDeviceRequest The yona:newDeviceRequest
     */
    public void setYonaNewDeviceRequest(Href yonaNewDeviceRequest) {
        this.yonaNewDeviceRequest = yonaNewDeviceRequest;
    }

    /**
     * Gets yona app activity.
     *
     * @return The yonaAppActivity
     */
    public Href getYonaAppActivity() {
        return yonaAppActivity;
    }

    /**
     * Sets yona app activity.
     *
     * @param yonaAppActivity The yona:appActivity
     */
    public void setYonaAppActivity(Href yonaAppActivity) {
        this.yonaAppActivity = yonaAppActivity;
    }

    /**
     * Gets curies.
     *
     * @return The curies
     */
    public List<Cury> getCuries() {
        return curies;
    }

    /**
     * Sets curies.
     *
     * @param curies The curies
     */
    public void setCuries(List<Cury> curies) {
        this.curies = curies;
    }

    /**
     * Gets yona confirm mobile number.
     *
     * @return the yona confirm mobile number
     */
    public Href getYonaConfirmMobileNumber() {
        return yonaConfirmMobileNumber;
    }

    /**
     * Sets yona confirm mobile number.
     *
     * @param yonaConfirmMobileNumber the yona confirm mobile number
     */
    public void setYonaConfirmMobileNumber(Href yonaConfirmMobileNumber) {
        this.yonaConfirmMobileNumber = yonaConfirmMobileNumber;
    }

    /**
     * Gets resend mobile number confirmation code.
     *
     * @return the resend mobile number confirmation code
     */
    public Href getResendMobileNumberConfirmationCode() {
        return resendMobileNumberConfirmationCode;
    }

    /**
     * Sets resend mobile number confirmation code.
     *
     * @param resendMobileNumberConfirmationCode the resend mobile number confirmation code
     */
    public void setResendMobileNumberConfirmationCode(Href resendMobileNumberConfirmationCode) {
        this.resendMobileNumberConfirmationCode = resendMobileNumberConfirmationCode;
    }

    /**
     * Gets request pin reset.
     *
     * @return the request pin reset
     */
    public Href getRequestPinReset() {
        return requestPinReset;
    }

    /**
     * Sets request pin reset.
     *
     * @param requestPinReset the request pin reset
     */
    public void setRequestPinReset(Href requestPinReset) {
        this.requestPinReset = requestPinReset;
    }

    /**
     * Gets verify pin reset.
     *
     * @return the verify pin reset
     */
    public Href getVerifyPinReset() {
        return verifyPinReset;
    }

    /**
     * Sets verify pin reset.
     *
     * @param verifyPinReset the verify pin reset
     */
    public void setVerifyPinReset(Href verifyPinReset) {
        this.verifyPinReset = verifyPinReset;
    }

    /**
     * Gets clear pin reset.
     *
     * @return the clear pin reset
     */
    public Href getClearPinReset() {
        return clearPinReset;
    }

    /**
     * Sets clear pin reset.
     *
     * @param clearPinReset the clear pin reset
     */
    public void setClearPinReset(Href clearPinReset) {
        this.clearPinReset = clearPinReset;
    }

    /**
     * Gets yona user.
     *
     * @return The yonaUser
     */
    public Href getYonaUser() {
        return yonaUser;
    }

    /**
     * Sets yona user.
     *
     * @param yonaUser The yona:user
     */
    public void setYonaUser(Href yonaUser) {
        this.yonaUser = yonaUser;
    }

    /**
     * Gets yona activity category.
     *
     * @return the yona activity category
     */
    public Href getYonaActivityCategory() {
        return yonaActivityCategory;
    }

    /**
     * Sets yona activity category.
     *
     * @param yonaActivityCategory the yona activity category
     */
    public void setYonaActivityCategory(Href yonaActivityCategory) {
        this.yonaActivityCategory = yonaActivityCategory;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

    /**
     * Gets yona reject.
     *
     * @return The yonaReject
     */
    public Href getYonaReject() {
        return yonaReject;
    }

    /**
     * Sets yona reject.
     *
     * @param yonaReject The yona:reject
     */
    public void setYonaReject(Href yonaReject) {
        this.yonaReject = yonaReject;
    }

    /**
     * Gets yona accept.
     *
     * @return The yonaAccept
     */
    public Href getYonaAccept() {
        return yonaAccept;
    }

    /**
     * Sets yona accept.
     *
     * @param yonaAccept The yona:accept
     */
    public void setYonaAccept(Href yonaAccept) {
        this.yonaAccept = yonaAccept;
    }


    /**
     * Gets yona preocess.
     *
     * @return the yona preocess
     */
    public Href getYonaPreocess() {
        return yonaPreocess;
    }

    /**
     * Sets yona preocess.
     *
     * @param yonaPreocess the yona preocess
     */
    public void setYonaPreocess(Href yonaPreocess) {
        this.yonaPreocess = yonaPreocess;
    }

    /**
     * Gets yona goal.
     *
     * @return the yona goal
     */
    public Href getYonaGoal() {
        return this.yonaGoal;
    }

    /**
     * Sets yona goal.
     *
     * @param yonaGoal the yona goal
     */
    public void setYonaGoal(Href yonaGoal) {
        this.yonaGoal = yonaGoal;
    }


    /**
     * Gets yona day details.
     *
     * @return The yonaDayDetails
     */
    public Href getYonaDayDetails() {
        return yonaDayDetails;
    }

    /**
     * Sets yona day details.
     *
     * @param yonaDayDetails The yona:dayDetails
     */
    public void setYonaDayDetails(Href yonaDayDetails) {
        this.yonaDayDetails = yonaDayDetails;
    }

    /**
     * Gets next.
     *
     * @return The next
     */
    public Href getNext() {
        return next;
    }

    /**
     * Sets next.
     *
     * @param next The next
     */
    public void setNext(Href next) {
        this.next = next;
    }

    /**
     * Gets prev.
     *
     * @return The prev
     */
    public Href getPrev() {
        return prev;
    }

    /**
     * Sets prev.
     *
     * @param prev The prev
     */
    public void setPrev(Href prev) {
        this.prev = prev;
    }

    /**
     * Gets first.
     *
     * @return The first
     */
    public Href getFirst() {
        return first;
    }

    /**
     * Sets first.
     *
     * @param first The first
     */
    public void setFirst(Href first) {
        this.first = first;
    }

    /**
     * Gets last.
     *
     * @return The last
     */
    public Href getLast() {
        return last;
    }

    /**
     * Sets last.
     *
     * @param last The last
     */
    public void setLast(Href last) {
        this.last = last;
    }

    /**
     * Gets resend pin reset confirmation code.
     *
     * @return the resend pin reset confirmation code
     */
    public Href getResendPinResetConfirmationCode() {
        return this.resendPinResetConfirmationCode;
    }

    /**
     * Sets resend pin reset confirmation code.
     *
     * @param resendPinResetConfirmationCode the resend pin reset confirmation code
     */
    public void setResendPinResetConfirmationCode(Href resendPinResetConfirmationCode) {
        this.resendPinResetConfirmationCode = resendPinResetConfirmationCode;
    }

    /**
     * Gets week details.
     *
     * @return the week details
     */
    public Href getWeekDetails() {
        return this.weekDetails;
    }

    /**
     * Sets week details.
     *
     * @param weekDetails the week details
     */
    public void setWeekDetails(Href weekDetails) {
        this.weekDetails = weekDetails;
    }

    /**
     * Gets yona buddy.
     *
     * @return the yona buddy
     */
    public Href getYonaBuddy() {
        return this.yonaBuddy;
    }

    /**
     * Sets yona buddy.
     *
     * @param yonaBuddy the yona buddy
     */
    public void setYonaBuddy(Href yonaBuddy) {
        this.yonaBuddy = yonaBuddy;
    }
}
