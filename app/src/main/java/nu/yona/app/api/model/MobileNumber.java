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
/**
 * Created by kinnarvasa on 21/04/16.
 */
public class MobileNumber {
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    /**
     *
     * @param number Mobile Number
     */
    public MobileNumber(String number) {
        this.mobileNumber = number;
    }
    /**
     *
     * @return
     * The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     *
     * @param mobileNumber
     * The mobileNumber
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
