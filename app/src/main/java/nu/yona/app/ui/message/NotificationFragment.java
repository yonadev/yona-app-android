/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.enums.StatusEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.recyclerViewDecor.DividerDecoration;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.frinends.OnFriendsItemClickListener;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class NotificationFragment extends BaseFragment {

    private static final int PAGE_SIZE = 20;
    private RecyclerView mMessageRecyclerView;
    private MessageStickyRecyclerAdapter mMessageStickyRecyclerAdapter;
    private YonaMessages mYonaMessages;
    private int currentPage = 0;
    private boolean mIsLoading = false;
    private LinearLayoutManager mLayoutManager;

    /**
     * Click listener for item click and delete click of recycler view's item
     */
    private OnFriendsItemClickListener onFriendsItemClickListener = new OnFriendsItemClickListener() {
        @Override
        public void onFriendsItemClick(View view) {
            if (view.getTag() instanceof YonaMessage) {
                YonaMessage yonaMessage = (YonaMessage) view.getTag();
                Intent mMessageIntent = null;
                if (yonaMessage.getLinks() != null && yonaMessage.getLinks().getEdit() != null && yonaMessage.getNotificationMessageEnum().getStatusEnum() == StatusEnum.ACCEPTED) {
                    mMessageIntent = new Intent(IntentEnum.ACTION_FRIEND_PROFILE.getActionString());
                    YonaHeaderTheme yonaHeaderTheme = new YonaHeaderTheme(false, null, null, 0, 0, null, R.color.mid_blue_two, R.drawable.triangle_shadow_blue);
                    mMessageIntent.putExtra(AppConstant.YONA_THEME_OBJ, yonaHeaderTheme);
                    mMessageIntent.putExtra(AppConstant.YONAMESSAGE_OBJ, yonaMessage);
                    mMessageIntent.putExtra(AppConstant.SECOND_COLOR_CODE, R.color.grape);
                } else if (yonaMessage.getNotificationMessageEnum().getStatusEnum() == StatusEnum.REQUESTED) {
                    mMessageIntent = new Intent(IntentEnum.ACTION_FRIEND_REQUEST.getActionString());
                    mMessageIntent.putExtra(AppConstant.YONAMESSAGE_OBJ, yonaMessage);
                }
                YonaActivity.getActivity().replaceFragment(mMessageIntent);
            }
        }

        @Override
        public void onFriendsItemDeleteClick(View view) {
            if (view.getTag() instanceof YonaMessage) {
                YonaMessage yonaMessage = (YonaMessage) view.getTag();
                if (yonaMessage != null && yonaMessage.getLinks() != null && yonaMessage.getLinks().getEdit() != null && !TextUtils.isEmpty(yonaMessage.getLinks().getEdit().getHref())) {
                    YonaActivity.getActivity().showLoadingView(true, null);
                    APIManager.getInstance().getNotificationManager().deleteMessage(yonaMessage.getLinks().getEdit().getHref(), 0, 0, new DataLoadListener() {
                        @Override
                        public void onDataLoad(Object result) {
                            YonaActivity.getActivity().showLoadingView(false, null);
                            refreshAdapter();
                        }

                        @Override
                        public void onError(Object errorMessage) {
                            YonaActivity.getActivity().showLoadingView(false, null);
                            YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
                        }
                    });
                }
            }
        }
    };

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

                if (!mIsLoading && currentPage < mYonaMessages.getPage().getTotalPages()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        loadMoreItems();
                    }
                }
            }
        }
    };

    /**
     * load more items
     */
    private void loadMoreItems() {
        mIsLoading = true;
        currentPage += 1;
        getUserMessages();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_layout, null);

        setupToolbar(view);

        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mLayoutManager = new LinearLayoutManager(YonaActivity.getActivity());
        mMessageRecyclerView.setLayoutManager(mLayoutManager);
        mMessageStickyRecyclerAdapter = new MessageStickyRecyclerAdapter(new ArrayList<YonaMessage>(), YonaActivity.getActivity(), onFriendsItemClickListener);
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

    /**
     * update toolbar's Item
     */
    private void setTitleAndIcon() {
        leftIcon.setVisibility(View.GONE);
        toolbarTitle.setText(getString(R.string.message));
        rightIcon.setVisibility(View.GONE);
    }

    /**
     * Refresh recyclerview's adapter
     */
    private void refreshAdapter() {
        mMessageStickyRecyclerAdapter.clear();
        currentPage = 0;
        getUserMessages();
        YonaApplication.getEventChangeManager().getDataState().setEmbeddedWithBuddyActivity(null);
    }

    /**
     * to get the list of user's messages
     */
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

    /**
     * update RecyclerView item header for grouping section
     *
     * @param headerDecor
     */
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
