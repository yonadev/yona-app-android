/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import javax.crypto.spec.IvParameterSpec;

import nu.yona.app.YonaApplication;
import nu.yona.app.security.EncryptionUtils;
import nu.yona.app.security.MyCipher;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 29/06/16.
 */

public class SharedPreference
{
	private SharedPreferences userPreferences;
	private SharedPreferences appPreferences;
	private String yonaPwd = null;


	/**
	 * Gets user preferences.
	 *
	 * @return the user preferences
	 */
	public synchronized SharedPreferences getUserPreferences()
	{
		if (userPreferences == null)
		{
			userPreferences = YonaApplication.getAppContext().getSharedPreferences(PreferenceConstant.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
		}
		return userPreferences;
	}

	/**
	 * Gets user preferences.
	 *
	 * @return the user preferences
	 */
	public synchronized SharedPreferences getAppPreferences()
	{
		if (appPreferences == null)
		{
			appPreferences = YonaApplication.getAppContext().getSharedPreferences(PreferenceConstant.APP_PREFERENCE_KEY, Context.MODE_PRIVATE);
		}
		return appPreferences;
	}


	public String getVPNProfilePath()
	{
		return userPreferences.getString(PreferenceConstant.VPN_PROFILE_PATH, null);
	}

	public void setRootCertPath(String path)
	{
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString(PreferenceConstant.ROOT_CERTIFICATE, path);
		editor.putBoolean(PreferenceConstant.ROOT_CERTIFICATE_ACTIVE, false);
		editor.commit();
	}

	public String getRootCertPath()
	{
		if (userPreferences != null)
		{
			return userPreferences.getString(PreferenceConstant.ROOT_CERTIFICATE, null);
		}
		return null;
	}


	public void setVPNProfilePath(String path)
	{
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString(PreferenceConstant.VPN_PROFILE_PATH, path);
		editor.putBoolean(PreferenceConstant.VPN_PROFILE_ACTIVE, false);
		editor.commit();
	}

	/**
	 * Sets yona password.
	 *
	 * @param password yona password
	 */
	public void setYonaPassword(String password)
	{
		yonaPwd = null;
		userPreferences.edit().putString(PreferenceConstant.YONA_DATA, EncryptionUtils.encrypt(YonaApplication.getAppContext(), password)).commit();
	}

	/**
	 * Gets yona password.
	 *
	 * @return return yona password
	 */
	public String getYonaPassword()
	{
		if (yonaPwd == null)
		{
			yonaPwd = getDecryptedKey();
		}
		return yonaPwd;
	}

	private String getDecryptedKey()
	{
		if (!TextUtils.isEmpty(userPreferences.getString(PreferenceConstant.YONA_DATA, "")))
		{
			return EncryptionUtils.decrypt(YonaApplication.getAppContext(), userPreferences.getString(PreferenceConstant.YONA_DATA, ""));
		}
		else
		{
			return "";
		}
	}

	public void upgradeFromInitialPasswordEncryption()
	{
		byte[] encrypted_data = byteToString(userPreferences.getString(PreferenceConstant.YONA_DATA, ""));
		byte[] dataIV = byteToString(userPreferences.getString(PreferenceConstant.YONA_IV, ""));
		IvParameterSpec iv = new IvParameterSpec(dataIV);
		MyCipher myCipher = new MyCipher(Build.SERIAL);
		setYonaPassword(myCipher.getYonaPasswordWithOldEncryptedData(encrypted_data, iv));
	}

	public void upgradeFromSerialBasedPasswordEncryption()
	{
		setYonaPassword(getDecryptedKeyFromSerialBasedEncryptedData());
	}

	private String getDecryptedKeyFromSerialBasedEncryptedData()
	{
		if (!TextUtils.isEmpty(userPreferences.getString(PreferenceConstant.YONA_DATA, "")))
		{
			byte[] encrypted_data = byteToString(userPreferences.getString(PreferenceConstant.YONA_DATA, ""));
			byte[] dataIV = byteToString(userPreferences.getString(PreferenceConstant.YONA_IV, ""));
			IvParameterSpec iv = new IvParameterSpec(dataIV);
			return new MyCipher(Build.SERIAL).decryptUTF8(encrypted_data, iv);
		}
		else
		{
			return "";
		}
	}

	private byte[] byteToString(String response)
	{
		String[] byteValues = response.substring(1, response.length() - 1).split(",");
		byte[] encrypted_data = new byte[byteValues.length];
		for (int i = 0, len = encrypted_data.length; i < len; i++)
		{
			encrypted_data[i] = Byte.parseByte(byteValues[i].trim());
		}
		return encrypted_data;
	}

}
