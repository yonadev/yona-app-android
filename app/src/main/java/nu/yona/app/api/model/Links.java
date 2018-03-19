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
    @SerializedName("yona:dailyActivityReportsWithBuddies")
    @Expose
    private Href dailyActivityReportsWithBuddies;
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
    @SerializedName("yona:addComment")
    @Expose
    private Href addComment;
    @SerializedName("yona:reply")
    @Expose
    private Href replyComment;
    @SerializedName("yona:ovpnProfile")
    @Expose
    private Href ovpnProfile;
    @SerializedName("yona:sslRootCert")
    @Expose
    private Href sslRootCert;
    @SerializedName("yona:markRead")
    @Expose
    private Href markRead;
    @SerializedName("yona:markUnread")
    @Expose
    private Href markUnRead;
    @Expose
    @SerializedName("yona:repliedMessage")
    private Href repliedMessage;
    @Expose
    @SerializedName("yona:editUserPhoto")
    private Href editUserPhoto;
    @Expose
    @SerializedName("yona:userPhoto")
    private Href userPhoto;

    public Href getRepliedMessage() {
        return this.repliedMessage;
    }

    public void setRepliedMessage(Href repliedMessage) {
        this.repliedMessage = repliedMessage;
    }

    /**
     * Gets self.
     *
     * @return the self
     */
    public Href getSelf() {
        return self;
    }

    /**
     * Sets self.
     *
     * @param self the self
     */
    public void setSelf(Href self) {
        this.self = self;
    }

    /**
     * Gets edit.
     *
     * @return the edit
     */
    public Href getEdit() {
        return edit;
    }

    /**
     * Sets edit.
     *
     * @param edit the edit
     */
    public void setEdit(Href edit) {
        this.edit = edit;
    }

    /**
     * Gets yona messages.
     *
     * @return the yona messages
     */
    public Href getYonaMessages() {
        return yonaMessages;
    }

    /**
     * Sets yona messages.
     *
     * @param yonaMessages the yona messages
     */
    public void setYonaMessages(Href yonaMessages) {
        this.yonaMessages = yonaMessages;
    }

    /**
     * Gets yona daily activity reports.
     *
     * @return the yona daily activity reports
     */
    public Href getYonaDailyActivityReports() {
        return yonaDailyActivityReports;
    }

    /**
     * Sets yona daily activity reports.
     *
     * @param yonaDailyActivityReports the yona daily activity reports
     */
    public void setYonaDailyActivityReports(Href yonaDailyActivityReports) {
        this.yonaDailyActivityReports = yonaDailyActivityReports;
    }

    /**
     * Gets yona weekly activity reports.
     *
     * @return the yona weekly activity reports
     */
    public Href getYonaWeeklyActivityReports() {
        return yonaWeeklyActivityReports;
    }

    /**
     * Sets yona weekly activity reports.
     *
     * @param yonaWeeklyActivityReports the yona weekly activity reports
     */
    public void setYonaWeeklyActivityReports(Href yonaWeeklyActivityReports) {
        this.yonaWeeklyActivityReports = yonaWeeklyActivityReports;
    }

    /**
     * Gets yona new device request.
     *
     * @return the yona new device request
     */
    public Href getYonaNewDeviceRequest() {
        return yonaNewDeviceRequest;
    }

    /**
     * Sets yona new device request.
     *
     * @param yonaNewDeviceRequest the yona new device request
     */
    public void setYonaNewDeviceRequest(Href yonaNewDeviceRequest) {
        this.yonaNewDeviceRequest = yonaNewDeviceRequest;
    }

    /**
     * Gets yona app activity.
     *
     * @return the yona app activity
     */
    public Href getYonaAppActivity() {
        return yonaAppActivity;
    }

    /**
     * Sets yona app activity.
     *
     * @param yonaAppActivity the yona app activity
     */
    public void setYonaAppActivity(Href yonaAppActivity) {
        this.yonaAppActivity = yonaAppActivity;
    }

    /**
     * Gets curies.
     *
     * @return the curies
     */
    public List<Cury> getCuries() {
        return curies;
    }

    /**
     * Sets curies.
     *
     * @param curies the curies
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
     * @return the yona user
     */
    public Href getYonaUser() {
        return yonaUser;
    }

    /**
     * Sets yona user.
     *
     * @param yonaUser the yona user
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
     * @return the yona reject
     */
    public Href getYonaReject() {
        return yonaReject;
    }

    /**
     * Sets yona reject.
     *
     * @param yonaReject the yona reject
     */
    public void setYonaReject(Href yonaReject) {
        this.yonaReject = yonaReject;
    }

    /**
     * Gets yona accept.
     *
     * @return the yona accept
     */
    public Href getYonaAccept() {
        return yonaAccept;
    }

    /**
     * Sets yona accept.
     *
     * @param yonaAccept the yona accept
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
     * @return the yona day details
     */
    public Href getYonaDayDetails() {
        return yonaDayDetails;
    }

    /**
     * Sets yona day details.
     *
     * @param yonaDayDetails the yona day details
     */
    public void setYonaDayDetails(Href yonaDayDetails) {
        this.yonaDayDetails = yonaDayDetails;
    }

    /**
     * Gets next.
     *
     * @return the next
     */
    public Href getNext() {
        return next;
    }

    /**
     * Sets next.
     *
     * @param next the next
     */
    public void setNext(Href next) {
        this.next = next;
    }

    /**
     * Gets prev.
     *
     * @return the prev
     */
    public Href getPrev() {
        return prev;
    }

    /**
     * Sets prev.
     *
     * @param prev the prev
     */
    public void setPrev(Href prev) {
        this.prev = prev;
    }

    /**
     * Gets first.
     *
     * @return the first
     */
    public Href getFirst() {
        return first;
    }

    /**
     * Sets first.
     *
     * @param first the first
     */
    public void setFirst(Href first) {
        this.first = first;
    }

    /**
     * Gets last.
     *
     * @return the last
     */
    public Href getLast() {
        return last;
    }

    /**
     * Sets last.
     *
     * @param last the last
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

    /**
     * Gets daily activity reports with buddies.
     *
     * @return the daily activity reports with buddies
     */
    public Href getDailyActivityReportsWithBuddies() {
        return this.dailyActivityReportsWithBuddies;
    }

    /**
     * Sets daily activity reports with buddies.
     *
     * @param dailyActivityReportsWithBuddies the daily activity reports with buddies
     */
    public void setDailyActivityReportsWithBuddies(Href dailyActivityReportsWithBuddies) {
        this.dailyActivityReportsWithBuddies = dailyActivityReportsWithBuddies;
    }

    /**
     * Gets add comment.
     *
     * @return the add comment
     */
    public Href getAddComment() {
        return this.addComment;
    }

    /**
     * Sets add comment.
     *
     * @param addComment the add comment
     */
    public void setAddComment(Href addComment) {
        this.addComment = addComment;
    }

    /**
     * Gets reply comment.
     *
     * @return the reply comment
     */
    public Href getReplyComment() {
        return this.replyComment;
    }

    /**
     * Sets reply comment.
     *
     * @param replyComment the reply comment
     */
    public void setReplyComment(Href replyComment) {
        this.replyComment = replyComment;
    }

    /**
     * Gets ovpn profile.
     *
     * @return the ovpn profile
     */
    public Href getOvpnProfile() {
        return this.ovpnProfile;
    }

    /**
     * Sets ovpn profile.
     *
     * @param ovpnProfile the ovpn profile
     */
    public void setOvpnProfile(Href ovpnProfile) {
        this.ovpnProfile = ovpnProfile;
    }

    /**
     * Gets ssl root cert.
     *
     * @return the ssl root cert
     */
    public Href getSslRootCert() {
        return this.sslRootCert;
    }

    /**
     * Sets ssl root cert.
     *
     * @param sslRootCert the ssl root cert
     */
    public void setSslRootCert(Href sslRootCert) {
        this.sslRootCert = sslRootCert;
    }

    public Href getMarkRead() {
        return this.markRead;
    }

    public void setMarkRead(Href markRead) {
        this.markRead = markRead;
    }

    public Href getMarkUnRead() {
        return this.markUnRead;
    }

    public void setMarkUnRead(Href markUnRead) {
        this.markUnRead = markUnRead;
    }

    public Href getEditUserPhoto() {
        return editUserPhoto;
    }

    public void setEditUserPhoto(Href editUserPhoto) {
        this.editUserPhoto = editUserPhoto;
    }

    public Href getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Href userPhoto) {
        this.userPhoto = userPhoto;
    }
}
