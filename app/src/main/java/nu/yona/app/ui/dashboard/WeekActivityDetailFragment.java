/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.dashboard;

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
import nu.yona.app.api.model.Day;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaHeaderTheme;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 13/06/16.
 */
public class WeekActivityDetailFragment extends BaseFragment implements EventChangeListener {

    private CustomPageAdapter customPageAdapter;
    private ViewPager viewPager;
    private WeekActivity activity;
    private View view;
    private ImageView previousItem, nextItem;
    private YonaFontTextView dateTitle;
    private List<WeekActivity> weekActivityList;
    private YonaHeaderTheme mYonaHeaderTheme;
    private YonaBuddy yonaBuddy;
    private LinearLayout commentBox;
    private YonaFontEditTextView messageTxt;

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.circle_view:
                    Day day = (Day) v.getTag(R.integer.day_key);
                    if (day != null) {
                        openDetailPage(day);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void openDetailPage(Day activity) {
        Intent weekDayIntent = new Intent(IntentEnum.ACTION_SINGLE_ACTIVITY_DETAIL_VIEW.getActionString());
        weekDayIntent.putExtra(AppConstant.YONA_DAY_DEATIL_URL, activity.getLinks().getYonaDayDetails().getHref());
        weekDayIntent.putExtra(AppConstant.YONA_BUDDY_OBJ, yonaBuddy);
        weekDayIntent.putExtra(AppConstant.YONA_THEME_OBJ, mYonaHeaderTheme);
        YonaActivity.getActivity().replaceFragment(weekDayIntent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_pager_fragment, null);

        setupToolbar(view);
        if (mYonaHeaderTheme != null) {
            mToolBar.setBackgroundResource(mYonaHeaderTheme.getToolbar());
        }

        previousItem = (ImageView) view.findViewById(R.id.previous);
        nextItem = (ImageView) view.findViewById(R.id.next);
        dateTitle = (YonaFontTextView) view.findViewById(R.id.date);
        commentBox = (LinearLayout) view.findViewById(R.id.comment_box);
        messageTxt = (YonaFontEditTextView) view.findViewById(R.id.userMessage);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        customPageAdapter = new CustomPageAdapter(getActivity(), itemClickListener);
        viewPager.setAdapter(customPageAdapter);
        if (getArguments() != null) {
            if (getArguments().get(AppConstant.OBJECT) != null) {
                activity = (WeekActivity) getArguments().get(AppConstant.OBJECT);
            }
        }
        previousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });
        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != weekActivityList.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });

        view.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(messageTxt.getText())) {
                    addComment(messageTxt.getText().toString());
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fetchComments(position);
                updateFlow(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        weekActivityList = new ArrayList<>();
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
        if (embeddedYonaActivity != null && embeddedYonaActivity.getWeekActivityList() != null && embeddedYonaActivity.getWeekActivityList().size() > 0) {

            for (int i = embeddedYonaActivity.getWeekActivityList().size() - 1; i >= 0; i--) {
                try {
                    if (embeddedYonaActivity.getWeekActivityList().get(i).getYonaGoal().getLinks().getSelf().getHref().equals(activity.getLinks().getYonaGoal().getHref())) {
                        weekActivityList.add(embeddedYonaActivity.getWeekActivityList().get(i));
                    }
                } catch (Exception e) {
                    AppUtils.throwException(WeekActivityDetailFragment.class.getSimpleName(), e, Thread.currentThread(), null);
                }
            }
            customPageAdapter.notifyDataSetChanged(weekActivityList);
            fetchComments(weekActivityList.indexOf(activity));
            viewPager.setCurrentItem(weekActivityList.indexOf(activity));
            updateFlow(weekActivityList.indexOf(activity));
        } else {
            YonaActivity.getActivity().onBackPressed();
        }
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
                                ContextCompat.getColor(YonaActivity.getActivity(), mYonaHeaderTheme.isBuddyFlow() ? R.color.mid_blue : R.color.grape)));
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
                intent.putExtras(getArguments());
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

    private void updateFlow(int position) {
        dateTitle.setText(weekActivityList.get(position).getStickyTitle());
        if (position == 0) {
            previousItem.setVisibility(View.INVISIBLE);
        } else {
            previousItem.setVisibility(View.VISIBLE);
        }
        if (position == weekActivityList.size() - 1) {
            nextItem.setVisibility(View.INVISIBLE);
        } else {
            nextItem.setVisibility(View.VISIBLE);
        }
    }

    private void fetchComments(int position) {
        APIManager.getInstance().getActivityManager().getCommentsForWeek(weekActivityList, position, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                if (result instanceof List<?>) {
                    weekActivityList = (List<WeekActivity>) result;
                    customPageAdapter.notifyDataSetChanged(weekActivityList);
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

    //TODO @Bhargav, when user click on send button from comment box, it will call this API.
    private void addComment(String message) {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getActivityManager().addComment(activity, message, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                //TODO response will be object of YonaMessage -> add in list of comments array and notify UI to update item in list.
            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                //TODO show proper message
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