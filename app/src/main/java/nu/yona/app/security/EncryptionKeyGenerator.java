/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package nu.yona.app.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.security.auth.x500.X500Principal;

import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.YonaRuntimeException;

public class EncryptionKeyGenerator
{
	//Dont change this. AndroidKeyStore is the default one for Android 18+. refer https://developer.android.com/reference/java/security/KeyStore.html
	public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
	public static final String KEY_ALIAS = "YONA";

	@TargetApi(Build.VERSION_CODES.M)
	static SecurityKey generateSecretKey(KeyStore keyStore)
	{
		try
		{
			if (keyStore.containsAlias(KEY_ALIAS))
			{
				return getExistingSecurityKeyFromAlias(keyStore);
			}
			return getFreshSecurityKey(keyStore);
		}
		catch (KeyStoreException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	static SecurityKey getExistingSecurityKeyFromAlias(KeyStore keyStore)
	{
		try
		{
			KeyStore.SecretKeyEntry entry =
					(KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
			return new SecurityKey(entry.getSecretKey());
		}
		catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}


	@TargetApi(Build.VERSION_CODES.M)
	static SecurityKey getFreshSecurityKey(KeyStore keyStore)
	{
		try
		{
			KeyGenerator keyGenerator =
					KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
			keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
					KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(
					KeyProperties.BLOCK_MODE_GCM)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
					.setRandomizedEncryptionRequired(false)
					.build());
			return new SecurityKey(keyGenerator.generateKey());
		}
		catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	static SecurityKey generateSecretKeyPreM(Context context, KeyStore keyStore)
	{
		try
		{
			if (keyStore.containsAlias(KEY_ALIAS))
			{
				return getExistingSecretKeyPreM(keyStore);
			}
			return getFreshSecretKeyPreM(context);
		}
		catch (KeyStoreException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	static SecurityKey getExistingSecretKeyPreM(KeyStore keyStore)
	{
		try
		{
			KeyStore.PrivateKeyEntry entry =
					(KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
			return new SecurityKey(
					new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey()));
		}
		catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	static SecurityKey getFreshSecretKeyPreM(Context context)
	{
		try
		{
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			//1 Year validity
			end.add(Calendar.YEAR, 1);
			KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias(KEY_ALIAS)
					.setSubject(new X500Principal("CN=" + KEY_ALIAS))
					.setSerialNumber(BigInteger.TEN)
					.setStartDate(start.getTime())
					.setEndDate(end.getTime())
					.build();
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
			kpg.initialize(spec);
			return new SecurityKey(kpg.generateKeyPair());
		}
		catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e)
		{
			AppUtils.reportException(EncryptionKeyGenerator.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}
}