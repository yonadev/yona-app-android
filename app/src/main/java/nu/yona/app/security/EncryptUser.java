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

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * Created by kinnarvasa on 27/06/16.
 */

public class EncryptUser extends BaseSecurity {


    public String encryptString(final String secureKey) {

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
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
            } else {
                SecretKeySpec ks = new SecretKeySpec(key, "AES");
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, ks);
                byte[] encryptedText = c.doFinal(secureKey.getBytes("UTF-8"));
                return Base64.encodeToString(encryptedText, Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}
