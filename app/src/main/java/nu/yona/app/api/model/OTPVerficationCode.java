/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public class OTPVerficationCode extends BaseEntity
{

	@SerializedName("code")
	@Expose
	private String code;

	/**
	 * Instantiates a new Otp verfication code.
	 *
	 * @param otp the otp
	 */
	public OTPVerficationCode(String otp)
	{
		this.code = otp;
	}

	/**
	 * Gets code.
	 *
	 * @return The code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Sets code.
	 *
	 * @param code The code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}

}
