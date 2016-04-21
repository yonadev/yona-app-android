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

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class RegisterUser implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RegisterUser createFromParcel(Parcel in) {
            return new RegisterUser(in);
        }

        public RegisterUser[] newArray(int size) {
            return new RegisterUser[size];
        }
    };
    private String firstName, lastName, nickname, mobileNumber;

    public RegisterUser() {

    }

    public RegisterUser(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.firstName = data[0];
        this.lastName = data[1];
        this.nickname = data[2];
        this.mobileNumber = data[3];
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getNickName() {
        return nickname;
    }

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
}
