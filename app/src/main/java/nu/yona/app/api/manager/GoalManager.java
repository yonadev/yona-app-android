/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import nu.yona.app.api.model.Goals;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public interface GoalManager {

    void getUserGoal(DataLoadListener listener);

    Goals getUserGoalFromDb();

}
