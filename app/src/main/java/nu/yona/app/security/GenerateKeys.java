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

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;

import static android.content.ContentValues.TAG;

/**
 * Created by kinnarvasa on 27/06/16.
 */

public class GenerateKeys extends BaseSecurity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void createNewKeys(final DataLoadListener listener) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (alias == null || !keyStore.containsAlias(alias)) {
                        Calendar start = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();
                        end.add(Calendar.YEAR, 1);
                        KeyPairGenerator generator;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(YonaApplication.getAppContext())
                                    .setAlias(alias)
                                    .setKeyType(KeyProperties.KEY_ALGORITHM_RSA)
                                    .setKeySize(2048)
                                    .setSubject(new X500Principal("C=NL O=Yona CN=yona"))
                                    .setSerialNumber(BigInteger.ONE)
                                    .setStartDate(start.getTime())
                                    .setEndDate(end.getTime())
                                    .build();

                            generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                            generator.initialize(spec);
                        } else {
                            generator = KeyPairGenerator.getInstance(
                                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                            generator.initialize(new KeyGenParameterSpec.Builder(
                                    alias, KeyProperties.PURPOSE_SIGN)
                                    .setDigests(KeyProperties.DIGEST_SHA256)
                                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                                    .build());
                        }
                        generator.generateKeyPair();
                        Log.e(YonaApplication.class.getSimpleName(), "KEY GEnerated");
                        return "Done";
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (listener != null) {
                    if (s == null) {
                        listener.onError(new ErrorMessage());
                    } else {
                        listener.onDataLoad(s);
                    }
                }

            }
        }.execute();
    }

}
