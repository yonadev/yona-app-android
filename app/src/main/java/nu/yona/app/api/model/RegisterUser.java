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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class RegisterUser implements Parcelable {
    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RegisterUser createFromParcel(Parcel in) {
            return new RegisterUser(in);
        }

        public RegisterUser[] newArray(int size) {
            return new RegisterUser[size];
        }
    };
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("emailAddress")
    @Expose
    private String emailAddress;
    @SerializedName("_links")
    @Expose
    private Links_ Links;

    /**
     * Instantiates a new Register user.
     */
    public RegisterUser() {

    }

    private RegisterUser(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.firstName = data[0];
        this.lastName = data[1];
        this.nickname = data[2];
        this.mobileNumber = data[3];
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets mobile number.
     *
     * @return the mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets mobile number.
     *
     * @param mobileNumber the mobile number
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickname;
    }

    /**
     * Sets nick name.
     *
     * @param nickname the nickname
     */
    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.firstName, this.lastName, this.nickname, this.mobileNumber});
    }

    /**
     * Gets links.
     *
     * @return The Links
     */
    public Links_ getLinks() {
        return Links;
    }

    /**
     * Sets links.
     *
     * @param Links The _links
     */
    public void setLinks(Links_ Links) {
        this.Links = Links;
    }

    /**
     * Gets email address.
     *
     * @return The Email
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets email address.
     *
     * @param email The Email
     */
    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }
}
