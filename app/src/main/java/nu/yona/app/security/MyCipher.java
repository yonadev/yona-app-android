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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.YonaRuntimeException;

import static android.util.Base64.DEFAULT;

/*
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will Google be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, as long as the origin is not misrepresented.
 *
 * @author: Ricardo Champa
 *
 */

public class MyCipher
{

	private final static String ALGORITHM = "AES";
	/**
	 * As we are using secure random passwords, a fixed salt suffices.
	 */
	private static final byte[] SALT = "0123456789012345".getBytes();
	private final String mySecret;

	public MyCipher(String mySecret)
	{
		this.mySecret = mySecret;
	}

	public MyCipherData encryptUTF8(String plaintext)
	{
		try
		{
			byte[] bytes = plaintext.getBytes("utf-8");
			byte[] bytesBase64 = Base64.encode(bytes, DEFAULT);
			return encrypt(bytesBase64);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException
				| IllegalBlockSizeException | BadPaddingException | InvalidParameterSpecException | InvalidKeySpecException e)
		{
			AppUtils.reportException(MyCipher.class, e, Thread.currentThread());
			throw new YonaRuntimeException(e);
		}
	}

	public String decryptUTF8(byte[] ciphertext, IvParameterSpec iv)
	{
		try
		{
			byte[] decryptedData = decrypt(ciphertext, iv);
			byte[] decodedBytes = Base64.decode(decryptedData, DEFAULT);
			return new String(decodedBytes, Charset.forName("UTF8"));
		}
		catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e)
		{
			AppUtils.reportException(MyCipher.class, e, Thread.currentThread());
			throw new YonaRuntimeException(e);
		}
	}

	//AES
	private MyCipherData encrypt(byte[] key, byte[] plaintext) throws
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		//solved using PRNGFixes class
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] data = cipher.doFinal(plaintext);
		AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		return new MyCipherData(data, iv);
	}

	private byte[] decrypt(byte[] key, byte[] ciphertext, IvParameterSpec iv) throws
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		return cipher.doFinal(ciphertext);
	}

	private byte[] getKey() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		String password = this.mySecret;
		int iterationCount = 1000;
		int keyLength = 128; // 256-bits for AES-256, 128-bits for AES-128, etc
		/* Use this to derive the key from the password: */
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(getCompatibleAlgorithm());
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		SecretKey key = new SecretKeySpec(keyBytes, "AES");
		return key.getEncoded();
	}

	private String getCompatibleAlgorithm()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			return "PBKDF2withHmacSHA256";
		}
		return "PBKDF2WithHmacSHA1";
	}

	private byte[] getOldKey() throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		byte[] keyStart = this.mySecret.getBytes("utf-8");
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
		sr.setSeed(keyStart);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] key = skey.getEncoded();
		return key;
	}


	////////////////////////////////////////////////////////////
	private MyCipherData encrypt(byte[] data) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException
	{
		return encrypt(getKey(), data);
	}

	private byte[] decrypt(byte[] encryptedData, IvParameterSpec iv) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		return decrypt(getKey(), encryptedData, iv);
	}

	private byte[] decryptWithOldKey(byte[] encryptedData, IvParameterSpec iv) throws
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		return decrypt(getOldKey(), encryptedData, iv);
	}

	public String getYonaPasswordWithOldEncryptedData(byte[] encryptedData, IvParameterSpec iv)
	{
		try
		{
			byte[] decryptedData = decryptWithOldKey(encryptedData, iv);
			byte[] decodedBytes = Base64.decode(decryptedData, DEFAULT);
			return new String(decodedBytes, Charset.forName("UTF8"));
		}
		catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException e)
		{
			AppUtils.reportException(MyCipher.class, e, Thread.currentThread());
		}
		return null;
	}
}

