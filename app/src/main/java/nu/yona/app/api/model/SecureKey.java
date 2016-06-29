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

/**
 * Created by kinnarvasa on 28/06/16.
 */

public class SecureKey {
    private String yonaPassword;
    private String vpnUserName;
    private String vpnPasswrod;

    public String getVpnPasswrod() {
        return this.vpnPasswrod;
    }

    public void setVpnPasswrod(String vpnPasswrod) {
        this.vpnPasswrod = vpnPasswrod;
    }

    public String getVpnUserName() {
        return this.vpnUserName;
    }

    public void setVpnUserName(String vpnUserName) {
        this.vpnUserName = vpnUserName;
    }

    public String getYonaPassword() {
        return this.yonaPassword;
    }

    public void setYonaPassword(String yonaPassword) {
        this.yonaPassword = yonaPassword;
    }
}
