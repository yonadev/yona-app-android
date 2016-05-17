/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.os.Bundle;
import android.os.Handler;
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
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.recyclerViewDecor.DividerDecoration;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class MessageFragment extends BaseFragment {

    private static final int PAGE_SIZE = 20;
    private List<YonaMessage> listYonaMsgs;
    private RecyclerView mMessageRecyclerView;
    private MessageStickyRecyclerAdapter mMessageStickyRecyclerAdapter;
    private YonaMessages mYonaMessages;
    private int currentPage = 0;
    private boolean mIsLoading = false;
    private LinearLayoutManager mLayoutManager;

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

                if (!mIsLoading && currentPage < mYonaMessages.getPage().getTotalPages()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        loadMoreItems();
                    }
                }
            }
        }
    };

    private void loadMoreItems() {
        mIsLoading = true;
        currentPage += 1;
        getUserMessages();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout, null);
        listYonaMsgs = new ArrayList<>();
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
        mMessageRecyclerView.setLayoutManager(mLayoutManager);
        mMessageStickyRecyclerAdapter = new MessageStickyRecyclerAdapter(listYonaMsgs, YonaActivity.getActivity());
        //mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(YonaActivity.getActivity()));
        mMessageRecyclerView.setAdapter(mMessageStickyRecyclerAdapter);
        mMessageRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
        setRecyclerHeaderAdapterUpdate(new StickyRecyclerHeadersDecoration(mMessageStickyRecyclerAdapter));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        refreshAdapter();
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YonaActivity.getActivity().getLeftIcon().setVisibility(View.GONE);
                YonaActivity.getActivity().updateTitle(R.string.message);
                YonaActivity.getActivity().getRightIcon().setVisibility(View.GONE);
            }
        }, AppConstant.TIMER_DELAY_THREE_HUNDRED);
    }

    private void refreshAdapter() {
        mMessageStickyRecyclerAdapter.clear();
        currentPage = 0;
        getUserMessages();
    }

    private void getUserMessages() {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getNotificationManager().getMessage(PAGE_SIZE, currentPage, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                if (isAdded() && result != null && result instanceof YonaMessages) {
                    mYonaMessages = (YonaMessages) result;
                    if (mYonaMessages.getEmbedded() != null && mYonaMessages.getEmbedded().getYonaMessages() != null) {
                        if (mIsLoading) {
                            mMessageStickyRecyclerAdapter.updateData(mYonaMessages.getEmbedded().getYonaMessages());
                        } else {
                            mMessageStickyRecyclerAdapter.notifyDataSetChange(mYonaMessages.getEmbedded().getYonaMessages());
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

    private void setRecyclerHeaderAdapterUpdate(StickyRecyclerHeadersDecoration headerDecor) {
        mMessageRecyclerView.addItemDecoration(headerDecor);

        // Add decoration for dividers between list items
        mMessageRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        // Add touch listeners
        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(mMessageRecyclerView, headerDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                    }
                });
    }
}
