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
import nu.yona.app.YonaApplication;
import nu.yona.app.enums.ChallengesEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class NoGoFragment extends BaseGoalCreateFragment implements View.OnClickListener, EventChangeListener {

    private GoalListAdapter mGoalListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mGoalListAdapter = new GoalListAdapter(getActivity(), challengesManager.getListOfNoGoGoals());
        mGoalListView.setAdapter(mGoalListAdapter);
        mGoalListView.setOnItemClickListener(itemClickListener);
        showCurrentGoalListView(ChallengesEnum.NO_GO_TAB.getTab());
        btnGoalAdd.setOnClickListener(this);
        showCurrentGoalListView(ChallengesEnum.NO_GO_TAB.getTab());
        mDescTab.setText(getActivity().getString(R.string.challenges_nogo_header_text));
        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        YonaApplication.getEventChangeManager().unRegisterListener(this);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_goal:
                //show new goal list creation view
                showNewListOfGoalView(ChallengesEnum.NO_GO_TAB.getTab());
                break;
            default:
                break;
        }
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_UPDATE_GOALS:
                mGoalListAdapter.notifyDataSetChanged(challengesManager.getListOfNoGoGoals());
                break;
            default:
                break;
        }
    }

}
