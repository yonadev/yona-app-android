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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.impl.ActivityCategoryManagerImpl;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ChallengesFragment extends BaseFragment implements EventChangeListener {
    private final float TAB_ALPHA_SELECTED = 1;
    private final float TAB_ALPHA_UNSELECTED = 0.5f;
    private final int TAB_INDEX_ONE = 0;
    private final int TAB_INDEX_TWO = 1;
    private final int TAB_INDEX_THREE = 2;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CreditFragment creditFragment;
    private ZoneFragment zoneFragment;
    private NoGoFragment noGoFragment;
    private YonaActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenges_layout, null);
        creditFragment = new CreditFragment();
        zoneFragment = new ZoneFragment();
        noGoFragment = new NoGoFragment();
        activity = (YonaActivity) getActivity();
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        YonaApplication.getEventChangeManager().registerListener(this);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                updateCounterTab();
                updateTabViewBackground(tab, TAB_ALPHA_SELECTED);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabViewBackground(tab, TAB_ALPHA_UNSELECTED);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();
        tabLayout.getTabAt(TAB_INDEX_THREE).select();
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_ONE), TAB_ALPHA_UNSELECTED);
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_TWO), TAB_ALPHA_UNSELECTED);
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_THREE), TAB_ALPHA_SELECTED);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivityCategories();
    }

    private void getActivityCategories() {
        new ActivityCategoryManagerImpl(getActivity()).getActivityCategoriesById(new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                getUserGoal();
            }

            @Override
            public void onError(Object errorMessage) {
                activity.showLoadingView(false, null);

            }
        });

    }

    private void getUserGoal() {
        APIManager.getInstance().getChallengesManager().getUserGoal(new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, null);
                activity.showLoadingView(false, null);
            }

            @Override
            public void onError(Object errorMessage) {
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, null);
                activity.showLoadingView(false, null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((YonaActivity) getActivity()).updateTitle(R.string.challenges);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(creditFragment, getString(R.string.challengescredit));
        adapter.addFragment(zoneFragment, getString(R.string.challengeszone));
        adapter.addFragment(noGoFragment, getString(R.string.challengesnogo));
        viewPager.setAdapter(adapter);

    }

    private void setupTabIcons() {
        APIManager.getInstance().getChallengesManager().getListOfCategories();
        if (APIManager.getInstance().getChallengesManager().getListOfBudgetGoals() != null) {
            int budgetGoalCounter = APIManager.getInstance().getChallengesManager().getListOfBudgetGoals().size();
            View budgetTab = getTabView(R.drawable.icn_challenge_timezone, R.string.challengescredit, budgetGoalCounter);
            tabLayout.getTabAt(TAB_INDEX_ONE).setCustomView(budgetTab);
        }

        if (APIManager.getInstance().getChallengesManager().getListOfTimeZoneGoals() != null) {
            int timeZoneGoalCounter = APIManager.getInstance().getChallengesManager().getListOfTimeZoneGoals().size();
            View timeZoneTab = getTabView(R.drawable.icn_challenge_timebucket, R.string.challengeszone, timeZoneGoalCounter);
            tabLayout.getTabAt(TAB_INDEX_TWO).setCustomView(timeZoneTab);
        }

        if (APIManager.getInstance().getChallengesManager().getListOfNoGoGoals() != null) {
            int nogoGoalCounter = APIManager.getInstance().getChallengesManager().getListOfNoGoGoals().size();
            View nogoTab = getTabView(R.drawable.icn_challenge_nogo, R.string.challengesnogo, nogoGoalCounter);
            tabLayout.getTabAt(TAB_INDEX_THREE).setCustomView(nogoTab);
        }

    }

    private View getTabView(int imgResourceId, int titleTab, int counter) {
        View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_layout, null);
        ((YonaFontTextView) tab.findViewById(R.id.tab_text)).setText(getString(titleTab));
        ((ImageView) tab.findViewById(R.id.tab_image)).setImageResource(imgResourceId);
        YonaFontTextView counterTextView = ((YonaFontTextView) tab.findViewById(R.id.tab_item_count));
        if (counter > 0) {
            counterTextView.setVisibility(View.VISIBLE);
            counterTextView.setText("" + counter);
        }
        return tab;
    }

    private void updateCounterTab() {
        APIManager.getInstance().getChallengesManager().getListOfCategories();
        YonaFontTextView counterTextView;
        switch (tabLayout.getSelectedTabPosition()) {
            case TAB_INDEX_ONE:
                counterTextView = ((YonaFontTextView) tabLayout.getTabAt(TAB_INDEX_ONE).getCustomView().findViewById(R.id.tab_item_count));
                if (APIManager.getInstance().getChallengesManager().getListOfBudgetGoals().size() > 0) {
                    counterTextView.setText("" + APIManager.getInstance().getChallengesManager().getListOfBudgetGoals().size());
                    counterTextView.setVisibility(View.VISIBLE);
                } else {
                    counterTextView.setVisibility(View.GONE);
                }
                break;
            case TAB_INDEX_TWO:
                counterTextView = ((YonaFontTextView) tabLayout.getTabAt(TAB_INDEX_TWO).getCustomView().findViewById(R.id.tab_item_count));
                if (APIManager.getInstance().getChallengesManager().getListOfTimeZoneGoals().size() > 0) {
                    counterTextView.setText("" + APIManager.getInstance().getChallengesManager().getListOfTimeZoneGoals().size());
                    counterTextView.setVisibility(View.VISIBLE);
                } else {
                    counterTextView.setVisibility(View.GONE);
                }
                break;
            case TAB_INDEX_THREE:
                counterTextView = ((YonaFontTextView) tabLayout.getTabAt(TAB_INDEX_THREE).getCustomView().findViewById(R.id.tab_item_count));
                if (APIManager.getInstance().getChallengesManager().getListOfNoGoGoals().size() > 0) {
                    counterTextView.setText("" + APIManager.getInstance().getChallengesManager().getListOfNoGoGoals().size());
                    counterTextView.setVisibility(View.VISIBLE);
                } else {
                    counterTextView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * On selection of tab need to change the background of tab selection
     */
    private void updateTabViewBackground(TabLayout.Tab tab, float alpha) {
        if (alpha == TAB_ALPHA_SELECTED) {
            tab.getCustomView().setAlpha(alpha);
            tab.select();
        } else {
            tab.getCustomView().setAlpha(alpha);
        }
    }

    /**
     * Is child view visible boolean.
     *
     * @return the boolean
     */
    public boolean isChildViewVisible() {

        switch (tabLayout.getSelectedTabPosition()) {
            case TAB_INDEX_ONE:
                return creditFragment.checkIsChildViewVisible();
            case TAB_INDEX_TWO:
                return zoneFragment.checkIsChildViewVisible();
            case TAB_INDEX_THREE:
                return noGoFragment.checkIsChildViewVisible();
            default:
                return false;
        }
    }

    /**
     * Update view.
     */
    public void updateView() {

        switch (tabLayout.getSelectedTabPosition()) {
            case TAB_INDEX_ONE:
                creditFragment.onBackPressedView();
                break;
            case TAB_INDEX_TWO:
                zoneFragment.onBackPressedView();
                break;
            case TAB_INDEX_THREE:
                noGoFragment.onBackPressedView();
                break;
            default:
                break;
        }
    }


    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_UPDATE_GOALS:
                updateCounterTab();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }
}
