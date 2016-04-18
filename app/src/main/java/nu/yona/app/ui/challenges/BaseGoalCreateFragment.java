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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
    private GoalManager goalManager;
    private ActivityCategoryManager activityCategoryManager;
    protected List<YonaGoal> budgetCategoriesGoalList;
    protected List<YonaGoal> timeZoneCategoriesGoalList;
    protected List<YonaGoal> noGoCategoriesGoalList;
    protected List<YonaActivityCategories> mYonaActivityCategoriesList;

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
            }

        }
    }


    private synchronized void filterCategoriesGoal() {
        Goals userGoals = goalManager.getUserGoalFromDb();

        if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals().size() > 0) {
            for (YonaGoal mYonaGoal : userGoals.getEmbedded().getYonaGoals()) {
                if (mYonaGoal != null) {
                    if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString())) {
                        budgetCategoriesGoalList.add(mYonaGoal);
                    } else if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
                        timeZoneCategoriesGoalList.add(mYonaGoal);
                    } else {
                        noGoCategoriesGoalList.add(mYonaGoal);
                    }
                }
            }
        }
    }

}
