/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import nu.yona.app.listener.DataLoadListenerImpl;

/**
 * Created by kinnarvasa on 30/03/16.
 */
public interface YonaManager
{
	/**
	 * Posts OpenAppEvent
	 *
	 * @param listener the listener
	 */
	void postOpenAppEvent(String url, String yonaPassword, DataLoadListenerImpl listener);

}
