/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by kinnarvasa on 02/09/16.
 */

public interface PauseResumeHook
{
	void onPause(Fragment curFragment);

	void onResume(Fragment curFragment);

	void onPause(Activity activity);

	void onResume(Activity activity);
}
