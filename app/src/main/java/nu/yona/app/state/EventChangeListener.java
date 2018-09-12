/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.state;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public interface EventChangeListener
{
	/**
	 * On state change.
	 *
	 * @param eventType the event type
	 * @param object    the object
	 */
	void onStateChange(int eventType, Object object);
}
