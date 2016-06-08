/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.recyclerViewDecor.DividerDecoration;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class PerDayFragment extends BaseFragment {

    private RecyclerView listView;
    private PerDayStickyAdapter perDayStickyAdapter;
    private LinearLayoutManager mLayoutManager;
    private EmbeddedYonaActivity embeddedYonaActivity;
    private int currentPage = 0;
    private boolean mIsLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_perday_fragment, null);

        listView = (RecyclerView) view.findViewById(R.id.listView);
        mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());

        perDayStickyAdapter = new PerDayStickyAdapter(new ArrayList<DayActivity>(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO on click of list item, listener will return here.
            }
        });

        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(perDayStickyAdapter);
        listView.addOnScrollListener(mRecyclerViewOnScrollListener);
        setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(perDayStickyAdapter));
        return view;

    }

    /**
     * update RecyclerView item header for grouping section
     *
     * @param headerDecor
     */
    private void setRecyclerHeaderAdapterUpdate(StickyRecyclerHeadersDecoration headerDecor) {
        listView.addItemDecoration(headerDecor);

        // Add decoration for dividers between list items
        listView.addItemDecoration(new DividerDecoration(getActivity()));

        // Add touch listeners
        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(listView, headerDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                    }
                });
    }


    /**
     * Recyclerview's scroll listener when its getting end to load more data till the pages not reached
     */
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && currentPage < embeddedYonaActivity.getEmbedded().getPage().getTotalPages()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        loadMoreItems();
                    }
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    /**
     * Refresh recyclerview's adapter
     */
    private void refreshAdapter() {
        perDayStickyAdapter.clear();
        currentPage = 0;
        getDayActivity();
    }

    /**
     * load more items
     */
    private void loadMoreItems() {
        mIsLoading = true;
        currentPage += 1;
        getDayActivity();
    }

    /**
     * to get the list of user's messages
     */
    private void getDayActivity() {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getActivityManager().getDaysActivity(AppConstant.PAGE_SIZE, currentPage, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                if (isAdded() && result != null && result instanceof EmbeddedYonaActivity) {
                    embeddedYonaActivity = (EmbeddedYonaActivity) result;
                    if (embeddedYonaActivity.getEmbedded() != null && embeddedYonaActivity.getDayActivityList() != null) {
                        if (mIsLoading) {
                            perDayStickyAdapter.updateData(embeddedYonaActivity.getDayActivityList());
                        } else {
                            perDayStickyAdapter.notifyDataSetChange(embeddedYonaActivity.getDayActivityList());
                        }
                    }
                }
                mIsLoading = false;
            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
            }
        });
    }

}
