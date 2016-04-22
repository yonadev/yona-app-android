/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.enums.ChallengesEnum;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class CreditFragment extends BaseGoalCreateFragment implements View.OnClickListener {

    private GoalListAdapter mGoalListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        showCurrentGoalListView(ChallengesEnum.CREDIT_TAB.getTab());
        mGoalListAdapter = new GoalListAdapter(getActivity(), challengesManager.getListOfBudgetGoals());
        mGoalListView.setAdapter(mGoalListAdapter);
        mGoalListView.setOnItemClickListener(itemClickListener);
        mDescTab.setText(getActivity().getString(R.string.challenges_tegoed_header_text));
        btnGoalAdd.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_goal:
                //show new goal list creation view
                showNewListOfGoalView(ChallengesEnum.CREDIT_TAB.getTab());
                break;
            default:
                break;
        }
    }
}
