/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.api.manager.ChallengesManager;
import nu.yona.app.api.manager.impl.ChallengesManagerImpl;
import nu.yona.app.api.manager.impl.GoalManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.CustomTimePickerDialog;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public class ChallengesGoalDetailFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mHGoalTypeImg;
    private YonaFontTextView mHTxtGoalTitle, mHTxtGoalSubscribe, mFTxtGoalTitle, mFTxtGoalSubscribe, mBudgetGoalTime;
    private View budgetGoalView, timezoneGoalView;
    private ImageView rightIcon;
    private YonaActivity activity;
    private Object mYonaGoal;
    private GoalsEnum currentGoalType;
    private ChallengesManager challengesManager;
    private YonaFontButton btnChallenges;
    private String first_time;
    private String second_time;
    private String currentTab;
    private CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {

        @Override
        public void setTime(String time) {
            setFirst_time(time);
            mBudgetGoalTime.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(getTimeInMilliseconds(getFirst_time()))));
        }
    };

    public String getSecond_time() {
        return second_time;
    }

    public void setSecond_time(String second_time) {
        this.second_time = second_time;
    }

    public String getFirst_time() {
        return first_time;
    }

    public void setFirst_time(String first_time) {
        this.first_time = first_time;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYonaGoal = getArguments().getSerializable(AppConstant.GOAL_OBJECT);
            currentTab = getArguments().getString(AppConstant.NEW_GOAL_TYPE);
        }
        activity = (YonaActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_detail_layout, null);
        challengesManager = new ChallengesManagerImpl(getActivity());
        mHGoalTypeImg = (ImageView) view.findViewById(R.id.img_bucket);
        mHTxtGoalTitle = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_title);
        mHTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_subscribeTxt);
        mFTxtGoalTitle = (YonaFontTextView) view.findViewById(R.id.challenges_goal_footer_title);
        mFTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.challenges_goal_footer_subscribeTxt);
        timezoneGoalView = view.findViewById(R.id.timezoneView);
        budgetGoalView = view.findViewById(R.id.goal_item_layout);
        rightIcon = (ImageView) activity.findViewById(R.id.rightIcon);

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteGoal();
            }
        });
        btnChallenges = (YonaFontButton) view.findViewById(R.id.btnChallenges);
        btnChallenges.setOnClickListener(this);

        if (mYonaGoal != null) {
            if (mYonaGoal instanceof YonaGoal) {
                btnChallenges.setEnabled(false);
                YonaGoal yonaGoal = (YonaGoal) mYonaGoal;

                if (yonaGoal.getLinks().getEdit() != null && !TextUtils.isEmpty(yonaGoal.getLinks().getEdit().getHref())) {
                    rightIcon.setVisibility(View.VISIBLE);
                } else {
                    rightIcon.setVisibility(View.GONE);
                }
                mHTxtGoalTitle.setText(yonaGoal.getActivityCategoryName());
                if (challengesManager.typeOfGoal(yonaGoal).equals(GoalsEnum.BUDGET_GOAL)) {
                    setBudgetGoalViewVisibility();
                    mBudgetGoalTime = (YonaFontTextView) view.findViewById(R.id.goal_minutes_num);
                    mHTxtGoalSubscribe.setText(getString(R.string.budgetgoalheadersubtext, yonaGoal.getActivityCategoryName()));
                    mBudgetGoalTime.setText(String.valueOf(yonaGoal.getMaxDurationMinutes()));
                    (view.findViewById(R.id.goal_item_layout)).setOnClickListener(this);

                } else if (challengesManager.typeOfGoal(yonaGoal).equals(GoalsEnum.TIME_ZONE_GOAL)) {
                    setTimezoneGoalViewVisibility();
                    mHTxtGoalSubscribe.setText(getString(R.string.timezonegoalheadersubtext, yonaGoal.getActivityCategoryName()));
                    populateTimeZoneGoalView(view, yonaGoal);
                    ((YonaFontTextView) view.findViewById(R.id.txt_header_text)).setText(getString(R.string.timezone));
                } else {
                    //todo- for nogo
                    mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_nogo);
                }
            } else if (mYonaGoal instanceof YonaActivityCategories) {
                YonaActivityCategories yonaActivityCategories = (YonaActivityCategories) mYonaGoal;
                mHTxtGoalTitle.setText(yonaActivityCategories.getName());
                if (currentTab.equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString())) {
                    setBudgetGoalViewVisibility();
                    mBudgetGoalTime = (YonaFontTextView) view.findViewById(R.id.goal_minutes_num);
                    mHTxtGoalSubscribe.setText(getString(R.string.budgetgoalheadersubtext, yonaActivityCategories.getName()));
                    (view.findViewById(R.id.goal_item_layout)).setOnClickListener(this);
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
                    setTimezoneGoalViewVisibility();
                    mHTxtGoalSubscribe.setText(getString(R.string.timezonegoalheadersubtext, yonaActivityCategories.getName()));
                    ((YonaFontTextView) view.findViewById(R.id.txt_header_text)).setText(getString(R.string.timezone));
                }
            }
        }
        return view;
    }

    private void doDeleteGoal() {
        activity.showLoadingView(true, null);
        new GoalManagerImpl(getActivity()).deleteGoal((YonaGoal) mYonaGoal, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                activity.showLoadingView(false, null);
                goBackToScreen();
            }

            @Override
            public void onError(Object errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void showError(Object errorMessage) {
        ErrorMessage message = (ErrorMessage) errorMessage;
        activity.showLoadingView(false, null);
        CustomAlertDialog.show(activity, message.getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void setBudgetGoalViewVisibility() {
        currentGoalType = GoalsEnum.BUDGET_GOAL;
        timezoneGoalView.setVisibility(View.GONE);
        budgetGoalView.setVisibility(View.VISIBLE);
        mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_timezone);
    }

    private void setTimezoneGoalViewVisibility() {
        currentGoalType = GoalsEnum.TIME_ZONE_GOAL;
        timezoneGoalView.setVisibility(View.VISIBLE);
        budgetGoalView.setVisibility(View.GONE);
        mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_timebucket);
    }

    private void populateTimeZoneGoalView(View view, YonaGoal tYonaGoal) {
        RecyclerView timeZoneGoalView = (RecyclerView) view.findViewById(R.id.listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        timeZoneGoalView.setLayoutManager(layoutManager);
        TimeZoneGoalsAdapter timeZoneGoalsAdapter = new TimeZoneGoalsAdapter(tYonaGoal.getZones(), null);
        timeZoneGoalView.setAdapter(timeZoneGoalsAdapter);
    }

    private void createNewBudgetGoal(long minutes, Object object) {
        if (object instanceof YonaGoal) {
            challengesManager.postBudgetGoals(minutes, ((YonaGoal) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result != null) {
                        goBackToScreen();
                    }
                }

                @Override
                public void onError(Object errorMessage) {

                }
            });
        } else if (object instanceof YonaActivityCategories) {
            challengesManager.postBudgetGoals(minutes, ((YonaActivityCategories) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result != null) {
                        goBackToScreen();
                    }
                }

                @Override
                public void onError(Object errorMessage) {

                }
            });
        }
    }

    private void goBackToScreen() {
        ((YonaActivity) getActivity()).onBackPressed();
    }

    private void showTimePicker() {
        CustomTimePickerDialog fragmentTime = new CustomTimePickerDialog();
        fragmentTime.setOnTimeSetListener(timeSetListener);
        fragmentTime.setTimePickerInterval(1);
        fragmentTime.setPastTimeSelectionAllow(true);
        Bundle args = new Bundle();
        fragmentTime.setArguments(args);
        fragmentTime.show(getFragmentManager(), "dialog");
    }

    private long getTimeInMilliseconds(String time) {
        if (!TextUtils.isEmpty(time)) {
            String[] min = time.split(":");
            long minutes = TimeUnit.MINUTES.toMillis(Integer.parseInt(min[1]));
            long hr = TimeUnit.HOURS.toMillis(Integer.parseInt(min[0]));
            return (minutes + hr);
        } else {
            return 0;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChallenges:
                if (mYonaGoal instanceof YonaGoal) {
                    createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), (YonaGoal) mYonaGoal);
                } else if (mYonaGoal instanceof YonaActivityCategories) {
                    createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), (YonaActivityCategories) mYonaGoal);
                }
                break;
            case R.id.goal_item_layout:
                showTimePicker();
            default:
                break;
        }
    }
}
