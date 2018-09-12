/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public interface GoalManager
{

	/**
	 * Gets user goal.
	 *
	 * @param listener the listener
	 */
	void getUserGoal(DataLoadListener listener);

	/**
	 * Gets user goal from db.
	 *
	 * @return the user goal from db
	 */
	Goals getUserGoalFromDb();

	/**
	 * Post budget goals.
	 *
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void postBudgetGoals(PostBudgetYonaGoal goal, DataLoadListener listener);

	/**
	 * Post time zone goals.
	 *
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void postTimeZoneGoals(PostTimeZoneYonaGoal goal, DataLoadListener listener);

	/**
	 * Delete goal.
	 *
	 * @param yonaGoal the yona goal
	 * @param listener the listener
	 */
	void deleteGoal(YonaGoal yonaGoal, DataLoadListener listener);

	/**
	 * Update budget goals.
	 *
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void updateBudgetGoals(PostBudgetYonaGoal goal, DataLoadListener listener);

	/**
	 * Update time zone goals.
	 *
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void updateTimeZoneGoals(PostTimeZoneYonaGoal goal, DataLoadListener listener);

	/**
	 * Save goals.
	 *
	 * @param goals    the goals
	 * @param listener the listener
	 */
	void saveGoals(Goals goals, DataLoadListener listener);
}
