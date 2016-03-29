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

public class User {

    //    @SerializedName("_embedded")
//    @Expose
//    private nu.yona.app.api.model.Embedded Embedded;
//    @SerializedName("_links")
//    @Expose
//    private Links_____ Links;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    //    @SerializedName("vpnProfile")
//    @Expose
//    private VpnProfile vpnProfile;
    @SerializedName("nickname")
    @Expose
    private String nickname;
//    @SerializedName("devices")
//    @Expose
//    private List<String> devices = new ArrayList<String>();

    /**
     *
     * @return
     *     The Embedded
     */
//    public nu.yona.app.api.model.Embedded getEmbedded() {
//        return Embedded;
//    }

    /**
     *
     * @param Embedded
     *     The _embedded
     */
//    public void setEmbedded(nu.yona.app.api.model.Embedded Embedded) {
//        this.Embedded = Embedded;
//    }

    /**
     *
     * @return
     *     The Links
     */
//    public Links_____ getLinks() {
//        return Links;
//    }

    /**
     *
     * @param Links
     *     The _links
     */
//    public void setLinks(Links_____ Links) {
//        this.Links = Links;
//    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber The mobileNumber
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     *
     * @return
     *     The vpnProfile
     */
//    public VpnProfile getVpnProfile() {
//        return vpnProfile;
//    }

    /**
     *
     * @param vpnProfile
     *     The vpnProfile
     */
//    public void setVpnProfile(VpnProfile vpnProfile) {
//        this.vpnProfile = vpnProfile;
//    }

    /**
     * @return The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return
     *     The devices
     */
//    public List<String> getDevices() {
//        return devices;
//    }

    /**
     *
     * @param devices
     *     The devices
     */
//    public void setDevices(List<String> devices) {
//        this.devices = devices;
//    }

}
