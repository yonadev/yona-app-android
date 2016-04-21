/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.yona.app.R;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.GoalManager;
import nu.yona.app.api.manager.impl.ActivityCategoryManagerImpl;
import nu.yona.app.api.manager.impl.GoalManagerImpl;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by bhargavsuthar on 13/04/16.
 */
public class BaseGoalCreateFragment extends BaseFragment {

    protected ListView mGoalListView;
    protected ListView mGoalCreationListView;
    protected ImageButton btnGoalAdd;
    protected YonaFontTextView mDescTab;
    protected List<YonaGoal> budgetCategoriesGoalList;
    protected List<YonaGoal> timeZoneCategoriesGoalList;
    protected List<YonaGoal> noGoCategoriesGoalList;
    protected List<YonaActivityCategories> mYonaActivityCategoriesList;
    protected HashMap<String, String> mGoalCategoriesMap;
    private GoalManager goalManager;
    private ActivityCategoryManager activityCategoryManager;
    private GoalCategoryListAdapter categoryGoalListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_creation_layout, null);

        goalManager = new GoalManagerImpl(getActivity());
        activityCategoryManager = new ActivityCategoryManagerImpl(getActivity());

        budgetCategoriesGoalList = new ArrayList<YonaGoal>();
        timeZoneCategoriesGoalList = new ArrayList<YonaGoal>();
        noGoCategoriesGoalList = new ArrayList<YonaGoal>();
        mYonaActivityCategoriesList = new ArrayList<YonaActivityCategories>();
        mGoalCategoriesMap = new HashMap<String, String>();

        getListOfCategory();
        filterCategoriesGoal();

        mGoalListView = (ListView) view.findViewById(R.id.goal_listview);
        mGoalCreationListView = (ListView) view.findViewById(R.id.new_goal_listview);
        categoryGoalListAdapter = new GoalCategoryListAdapter(getActivity(), mYonaActivityCategoriesList);
        mGoalCreationListView.setAdapter(categoryGoalListAdapter);
        btnGoalAdd = (ImageButton) view.findViewById(R.id.img_add_goal);
        mDescTab = (YonaFontTextView) view.findViewById(R.id.txt_header_text);
        return view;
    }

    public synchronized void showCurrentGoalListView() {
        btnGoalAdd.setVisibility(View.VISIBLE);
        mGoalListView.setVisibility(View.VISIBLE);
        mGoalCreationListView.setVisibility(View.GONE);
    }


    public synchronized void showNewListOfGoalView() {
        btnGoalAdd.setVisibility(View.GONE);
        mGoalListView.setVisibility(View.GONE);
        mGoalCreationListView.setVisibility(View.VISIBLE);
    }

    private synchronized void getListOfCategory() {
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
                    if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString()) && mYonaGoal.getMaxDurationMinutes() > 0) {
                        budgetCategoriesGoalList.add(mYonaGoal);
                    } else if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString()) && mYonaGoal.getMaxDurationMinutes() > 0) {
                        timeZoneCategoriesGoalList.add(mYonaGoal);
                    } else {
                        noGoCategoriesGoalList.add(mYonaGoal);
                    }
                }
            }
        }
    }

    /**
     * It will check the visibility of child View
     */
    public boolean checkIsChildViewVisible() {
        return mGoalCreationListView.getVisibility() == View.VISIBLE ? true : false;
    }

    /**
     * onBackpressed of button it will call from main activity
     */
    public void onBackPressedView() {
        showCurrentGoalListView();
    }

}
