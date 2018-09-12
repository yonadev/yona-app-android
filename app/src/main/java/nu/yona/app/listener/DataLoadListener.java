/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.listener;

/**
 * The interface Data load listener.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 */
public interface DataLoadListener<T, U>
{
	/**
	 * On data load.
	 *
	 * @param result the result
	 */
	void onDataLoad(T result);

	/**
	 * On error.
	 *
	 * @param errorMessage the error message
	 */
	void onError(U errorMessage);
}
