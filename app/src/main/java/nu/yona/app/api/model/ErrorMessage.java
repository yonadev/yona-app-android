/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class ErrorMessage extends BaseEntity
{
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("code")
	@Expose
	private String code;

	/**
	 * Instantiates a new Error message.
	 */
	public ErrorMessage()
	{

	}

	/**
	 * Instantiates a new Error message.
	 *
	 * @param message the message
	 */
	public ErrorMessage(String message)
	{
		this.code = "";
		this.message = message;
	}

	/**
	 * Gets message.
	 *
	 * @return The message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets message.
	 *
	 * @param message The message
	 */
	public void setMessage(String message)
	{
		this.message = message;
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
