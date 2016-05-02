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
 * The type Vpn profile.
 */
public class VpnProfile extends BaseEntity {

    @SerializedName("vpnLoginID")
    @Expose
    private String vpnLoginID;
    @SerializedName("vpnPassword")
    @Expose
    private String vpnPassword;
    @SerializedName("openVPNProfile")
    @Expose
    private String openVPNProfile;

    /**
     * Gets vpn login id.
     *
     * @return The vpnLoginID
     */
    public String getVpnLoginID() {
        return vpnLoginID;
    }

    /**
     * Sets vpn login id.
     *
     * @param vpnLoginID The vpnLoginID
     */
    public void setVpnLoginID(String vpnLoginID) {
        this.vpnLoginID = vpnLoginID;
    }

    /**
     * Gets vpn password.
     *
     * @return The vpnPassword
     */
    public String getVpnPassword() {
        return vpnPassword;
    }

    /**
     * Sets vpn password.
     *
     * @param vpnPassword The vpnPassword
     */
    public void setVpnPassword(String vpnPassword) {
        this.vpnPassword = vpnPassword;
    }

    /**
     * Gets open vpn profile.
     *
     * @return The openVPNProfile
     */
    public String getOpenVPNProfile() {
        return openVPNProfile;
    }

    /**
     * Sets open vpn profile.
     *
     * @param openVPNProfile The openVPNProfile
     */
    public void setOpenVPNProfile(String openVPNProfile) {
        this.openVPNProfile = openVPNProfile;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

}
