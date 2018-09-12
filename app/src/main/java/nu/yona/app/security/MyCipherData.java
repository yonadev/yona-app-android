/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.security;

public class MyCipherData
{

	private final byte[] data;
	private final byte[] iv;

	public MyCipherData(byte[] data, byte[] iv)
	{
		this.data = data;
		this.iv = iv;
	}

	public byte[] getData()
	{
		return data;
	}

	public byte[] getIV()
	{
		return iv;
	}

}
