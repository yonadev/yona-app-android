/*
 * <?xml version="1.0" encoding="utf-8"?><!--
 * ~ Copyright (c) 2018 Stichting Yona Foundation
 *   ~
 *   ~ This Source Code Form is subject to the terms of the Mozilla Public
 *   ~ License, v. 2.0. If a copy of the MPL was not distributed with this
 *   ~ file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *   -->
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


public class EncryptionUtils
{

	public static String encrypt(Context context, String token)
	{
		SecurityKey securityKey = getSecurityKey(context);
		return securityKey != null ? securityKey.encrypt(token) : null;
	}

	public static String decrypt(Context context, String token)
	{
		SecurityKey securityKey = getSecurityKey(context);
		return securityKey != null ? securityKey.decrypt(token) : null;
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
		KeyStore keyStore = null;
		try
		{
			keyStore = KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE);
			keyStore.load(null);
		}
		catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e)
		{
			AppUtils.reportException(EncryptionUtils.class, e, Thread.currentThread());
		}
		return keyStore;
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
			AppUtils.reportException(EncryptionUtils.class, e, Thread.currentThread());
		}
	}
}

