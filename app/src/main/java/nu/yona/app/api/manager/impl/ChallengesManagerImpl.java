/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.ChallengesManager;
import nu.yona.app.api.manager.GoalManager;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.Links;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.listener.DataLoadListener;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public class ChallengesManagerImpl implements ChallengesManager {

    private final GoalManager goalManager;
    private final ActivityCategoryManager activityCategoryManager;
    private final List<YonaActivityCategories> mYonaActivityCategoriesList;
    private final HashMap<String, String> mGoalCategoriesMap;
    private final List<YonaGoal> budgetCategoriesGoalList;
    private final List<YonaGoal> timeZoneCategoriesGoalList;
    private final List<YonaGoal> noGoCategoriesGoalList;

    public ChallengesManagerImpl(Context context) {
        activityCategoryManager = new ActivityCategoryManagerImpl(context);
        goalManager = new GoalManagerImpl(context);
        mYonaActivityCategoriesList = new ArrayList<>();
        budgetCategoriesGoalList = new ArrayList<>();
        timeZoneCategoriesGoalList = new ArrayList<>();
        noGoCategoriesGoalList = new ArrayList<>();
        mGoalCategoriesMap = new HashMap<>();
        updateCategoriesAndGoals();
    }

    @Override
    public void updateCategoriesAndGoals() {
        getListOfCategory();
        filterCategoriesGoal();
    }

    private synchronized void getListOfCategory() {
        mYonaActivityCategoriesList.clear();
        ActivityCategories embeddedActivityCategories = activityCategoryManager.getListOfActivityCategories();
        if (embeddedActivityCategories != null && embeddedActivityCategories.getEmbeddedActivityCategories() != null && embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories() != null) {
            for (YonaActivityCategories activityCategories : embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories()) {
                mYonaActivityCategoriesList.add(activityCategories);
                if (!TextUtils.isEmpty(activityCategories.getName()) && !TextUtils.isEmpty(activityCategories.get_links().getSelf().getHref())) {
                    mGoalCategoriesMap.put(activityCategories.getName(), activityCategories.get_links().getSelf().getHref());
                }
            }

        }
    }


    private synchronized void filterCategoriesGoal() {
        budgetCategoriesGoalList.clear();
        timeZoneCategoriesGoalList.clear();
        noGoCategoriesGoalList.clear();
        Goals userGoals = goalManager.getUserGoalFromDb();

        if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals().size() > 0) {
            for (YonaGoal mYonaGoal : userGoals.getEmbedded().getYonaGoals()) {
                if (mYonaGoal != null) {
                    for (Map.Entry<String, String> entry : mGoalCategoriesMap.entrySet()) {
                        if (entry.getValue().equals(mYonaGoal.getLinks().getYonaActivityCategory().getHref())) {
                            mYonaGoal.setActivityCategoryName(entry.getKey());
                            break;
                        }
                    }
                    if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString()) && (mYonaGoal.getMaxDurationMinutes() > 0)) {
                        budgetCategoriesGoalList.add(mYonaGoal);
                    } else if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString()) && (mYonaGoal.getZones() != null)) {
                        timeZoneCategoriesGoalList.add(mYonaGoal);
                    } else {
                        noGoCategoriesGoalList.add(mYonaGoal);
                    }
                }
            }
        }

    }

    @Override
    public List<YonaActivityCategories> getListOfCategories() {
        getListOfCategory();
        return mYonaActivityCategoriesList;
    }

    /**
     * Get list of Budget Goals
     *
     * @return
     */
    @Override
    public List<YonaGoal> getListOfBudgetGoals() {
        filterCategoriesGoal();
        return budgetCategoriesGoalList;
    }

    /**
     * Get list of Timezone Goals
     *
     * @return
     */
    @Override
    public List<YonaGoal> getListOfTimeZoneGoals() {
        filterCategoriesGoal();
        return timeZoneCategoriesGoalList;
    }

    /**
     * Get list of NOGO Goals
     *
     * @return
     */
    @Override
    public List<YonaGoal> getListOfNoGoGoals() {
        filterCategoriesGoal();
        return noGoCategoriesGoalList;
    }

    @Override
    public YonaGoal getYonaGoalByCategoryType(YonaActivityCategories activityCategories) {
        Goals userGoals = goalManager.getUserGoalFromDb();
        if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals().size() > 0) {
            for (YonaGoal mYonaGoal : userGoals.getEmbedded().getYonaGoals()) {
                if (mYonaGoal != null && activityCategories.get_links().getSelf().getHref().equalsIgnoreCase(mYonaGoal.getLinks().getYonaActivityCategory().getHref())) {
                    mYonaGoal.setActivityCategoryName(activityCategories.getName());
                    return mYonaGoal;
                }
            }
        }

        return null;
    }

    /**
     * Delete Goal by User's Goal id(url)
     *
     * @param goalId
     * @param listener
     */
    @Override
    public void deleteGoal(String goalId, DataLoadListener listener) {

    }

    /**
     * Create a new Goal
     *
     * @param yonaGoal
     * @param listener
     */
    @Override
    public void createNewGoal(YonaGoal yonaGoal, DataLoadListener listener) {

    }

    /**
     * @param time     milliseconds
     * @param goal     YonaGoal selected object
     * @param listener
     */
    public void postBudgetGoals(long time, YonaGoal goal, final DataLoadListener listener) {
        goalManager.postBudgetGoals(getPostYonaGoalForBudget(time, goal), listener);
    }

    public void postBudgetGoals(long time, final YonaActivityCategories category, final DataLoadListener listener) {
        goalManager.postBudgetGoals(getPostYonaGoalForBudget(time, category), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {

                listener.onDataLoad(result);
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }

    private PostBudgetYonaGoal getPostYonaGoalForBudget(long time, YonaActivityCategories category) {
        PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
        Links links = new Links();
        links.setYonaActivityCategory(category.get_links().getSelf());
        postBudgetYonaGoal.setMaxDurationMinutes(time);
        postBudgetYonaGoal.setLinks(links);

        return postBudgetYonaGoal;
    }

    private PostBudgetYonaGoal getPostYonaGoalForBudget(long time, YonaGoal goal) {
        PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
        Links links = new Links();
        Href yonaActivityCategory = new Href();
        yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
        links.setYonaActivityCategory(yonaActivityCategory);
        postBudgetYonaGoal.setMaxDurationMinutes(time);
        postBudgetYonaGoal.setLinks(links);

        return postBudgetYonaGoal;
    }

    private PostBudgetYonaGoal getUpdateYonaGoalForBudget(long time, YonaGoal goal) {
        PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
        Links links = new Links();
        Href yonaActivityCategory = new Href();
        yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
        Href yonaSelfLink = new Href();
        yonaSelfLink.setHref(goal.getLinks().getSelf().getHref());
        links.setYonaActivityCategory(yonaActivityCategory);
        links.setSelf(yonaSelfLink);
        postBudgetYonaGoal.setMaxDurationMinutes(time);
        postBudgetYonaGoal.setLinks(links);

        return postBudgetYonaGoal;
    }

    /**
     * @param timeGoal Array of Time Goals
     * @param goal     selected yona Goal
     * @param listener
     */
    public void postTimeGoals(List<String> timeGoal, YonaGoal goal, final DataLoadListener listener) {
        goalManager.postTimeZoneGoals(getPostYonaGoalForTimeZone(timeGoal, goal), listener);
    }

    @Override
    public void postTimeGoals(List<String> timeGoal, YonaActivityCategories categories, DataLoadListener listener) {
        goalManager.postTimeZoneGoals(getPostYonaGoalForTimeZone(timeGoal, categories), listener);
    }

    @Override
    public void updateTimeGoals(List<String> timeGoal, YonaGoal goal, DataLoadListener listener) {
        goalManager.updateTimeZoneGoals(getUpdateYonaGoalForTimeZone(timeGoal, goal), listener);
    }

    @Override
    public void updateBudgetGoals(long time, YonaGoal goal, DataLoadListener listener) {
        goalManager.updateBudgetGoals(getUpdateYonaGoalForBudget(time, goal), listener);
    }

    private PostTimeZoneYonaGoal getPostYonaGoalForTimeZone(List<String> timeGoal, YonaGoal goal) {
        PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
        Links links = new Links();
        Href yonaActivityCategory = new Href();
        yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
        links.setYonaActivityCategory(yonaActivityCategory);
        postBudgetYonaGoal.setZones(timeGoal);
        postBudgetYonaGoal.setLinks(links);

        return postBudgetYonaGoal;
    }

    private PostTimeZoneYonaGoal getPostYonaGoalForTimeZone(List<String> timeGoal, YonaActivityCategories category) {
        PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
        Links links = new Links();
        links.setYonaActivityCategory(category.get_links().getSelf());
        postBudgetYonaGoal.setLinks(links);
        postBudgetYonaGoal.setZones(timeGoal);
        return postBudgetYonaGoal;
    }

    private PostTimeZoneYonaGoal getUpdateYonaGoalForTimeZone(List<String> timeGoal, YonaGoal goal) {
        PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
        postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
        Links links = new Links();
        Href yonaActivityCategory = new Href();
        yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
        Href yonaSelf = new Href();
        yonaSelf.setHref(goal.getLinks().getSelf().getHref());
        links.setYonaActivityCategory(yonaActivityCategory);
        links.setSelf(yonaSelf);
        postBudgetYonaGoal.setZones(timeGoal);
        postBudgetYonaGoal.setLinks(links);

        return postBudgetYonaGoal;
    }

    /**
     * @param yonaGoal YonaGoal object to delete it.
     * @param listener
     */
    public void deleteGoal(YonaGoal yonaGoal, DataLoadListener listener) {
        goalManager.deleteGoal(yonaGoal, listener);
    }

    /**
     * It will check and return which type of goal is
     *
     * @param yonaGoal
     * @return
     */
    @Override
    public GoalsEnum typeOfGoal(YonaGoal yonaGoal) {

        if (yonaGoal != null) {
            if (yonaGoal.getMaxDurationMinutes() > 0 && GoalsEnum.BUDGET_GOAL.getActionString().equalsIgnoreCase(yonaGoal.getType())) {
                return GoalsEnum.BUDGET_GOAL;
            } else if (yonaGoal.getZones() != null && GoalsEnum.TIME_ZONE_GOAL.getActionString().equalsIgnoreCase(yonaGoal.getType())) {
                return GoalsEnum.TIME_ZONE_GOAL;
            } else {
                return GoalsEnum.NOGO;
            }
        }
        return null;
    }
}
