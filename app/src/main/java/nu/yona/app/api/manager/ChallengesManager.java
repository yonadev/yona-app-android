/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import java.util.List;

import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public interface ChallengesManager {

    List<YonaActivityCategories> getListOfCategories();

    List<YonaGoal> getListOfBudgetGoals();

    List<YonaGoal> getListOfTimeZoneGoals();

    List<YonaGoal> getListOfNoGoGoals();

    void deleteGoal(String goalId, DataLoadListener listener);

    void createNewGoal(YonaGoal yonaGoal, DataLoadListener listener);

}
