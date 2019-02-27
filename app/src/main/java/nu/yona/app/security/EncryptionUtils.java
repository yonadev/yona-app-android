/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package nu.yona.app.security;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.YonaRuntimeException;


public class EncryptionUtils
{

	public static String encrypt(Context context, String plaintext)
	{
		return getSecurityKey(context).encrypt(plaintext);
	}

	public static String decrypt(Context context, String cipherText)
	{
		return getSecurityKey(context).decrypt(cipherText);
	}

	private static SecurityKey getSecurityKey(Context context)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return EncryptionKeyGenerator.generateSecretKey(getKeyStore());
		}
		return EncryptionKeyGenerator.generateSecretKeyPreM(context, getKeyStore());
	}

	private static KeyStore getKeyStore()
	{
		try
		{
			KeyStore keyStore;
			keyStore = KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE);
			keyStore.load(null);
			return keyStore;
		}
		catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e)
		{
			AppUtils.reportException(EncryptionUtils.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	public static void clear()
	{
		KeyStore keyStore = getKeyStore();
		try
		{
			if (keyStore.containsAlias(EncryptionKeyGenerator.KEY_ALIAS))
			{
				keyStore.deleteEntry(EncryptionKeyGenerator.KEY_ALIAS);
			}
		}
		catch (KeyStoreException e)
		{
			AppUtils.reportException(EncryptionUtils.class, e, Thread.currentThread(), null, false);
		}
	}
}

