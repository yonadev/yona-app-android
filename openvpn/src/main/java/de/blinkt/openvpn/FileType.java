/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package de.blinkt.openvpn;

import de.blinkt.openvpn.fragments.Utils;

/**
 * Created by kinnarvasa on 22/06/16.
 */

public enum FileType {
    PKCS12(0),
    CLIENT_CERTIFICATE(1),
    CA_CERTIFICATE(2),
    OVPN_CONFIG(3),
    KEYFILE(4),
    TLS_AUTH_FILE(5),
    USERPW_FILE(6),
    CRL_FILE(7);

    private int value;

    FileType(int i) {
        value = i;
    }

    public static FileType getFileTypeByValue(int value) {
        switch (value) {
            case 0:
                return PKCS12;
            case 1:
                return CLIENT_CERTIFICATE;
            case 2:
                return CA_CERTIFICATE;
            case 3:
                return OVPN_CONFIG;
            case 4:
                return KEYFILE;
            case 5:
                return TLS_AUTH_FILE;
            case 6:
                return USERPW_FILE;
            case 7:
                return CRL_FILE;
            default:
                return null;
        }
    }

    public int getValue() {
        return value;
    }
}
