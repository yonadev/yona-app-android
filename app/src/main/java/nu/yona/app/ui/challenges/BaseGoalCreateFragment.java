/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.ChallengesManager;
import nu.yona.app.api.manager.GoalManager;
import nu.yona.app.api.manager.impl.ChallengesManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.ChallengesEnum;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 13/04/16.
 */
public class BaseGoalCreateFragment extends BaseFragment {

    public ChallengesManager challengesManager;
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
    private YonaActivity activity;
    private int CURRENT_TAB;
    public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            handleClickEvent(parent.getAdapter().getItem(position));
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_creation_layout, null);

        activity = (YonaActivity) getActivity();
        challengesManager = new ChallengesManagerImpl(activity);
        mGoalListView = (ListView) view.findViewById(R.id.goal_listview);
        mGoalCreationListView = (ListView) view.findViewById(R.id.new_goal_listview);
        categoryGoalListAdapter = new GoalCategoryListAdapter(activity, challengesManager.getListOfCategories());
        mGoalCreationListView.setAdapter(categoryGoalListAdapter);
        btnGoalAdd = (ImageButton) view.findViewById(R.id.img_add_goal);
        mDescTab = (YonaFontTextView) view.findViewById(R.id.txt_header_text);
        mGoalCreationListView.setOnItemClickListener(itemClickListener);
        return view;
    }

    public synchronized void showCurrentGoalListView(int tab) {
        btnGoalAdd.setVisibility(View.VISIBLE);
        mGoalListView.setVisibility(View.VISIBLE);
        mGoalCreationListView.setVisibility(View.GONE);
        CURRENT_TAB = tab;
    }

    public synchronized void showNewListOfGoalView(int tab) {
        btnGoalAdd.setVisibility(View.GONE);
        mGoalListView.setVisibility(View.GONE);
        mGoalCreationListView.setVisibility(View.VISIBLE);
        CURRENT_TAB = tab;
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
        showCurrentGoalListView(CURRENT_TAB);
    }

    private void handleClickEvent(Object object) {
        Intent goalIntent = new Intent(IntentEnum.ACTION_CHALLENGES_GOAL.getActionString());
        if (object != null) {
            if (object instanceof YonaGoal) {
                goalIntent.putExtra(AppConstant.GOAL_OBJECT, (YonaGoal) object);
            } else if (object instanceof YonaActivityCategories) {
                goalIntent.putExtra(AppConstant.GOAL_OBJECT, challengesManager.getYonaGoalByCategoryType((YonaActivityCategories) object));
            }
        }
        switch (ChallengesEnum.getEnum(CURRENT_TAB)) {
            case CREDIT_TAB:
                activity.replaceFragment(goalIntent);
                break;
            case ZONE_TAB:
                activity.replaceFragment(goalIntent);
                break;
            case NO_GO_TAB:
                addNoGoChallange(object);
                break;
            default:
                break;
        }
    }

    private void addNoGoChallange(Object object) {
        if (object instanceof YonaActivityCategories) {
            YonaActivityCategories categories = (YonaActivityCategories) object;
            activity.showLoadingView(true, null);
            challengesManager.postBudgetGoals(0, categories, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    activity.showLoadingView(false, null);
                    showCurrentGoalListView(CURRENT_TAB);
                }

                @Override
                public void onError(Object errorMessage) {
                    activity.showLoadingView(false, null);
                    String message;
                    if (errorMessage instanceof ErrorMessage) {
                        message = ((ErrorMessage) errorMessage).getMessage();
                    } else {
                        message = errorMessage.toString();
                    }
                    CustomAlertDialog.show(activity, message, getString(R.string.ok));
                }
            });
        }
    }
}
