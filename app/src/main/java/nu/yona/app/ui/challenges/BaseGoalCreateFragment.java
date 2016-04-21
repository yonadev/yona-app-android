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

import nu.yona.app.R;
import nu.yona.app.api.manager.ChallengesManager;
import nu.yona.app.api.manager.impl.ChallengesManagerImpl;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 13/04/16.
 */
public class BaseGoalCreateFragment extends BaseFragment {

    protected ListView mGoalListView;
    protected ListView mGoalCreationListView;
    protected ImageButton btnGoalAdd;
    protected YonaFontTextView mDescTab;
    private GoalCategoryListAdapter categoryGoalListAdapter;
    public ChallengesManager challengesManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_creation_layout, null);

        challengesManager = new ChallengesManagerImpl(getActivity());
        mGoalListView = (ListView) view.findViewById(R.id.goal_listview);
        mGoalCreationListView = (ListView) view.findViewById(R.id.new_goal_listview);
        categoryGoalListAdapter = new GoalCategoryListAdapter(getActivity(), challengesManager.getListOfCategories());
        mGoalCreationListView.setAdapter(categoryGoalListAdapter);
        btnGoalAdd = (ImageButton) view.findViewById(R.id.img_add_goal);
        mDescTab = (YonaFontTextView) view.findViewById(R.id.txt_header_text);
        mGoalCreationListView.setOnItemClickListener(itemClickListener);
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


    public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent goalIntent = new Intent(IntentEnum.ACTION_CHALLENGES_GOAL.getActionString());
            Object object = parent.getAdapter().getItem(position);
            if (object != null) {
                if (object instanceof YonaGoal) {
                    goalIntent.putExtra(AppConstant.GOAL_OBJECT, (YonaGoal) object);
                } else if (object instanceof YonaActivityCategories) {
                    goalIntent.putExtra(AppConstant.GOAL_OBJECT, challengesManager.getYonaGoalByCategoryType((YonaActivityCategories) object));
                }
            }
            ((YonaActivity) getActivity()).replaceFragment(goalIntent);
        }
    };
}
