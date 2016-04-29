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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
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
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public class ChallengesGoalDetailFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mHGoalTypeImg;
    private YonaFontTextView mBudgetGoalTime;
    private View budgetGoalView, timezoneGoalView;
    private YonaActivity activity;
    private Object mYonaGoal;
    private ChallengesManager challengesManager;
    private String currentTab;
    private List<String> listOfTimes;
    private TimeZoneGoalsAdapter timeZoneGoalsAdapter;
    /**
     * Use this listener only for budget time picker
     */
    private final CustomTimePickerDialog.OnTimeSetListener budgetTimeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {

        @Override
        public void setTime(String time) {
            mBudgetGoalTime.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(getTimeInMilliseconds(time))));
        }
    };
    /**
     * Use this listener only for Time zone picker
     */
    private final CustomTimePickerDialog.OnTimeSetListener timeZoneSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
        @Override
        public void setTime(String time) {
            if (time.contains("-")) {
                String[] str = time.split("-");
                if (str.length > 0) {
                    listOfTimes.add(time);
                    if (timeZoneGoalsAdapter != null) {
                        timeZoneGoalsAdapter.timeZoneNotifyDataSetChanged(listOfTimes);
                    }
                }
            }
        }
    };
    private final OnItemClickListener timeZoneGoalClickListener = new OnItemClickListener() {

        @Override
        public void onDelete(View v) {
            final Bundle timebundle = (Bundle) v.getTag();
            final int position = timebundle.getInt(AppConstant.POSITION);
            if (timeZoneGoalsAdapter != null) {
                timeZoneGoalsAdapter.removeItemFromList(position);
            }
        }

        @Override
        public void onClickStartTime(View v) {
            callTimePickerForTimeZone(v, true);

        }

        @Override
        public void onClickEndTime(View v) {
            callTimePickerForTimeZone(v, false);

        }
    };

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
        YonaFontTextView mHTxtGoalTitle = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_title);
        YonaFontTextView mHTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_subscribeTxt);
        YonaFontTextView mFTxtGoalTitle = (YonaFontTextView) view.findViewById(R.id.challenges_goal_footer_title);
        YonaFontTextView mFTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.challenges_goal_footer_subscribeTxt);
        timezoneGoalView = view.findViewById(R.id.timezoneView);
        budgetGoalView = view.findViewById(R.id.goal_item_layout);
        activity.getRightIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDeleteGoal();
            }
        });
        YonaFontButton btnChallenges = (YonaFontButton) view.findViewById(R.id.btnChallenges);
        btnChallenges.setOnClickListener(this);

        RecyclerView timeZoneGoalView = (RecyclerView) view.findViewById(R.id.listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        timeZoneGoalView.setLayoutManager(layoutManager);
        listOfTimes = new ArrayList<>();
        timeZoneGoalsAdapter = new TimeZoneGoalsAdapter(listOfTimes, timeZoneGoalClickListener);
        timeZoneGoalView.setAdapter(timeZoneGoalsAdapter);

        if (mYonaGoal != null) {
            if (mYonaGoal instanceof YonaGoal) {
                btnChallenges.setEnabled(false);
                YonaGoal yonaGoal = (YonaGoal) mYonaGoal;

                if (yonaGoal.getLinks().getEdit() != null && !TextUtils.isEmpty(yonaGoal.getLinks().getEdit().getHref())) {
                    activity.getRightIcon().setVisibility(View.VISIBLE);
                } else {
                    activity.getRightIcon().setVisibility(View.GONE);
                }
                mHTxtGoalTitle.setText(yonaGoal.getActivityCategoryName());
                if (challengesManager.typeOfGoal(yonaGoal).equals(GoalsEnum.BUDGET_GOAL)) {
                    setBudgetGoalViewVisibility();
                    mBudgetGoalTime = (YonaFontTextView) view.findViewById(R.id.goal_minutes_num);
                    mHTxtGoalSubscribe.setText(getString(R.string.budgetgoalheadersubtext, yonaGoal.getActivityCategoryName()));
                    mBudgetGoalTime.setText(String.valueOf(yonaGoal.getMaxDurationMinutes()));
                    view.findViewById(R.id.goal_item_layout).setOnClickListener(this);

                } else if (challengesManager.typeOfGoal(yonaGoal).equals(GoalsEnum.TIME_ZONE_GOAL)) {
                    setTimezoneGoalViewVisibility();
                    mHTxtGoalSubscribe.setText(getString(R.string.timezonegoalheadersubtext, yonaGoal.getActivityCategoryName()));
                    listOfTimes.addAll(yonaGoal.getZones());
                    ((YonaFontTextView) view.findViewById(R.id.txt_header_text)).setText(getString(R.string.timezone));
                    view.findViewById(R.id.img_add_goal).setOnClickListener(this);
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
                    mBudgetGoalTime.setText(String.valueOf(AppConstant.MIN_DEFAULT_TIME));
                    mHTxtGoalSubscribe.setText(getString(R.string.budgetgoalheadersubtext, yonaActivityCategories.getName()));
                    (view.findViewById(R.id.goal_item_layout)).setOnClickListener(this);
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
                    setTimezoneGoalViewVisibility();
                    mHTxtGoalSubscribe.setText(getString(R.string.timezonegoalheadersubtext, yonaActivityCategories.getName()));
                    ((YonaFontTextView) view.findViewById(R.id.txt_header_text)).setText(getString(R.string.timezone));
                    ((ImageView) view.findViewById(R.id.img_add_goal)).setOnClickListener(this);
                }
            }
        }
        return view;
    }

    /**
     * Delete a goal which aleady added on server
     */
    private void doDeleteGoal() {
        activity.showLoadingView(true, null);
        new GoalManagerImpl(getActivity()).deleteGoal((YonaGoal) mYonaGoal, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
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
        timezoneGoalView.setVisibility(View.GONE);
        budgetGoalView.setVisibility(View.VISIBLE);
        mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_timezone);
    }

    private void setTimezoneGoalViewVisibility() {
        timezoneGoalView.setVisibility(View.VISIBLE);
        budgetGoalView.setVisibility(View.GONE);
        mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_timebucket);
    }

    /**
     * Create or post new budget Goals
     *
     * @param minutes
     * @param object
     */
    private void createNewBudgetGoal(long minutes, Object object) {
        activity.showLoadingView(true, null);
        if (object instanceof YonaGoal) {
            challengesManager.postBudgetGoals(minutes, ((YonaGoal) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    activity.showLoadingView(false, null);
                    if (result != null) {
                        goBackToScreen();
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        } else if (object instanceof YonaActivityCategories) {
            challengesManager.postBudgetGoals(minutes, ((YonaActivityCategories) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    activity.showLoadingView(false, null);
                    if (result != null) {
                        goBackToScreen();
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    /**
     * @param timesList
     * @param object
     */
    private void createTimeZoneGoal(List<String> timesList, Object object) {
        activity.showLoadingView(true, null);
        if (object instanceof YonaGoal) {
            challengesManager.postTimeGoals(timesList, (YonaGoal) object, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    activity.showLoadingView(false, null);
                    if (result != null) {
                        goBackToScreen();
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        } else if (object instanceof YonaActivityCategories) {
            challengesManager.postTimeGoals(timesList, (YonaActivityCategories) object, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    activity.showLoadingView(false, null);
                    if (result != null) {
                        goBackToScreen();
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }

    }

    private void goBackToScreen() {
        getActivity().onBackPressed();
    }

    private void showTimePicker(boolean allowDualSelection, int interval, long maxTime, long minTime, CustomTimePickerDialog.OnTimeSetListener listener) {
        CustomTimePickerDialog fragmentTime = new CustomTimePickerDialog();
        fragmentTime.setOnTimeSetListener(listener);
        fragmentTime.setMinTime(minTime);
        fragmentTime.setMaxTime(maxTime);
        fragmentTime.setTimePickerInterval(interval);
        fragmentTime.setPastTimeSelectionAllow(false);
        fragmentTime.setIsNextAllow(allowDualSelection);
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
                if (currentTab.equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString()) && !TextUtils.isEmpty(mBudgetGoalTime.getText())) {
                    if (mYonaGoal instanceof YonaGoal) {
                        createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), (YonaGoal) mYonaGoal);
                    } else if (mYonaGoal instanceof YonaActivityCategories) {
                        createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), (YonaActivityCategories) mYonaGoal);
                    }
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
                    if (mYonaGoal instanceof YonaGoal) {
                        createTimeZoneGoal(listOfTimes, (YonaGoal) mYonaGoal);
                    } else if (mYonaGoal instanceof YonaActivityCategories) {
                        createTimeZoneGoal(listOfTimes, (YonaActivityCategories) mYonaGoal);
                    }
                }
                break;
            case R.id.goal_item_layout:
                showTimePicker(false, AppConstant.TIME_INTERVAL_ONE, 0, TimeUnit.MINUTES.toMillis(Integer.parseInt(mBudgetGoalTime.getText().toString())), budgetTimeSetListener);
                break;
            case R.id.img_add_goal:
                showTimePicker(true, AppConstant.TIME_INTERVAL_FIFTEEN, 0, 0, timeZoneSetListener);
                break;

            default:
                break;
        }
    }

    private void callTimePickerForTimeZone(final View v, boolean updatingStartTime) {
        final Bundle bTime = (Bundle) v.getTag();
        final String updatedTime = bTime.getString(AppConstant.TIME);
        final int position = bTime.getInt(AppConstant.POSITION);
        if (updatingStartTime) {
            showTimePicker(false, AppConstant.TIME_INTERVAL_FIFTEEN, 0, getTimeInMilliseconds(updatedTime), new CustomTimePickerDialog.OnTimeSetListener() {
                @Override
                public void setTime(String time) {
                    if (timeZoneGoalsAdapter != null) {
                        timeZoneGoalsAdapter.updateListItem(position, time, true);
                    }
                }
            });
        } else {
            showTimePicker(false, AppConstant.TIME_INTERVAL_FIFTEEN, 0, getTimeInMilliseconds(updatedTime), new CustomTimePickerDialog.OnTimeSetListener() {
                @Override
                public void setTime(String time) {
                    if (timeZoneGoalsAdapter != null) {
                        timeZoneGoalsAdapter.updateListItem(position, time, false);
                    }
                }
            });
        }
    }


}
