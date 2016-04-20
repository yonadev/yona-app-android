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
import android.widget.LinearLayout;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class ChallengesFragment extends BaseFragment {
    private final float TAB_ALPHA_SELECTED = 1;
    private final double TAB_ALPHA_UNSELECTED = 0.5;
    private final int TAB_INDEX_ONE = 0;
    private final int TAB_INDEX_TWO = 1;
    private final int TAB_INDEX_THREE = 2;
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CreditFragment creditFragment;
    private ZoneFragment zoneFragment;
    private NoGoFragment noGoFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.challenges_layout, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                updateTabViewBackground(tab, TAB_ALPHA_SELECTED);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabViewBackground(tab, (float) TAB_ALPHA_UNSELECTED);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_ONE), TAB_ALPHA_SELECTED);
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_TWO), (float) TAB_ALPHA_UNSELECTED);
        updateTabViewBackground(tabLayout.getTabAt(TAB_INDEX_THREE), (float) TAB_ALPHA_UNSELECTED);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        creditFragment = new CreditFragment();
        zoneFragment = new ZoneFragment();
        noGoFragment = new NoGoFragment();
        adapter.addFragment(creditFragment, getString(R.string.challenges_credit));
        adapter.addFragment(zoneFragment, getString(R.string.challenges_zone));
        adapter.addFragment(noGoFragment, getString(R.string.challenges_no_go));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tabOne = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_layout, null);
        ((YonaFontTextView) tabOne.findViewById(R.id.tab_text)).setText(getString(R.string.challenges_credit));
        ((ImageView) tabOne.findViewById(R.id.tab_image)).setImageResource(R.drawable.icn_challenge_timezone);
        tabLayout.getTabAt(TAB_INDEX_ONE).setCustomView(tabOne);

        LinearLayout tabTwo = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_layout, null);
        ((YonaFontTextView) tabTwo.findViewById(R.id.tab_text)).setText(getString(R.string.challenges_zone));
        ((ImageView) tabTwo.findViewById(R.id.tab_image)).setImageResource(R.drawable.icn_challenge_timebucket);
        tabLayout.getTabAt(TAB_INDEX_TWO).setCustomView(tabTwo);

        LinearLayout tabThree = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_layout, null);
        ((YonaFontTextView) tabThree.findViewById(R.id.tab_text)).setText(getString(R.string.challenges_no_go));
        ((ImageView) tabThree.findViewById(R.id.tab_image)).setImageResource(R.drawable.icn_challenge_nogo);
        tabLayout.getTabAt(TAB_INDEX_THREE).setCustomView(tabThree);
    }

    /**
     * On selection of tab need to change the background of tab selection
     */
    private void updateTabViewBackground(TabLayout.Tab tab, float alpha) {
        tab.getCustomView().setAlpha(alpha);
    }

    private void updateChallengeCounter(TabLayout.Tab tab, int count) {
        //Todo update the count of goal set
    }

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

}
