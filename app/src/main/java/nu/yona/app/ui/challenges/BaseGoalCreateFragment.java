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

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by bhargavsuthar on 13/04/16.
 */
public class BaseGoalCreateFragment extends BaseFragment {

    protected ListView mGoalListView;
    protected ListView mGoalCreationListView;
    protected ImageButton btnGoalAdd;
    protected YonaFontTextView mDescTab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_creation_layout, null);
        mGoalListView = (ListView) view.findViewById(R.id.goal_listview);
        mGoalCreationListView = (ListView) view.findViewById(R.id.new_goal_listview);
        btnGoalAdd = (ImageButton) view.findViewById(R.id.img_add_goal);
        mDescTab = (YonaFontTextView) view.findViewById(R.id.txt_header_text);
        return view;
    }

    public void showCurrentGoalListView() {
        //Todo display list of goals here and visible view
        btnGoalAdd.setVisibility(View.VISIBLE);
        mGoalListView.setVisibility(View.VISIBLE);
        mGoalCreationListView.setVisibility(View.GONE);
    }


    public void showNewListOfGoalView() {
        //Todo display list of goals here and visible view
        btnGoalAdd.setVisibility(View.GONE);
        mGoalListView.setVisibility(View.GONE);
        mGoalCreationListView.setVisibility(View.VISIBLE);
    }
}
