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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * Created by kinnarvasa on 27/06/16.
 */

public class DecryptUser extends BaseSecurity {

    public String decryptString(String secureKey) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e(DecryptUser.class.getSimpleName(), "Secure Key:" + secureKey);
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

                Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                output.init(Cipher.DECRYPT_MODE, privateKey);

                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(secureKey, Base64.DEFAULT)), output);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i).byteValue();
                }
                return new String(bytes, 0, bytes.length, "UTF-8");
            } else {
                SecretKeySpec ks = new SecretKeySpec(key, "AES");
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, ks);
                byte[] clearText = c.doFinal(Base64.decode(secureKey, Base64.DEFAULT));
                return new String(clearText, "UTF-8");
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

}
