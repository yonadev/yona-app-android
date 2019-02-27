/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package nu.yona.app.security;


import android.os.Build;
import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyPair;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.YonaRuntimeException;

class SecurityKey
{
	private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
	private static final String AES_MODE_FOR_POST_API_23 = "AES/GCM/NoPadding";

	private SecretKey secretKey;
	private KeyPair keyPair;

	SecurityKey(SecretKey secretKey)
	{
		this.secretKey = secretKey;
	}

	SecurityKey(KeyPair keyPair)
	{
		this.keyPair = keyPair;
	}

	String encrypt(String plainText)
	{
		if (plainText == null)
		{
			return null;
		}
		try
		{
			Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			return Base64.encodeToString(encrypted, Base64.URL_SAFE);
		}
		catch (GeneralSecurityException e)
		{
			AppUtils.reportException(SecurityKey.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	String decrypt(String encryptedToken)
	{
		if (encryptedToken == null)
		{
			return null;
		}
		try
		{
			Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
			byte[] decoded = Base64.decode(encryptedToken, Base64.URL_SAFE);
			byte[] original = cipher.doFinal(decoded);
			return new String(original);
		}
		catch (GeneralSecurityException e)
		{
			AppUtils.reportException(SecurityKey.class, e, Thread.currentThread(), null, false);
			throw new YonaRuntimeException(e);
		}
	}

	private Cipher getCipher(int mode) throws GeneralSecurityException
	{
		Cipher cipher;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			cipher = Cipher.getInstance(AES_MODE_FOR_POST_API_23);
			cipher.init(mode, secretKey, new GCMParameterSpec(128, AES_MODE_FOR_POST_API_23.getBytes(), 0, 12));
		}
		else
		{
			cipher = Cipher.getInstance(RSA_MODE);
			cipher.init(mode, mode == Cipher.DECRYPT_MODE ? keyPair.getPublic() : keyPair.getPrivate());
		}
		return cipher;
	}
}