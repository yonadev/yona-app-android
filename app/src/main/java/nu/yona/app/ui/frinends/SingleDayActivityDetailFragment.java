/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.dashboard.CustomPageAdapter;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class SingleDayActivityDetailFragment extends BaseFragment implements EventChangeListener {

    private CustomPageAdapter customPageAdapter;
    private ViewPager viewPager;
    private DayActivity activity;
    private View view;
    private ImageView previousItem, nextItem;
    private YonaFontTextView dateTitle;
    private YonaHeaderTheme mYonaHeaderTheme;
    private List<DayActivity> dayActivityList;
    private String yonaDayDetailUrl;
    private YonaBuddy yonaBuddy;
    private LinearLayout commentBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) != null) {
                if (getArguments().get(AppConstant.YONA_BUDDY_OBJ) instanceof YonaBuddy) {
                    yonaBuddy = (YonaBuddy) getArguments().get(AppConstant.YONA_BUDDY_OBJ);
                } else {
                    yonaBuddy = APIManager.getInstance().getActivityManager().findYonaBuddy((Href) getArguments().get(AppConstant.YONA_BUDDY_OBJ));
                }
            }
            if (getArguments().getSerializable(AppConstant.YONA_THEME_OBJ) != null) {
                mYonaHeaderTheme = (YonaHeaderTheme) getArguments().getSerializable(AppConstant.YONA_THEME_OBJ);
            }
            if (getArguments().get(AppConstant.YONA_DAY_DEATIL_URL) != null) {
                yonaDayDetailUrl = (String) getArguments().get(AppConstant.YONA_DAY_DEATIL_URL);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_pager_fragment, null);

        setupToolbar(view);
        if (mYonaHeaderTheme != null) {
            mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
        }

        dayActivityList = new ArrayList<>();
        previousItem = (ImageView) view.findViewById(R.id.previous);
        nextItem = (ImageView) view.findViewById(R.id.next);
        dateTitle = (YonaFontTextView) view.findViewById(R.id.date);
        commentBox = (LinearLayout) view.findViewById(R.id.comment_box);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        customPageAdapter = new CustomPageAdapter(getActivity());
        viewPager.setAdapter(customPageAdapter);
        previousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDayActivity();
            }
        });
        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDayActivity();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                updateFlow(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!TextUtils.isEmpty(yonaDayDetailUrl)) {
            setDayActivityDetails();
        }
        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void previousDayActivity() {
        if (activity != null) {
            loadDayActivity(activity.getLinks().getPrev().getHref());
        }
    }

    private void nextDayActivity() {
        if (activity != null) {
            loadDayActivity(activity.getLinks().getNext().getHref());
        }
    }

    private void setDayActivityDetails() {
        loadDayActivity(yonaDayDetailUrl);
        setDayDetailTitleAndIcon();
    }

    private void setDayDetailTitleAndIcon() {
        if (mYonaHeaderTheme.isBuddyFlow() && yonaBuddy != null) {
            leftIcon.setVisibility(View.GONE);
            rightIcon.setVisibility(View.GONE);
            rightIconProfile.setVisibility(View.VISIBLE);
            if (yonaBuddy.getEmbedded() != null && yonaBuddy.getEmbedded().getYonaUser() != null && !TextUtils.isEmpty(yonaBuddy.getEmbedded().getYonaUser().getFirstName())) {
                rightIconProfile.setImageDrawable(TextDrawable.builder()
                        .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                        .buildRound(yonaBuddy.getEmbedded().getYonaUser().getFirstName().substring(0, 1).toUpperCase(),
                                ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
            }
            profileClickEvent(rightIconProfile);

        } else {
            leftIcon.setVisibility(View.GONE);
            rightIcon.setVisibility(View.GONE);
            if (mYonaHeaderTheme.isBuddyFlow()) {
                rightIconProfile.setVisibility(View.VISIBLE);
                rightIconProfile.setImageDrawable(TextDrawable.builder()
                        .beginConfig().withBorder(AppConstant.PROFILE_ICON_BORDER_SIZE).endConfig()
                        .buildRound(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName().substring(0, 1).toUpperCase(),
                                ContextCompat.getColor(YonaActivity.getActivity(), R.color.mid_blue)));
                profileClickEvent(rightIconProfile);
            }
        }
        toolbarTitle.setText(mYonaHeaderTheme.getHeader_title());
    }

    private void profileClickEvent(View profileView) {
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentEnum.ACTION_PROFILE.getActionString());
                intent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
                if (yonaBuddy != null) {
                    intent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
                } else {
                    intent.putExtra(AppConstant.YONA_THEME_OBJ, new YonaHeaderTheme(false, null, null, 0, R.drawable.icn_reminder, getString(R.string.dashboard), R.color.grape, R.drawable.triangle_shadow_grape));
                    intent.putExtra(AppConstant.USER, YonaApplication.getEventChangeManager().getDataState().getUser());
                }
                YonaActivity.getActivity().replaceFragment(intent);
            }
        });
    }

    private void loadDayActivity(String url) {
        if (url == null) {
            return;
        }
        YonaActivity.getActivity().showLoadingView(true, null);

        if (getLocalDayActivity(url) != null) {
            activity = getLocalDayActivity(url);
            updateDayActivityData(activity);
            YonaActivity.getActivity().showLoadingView(false, null);
        } else {
            APIManager.getInstance().getActivityManager().getDayDetailActivity(url, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof DayActivity) {
                        dayActivityList.add((DayActivity) result);
                        activity = (DayActivity) result;
                        updateDayActivityData(activity);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    YonaActivity.getActivity().showLoadingView(false, null);
                }
            });
        }
    }

    private DayActivity getLocalDayActivity(String url) {
        for (DayActivity dayActivity : dayActivityList) {
            if (dayActivity != null && dayActivity.getLinks() != null && dayActivity.getLinks().getSelf() != null && !TextUtils.isEmpty(dayActivity.getLinks().getSelf().getHref()) && url.equalsIgnoreCase(dayActivity.getLinks().getSelf().getHref())) {
                return dayActivity;
            }
        }
        return null;
    }

    private void updateDayActivityData(DayActivity dayActivity) {
        customPageAdapter.notifyDataSetChanged(dayActivityList);
        fetchComments(dayActivityList.indexOf(activity));
        viewPager.setCurrentItem(dayActivityList.indexOf(dayActivity));
        updateFlow(dayActivityList.indexOf(dayActivity));
        YonaActivity.getActivity().showLoadingView(false, null);
    }

    private void updateFlow(int position) {
        if (dayActivityList != null && dayActivityList.size() > 0) {
            dateTitle.setText(dayActivityList.get(position).getStickyTitle());
        }

        if ((activity != null && activity.getLinks() != null && activity.getLinks().getPrev() != null && !TextUtils.isEmpty(activity.getLinks().getPrev().getHref()))) {
            previousItem.setVisibility(View.VISIBLE);
        } else {
            previousItem.setVisibility(View.INVISIBLE);
        }
        if ((activity != null && activity.getLinks() != null && activity.getLinks().getNext() != null && !TextUtils.isEmpty(activity.getLinks().getNext().getHref()))) {
            nextItem.setVisibility(View.VISIBLE);
        } else {
            nextItem.setVisibility(View.INVISIBLE);
        }

    }

    private void fetchComments(int position) {
        APIManager.getInstance().getActivityManager().getComments(dayActivityList, position, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                if (result instanceof List<?>) {
                    dayActivityList = (List<DayActivity>) result;
                    customPageAdapter.notifyDataSetChanged(dayActivityList);
                }
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    YonaActivity.getActivity().showError((ErrorMessage) errorMessage);
                } else {
                    YonaActivity.getActivity().showError(new ErrorMessage(getString(R.string.no_data_found)));
                }
            }
        });
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_SHOW_CHAT_OPTION:
                commentBox.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}