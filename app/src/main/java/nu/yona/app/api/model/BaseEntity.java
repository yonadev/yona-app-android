/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Author @MobiquityInc.
 */
public abstract class BaseEntity implements Serializable, Cloneable
{

	private static final long serialVersionUID = 4243329623573859700L;

	/**
	 * Instantiates a new Base entity.
	 */
	BaseEntity()
	{

	}

	/**
	 * Gets db content values.
	 *
	 * @return the db content values
	 */
	public abstract ContentValues getDbContentValues();
}
