/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.security;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by kinnarvasa on 27/06/16.
 */

public class EncryptUser extends BaseSecurity {


    public String encryptString(final String secureKey) {

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Log.e(EncryptUser.class.getSimpleName(), "Secure Key:" + secureKey);
            // Encrypt the text
            if (secureKey.isEmpty()) {
                return null;
            }

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
            cipherOutputStream.write(secureKey.getBytes("UTF-8"));
            cipherOutputStream.close();
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}
