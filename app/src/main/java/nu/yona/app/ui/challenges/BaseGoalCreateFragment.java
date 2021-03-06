/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.ChallengesEnum;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 13/04/16.
 */
public class BaseGoalCreateFragment extends BaseFragment implements EventChangeListener
{
	/**
	 * The M goal list view.
	 */
	ListView mGoalListView;
	/**
	 * The Btn goal add.
	 */
	ImageButton btnGoalAdd;
	/**
	 * The M desc tab.
	 */
	YonaFontTextView mDescTab;
	/**
	 * The Category goal list adapter.
	 */
	GoalCategoryListAdapter categoryGoalListAdapter;
	private ListView mGoalCreationListView;
	private int CURRENT_TAB;
	/**
	 * The Item click listener.
	 */
	final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			handleClickEvent(parent.getAdapter().getItem(position));
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.goal_creation_layout, null);

		YonaApplication.getEventChangeManager().registerListener(this);
		mGoalListView = (ListView) view.findViewById(R.id.goal_listview);
		mGoalCreationListView = (ListView) view.findViewById(R.id.new_goal_listview);
		List<YonaActivityCategories> goals = APIManager.getInstance().getChallengesManager().getListOfCategories();
		categoryGoalListAdapter = getCategoryGoalListAdapter();
		mGoalCreationListView.setAdapter(categoryGoalListAdapter);
		btnGoalAdd = (ImageButton) view.findViewById(R.id.img_add_goal);
		mDescTab = (YonaFontTextView) view.findViewById(R.id.txt_header_text);
		mGoalCreationListView.setOnItemClickListener(itemClickListener);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		updateCategoryView();
	}

	private GoalCategoryListAdapter getCategoryGoalListAdapter()
	{
		if (categoryGoalListAdapter == null)
		{
			categoryGoalListAdapter = new GoalCategoryListAdapter(YonaActivity.getActivity(), APIManager.getInstance().getChallengesManager().getListOfCategories());
		}
		return categoryGoalListAdapter;
	}

	/**
	 * Update category view.
	 */
	protected void updateCategoryView()
	{
		if (isAdded())
		{
			if (APIManager.getInstance().getChallengesManager().getListOfCategories().size() == 0)
			{
				btnGoalAdd.setVisibility(View.INVISIBLE);
				mGoalCreationListView.setVisibility(View.GONE);
				mGoalListView.setVisibility(View.VISIBLE);
			}
			else
			{
				getCategoryGoalListAdapter().notifyDataSetChanged(APIManager.getInstance().getChallengesManager().getListOfCategories());
				if (mGoalCreationListView.getVisibility() == View.VISIBLE)
				{
					btnGoalAdd.setVisibility(View.INVISIBLE);
					mGoalListView.setVisibility(View.GONE);
				}
				else
				{
					btnGoalAdd.setVisibility(View.VISIBLE);
					mGoalListView.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * Show current goal list view.
	 *
	 * @param tab the tab
	 */
	synchronized void showCurrentGoalListView(int tab)
	{
		btnGoalAdd.setVisibility(View.VISIBLE);
		mGoalListView.setVisibility(View.VISIBLE);
		mGoalCreationListView.setVisibility(View.GONE);
		CURRENT_TAB = tab;
	}

	/**
	 * Show new list of goal view.
	 *
	 * @param tab the tab
	 */
	synchronized void showNewListOfGoalView(int tab)
	{
		btnGoalAdd.setVisibility(View.INVISIBLE);
		mGoalListView.setVisibility(View.GONE);
		mGoalCreationListView.setVisibility(View.VISIBLE);
		CURRENT_TAB = tab;
	}

	/**
	 * It will check the visibility of child View
	 *
	 * @return the boolean
	 */
	public boolean checkIsChildViewVisible()
	{
		return mGoalCreationListView.getVisibility() == View.VISIBLE;
	}

	/**
	 * onBackpressed of button it will call from main activity
	 */
	public void onBackPressedView()
	{
		switch (ChallengesEnum.getEnum(CURRENT_TAB))
		{
			case CREDIT_TAB:
				mDescTab.setText(getActivity().getString(R.string.challenge_budget_title));
				break;
			case NO_GO_TAB:
				mDescTab.setText(getActivity().getString(R.string.challengesnogoheader));
				break;
			case ZONE_TAB:
				mDescTab.setText(getActivity().getString(R.string.challenge_timezone_title));
				break;
		}

		showCurrentGoalListView(CURRENT_TAB);
	}

	private void handleClickEvent(Object object)
	{
		Intent goalIntent = new Intent(IntentEnum.ACTION_CHALLENGES_GOAL.getActionString());
		if (object != null)
		{

			if (object instanceof YonaGoal)
			{
				goalIntent.putExtra(AppConstant.GOAL_OBJECT, (YonaGoal) object);
				YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.CHALLENGES_SCREEN, ((YonaGoal) object).getActivityCategoryName());
			}
			else if (object instanceof YonaActivityCategories)
			{
				YonaGoal yonaGoal = APIManager.getInstance().getChallengesManager().getYonaGoalByCategoryType((YonaActivityCategories) object);
				if (yonaGoal == null)
				{
					goalIntent.putExtra(AppConstant.GOAL_OBJECT, (YonaActivityCategories) object);
					YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.CHALLENGES_SCREEN, ((YonaActivityCategories) object).getName());
				}
				else
				{
					Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), getString(R.string.category_added), Snackbar.LENGTH_LONG).show();
					return;
				}
			}
		}
		switch (ChallengesEnum.getEnum(CURRENT_TAB))
		{
			case CREDIT_TAB:
				goalIntent.putExtra(AppConstant.NEW_GOAL_TYPE, GoalsEnum.BUDGET_GOAL.getActionString());
				break;
			case ZONE_TAB:
				goalIntent.putExtra(AppConstant.NEW_GOAL_TYPE, GoalsEnum.TIME_ZONE_GOAL.getActionString());
				break;
			case NO_GO_TAB:
				goalIntent.putExtra(AppConstant.NEW_GOAL_TYPE, GoalsEnum.NOGO.getActionString());
				break;
			default:
				break;
		}
		YonaActivity.getActivity().replaceFragment(goalIntent);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_UPDATE_GOALS:
				updateCategoryView();
				categoryGoalListAdapter.notifyDataSetChanged(APIManager.getInstance().getChallengesManager().getListOfCategories());
				break;
			default:
				break;
		}

	}
}
