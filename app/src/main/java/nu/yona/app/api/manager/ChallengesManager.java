/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager;

import java.util.List;

import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public interface ChallengesManager
{

	/**
	 * Gets list of categories.
	 *
	 * @return the list of categories
	 */
	List<YonaActivityCategories> getListOfCategories();

	/**
	 * Gets list of budget goals.
	 *
	 * @return the list of budget goals
	 */
	List<YonaGoal> getListOfBudgetGoals();

	/**
	 * Gets list of time zone goals.
	 *
	 * @return the list of time zone goals
	 */
	List<YonaGoal> getListOfTimeZoneGoals();

	/**
	 * Gets list of no go goals.
	 *
	 * @return the list of no go goals
	 */
	List<YonaGoal> getListOfNoGoGoals();

	/**
	 * Gets yona goal by category type.
	 *
	 * @param activityCategories the activity categories
	 * @return the yona goal by category type
	 */
	YonaGoal getYonaGoalByCategoryType(YonaActivityCategories activityCategories);

	/**
	 * Post budget goals.
	 *
	 * @param time     the time
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void postBudgetGoals(long time, YonaGoal goal, final DataLoadListener listener);

	/**
	 * Post budget goals.
	 *
	 * @param time       the time
	 * @param categories the categories
	 * @param listener   the listener
	 */
	void postBudgetGoals(long time, YonaActivityCategories categories, final DataLoadListener listener);

	/**
	 * Post time goals.
	 *
	 * @param timeGoal the time goal
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void postTimeGoals(List<String> timeGoal, YonaGoal goal, final DataLoadListener listener);

	/**
	 * Post time goals.
	 *
	 * @param timeGoal   the time goal
	 * @param categories the categories
	 * @param listener   the listener
	 */
	void postTimeGoals(List<String> timeGoal, YonaActivityCategories categories, final DataLoadListener listener);

	/**
	 * Update time goals.
	 *
	 * @param timeGoal the time goal
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void updateTimeGoals(List<String> timeGoal, YonaGoal goal, final DataLoadListener listener);

	/**
	 * Update budget goals.
	 *
	 * @param time     the time
	 * @param goal     the goal
	 * @param listener the listener
	 */
	void updateBudgetGoals(long time, YonaGoal goal, final DataLoadListener listener);

	/**
	 * Delete goal.
	 *
	 * @param yonaGoal the yona goal
	 * @param listener the listener
	 */
	void deleteGoal(YonaGoal yonaGoal, DataLoadListener listener);

	/**
	 * Type of goal goals enum.
	 *
	 * @param yonaGoal the yona goal
	 * @return the goals enum
	 */
	GoalsEnum typeOfGoal(YonaGoal yonaGoal);

	/**
	 * Gets selected goal categories.
	 *
	 * @param budgetType the budget type
	 * @return the selected goal categories
	 */
	YonaActivityCategories getSelectedGoalCategories(String budgetType);

	/**
	 * Gets user goal.
	 *
	 * @param listener the listener
	 */
	void getUserGoal(DataLoadListener listener);
}
