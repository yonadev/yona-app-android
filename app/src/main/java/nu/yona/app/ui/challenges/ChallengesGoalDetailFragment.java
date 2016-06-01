/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.timepicker.time.TimePickerDialog;
import nu.yona.timepicker.time.Timepoint;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public class ChallengesGoalDetailFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mHGoalTypeImg;
    private YonaFontTextView mBudgetGoalTime;
    /**
     * Use this listener only for budget time picker
     */
    private final TimePickerDialog.OnTimeSelected budgetTimeSetListener = new TimePickerDialog.OnTimeSelected() {
        @Override
        public void setTime(Timepoint firstTime, Timepoint secondTime) {
            long time = getTimeInMilliseconds(firstTime.getHour() + ":" + firstTime.getMinute());
            mBudgetGoalTime.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(time)));
        }
    };
    private View budgetGoalView, timezoneGoalView;
    private Object mYonaGoal;
    private String currentTab;
    private List<String> listOfTimes;
    private TimeZoneGoalsAdapter timeZoneGoalsAdapter;
    /**
     * Use this listener only for Time zone picker
     */
    private final TimePickerDialog.OnTimeSelected timeZoneSetListener = new TimePickerDialog.OnTimeSelected() {
        @Override
        public void setTime(Timepoint firstTime, Timepoint secondTime) {
            if (firstTime != null && secondTime != null) {
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append(AppUtils.getTimeDigit(firstTime.getHour()))
                        .append(":")
                        .append(AppUtils.getTimeDigit(firstTime.getMinute())).append("-")
                        .append(AppUtils.getTimeDigit(secondTime.getHour()))
                        .append(":")
                        .append(AppUtils.getTimeDigit(secondTime.getMinute()));

                listOfTimes.add(strBuilder.toString());
                if (timeZoneGoalsAdapter != null) {
                    timeZoneGoalsAdapter.timeZoneNotifyDataSetChanged(listOfTimes);
                }
            }

        }
    };

    private RecyclerView timeZoneGoalView;
    private OnItemClickListener timeZoneGoalClickListener = new OnItemClickListener() {

        @Override
        public void onDelete(View v) {
            final Bundle timebundle = (Bundle) v.getTag();
            final int position = timebundle.getInt(AppConstant.POSITION);

            CustomAlertDialog.show(getActivity(), "", getString(R.string.challengedeletemsg), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (timeZoneGoalsAdapter != null) {
                        timeZoneGoalsAdapter.removeItemFromList(position);
                    }
                    dialog.dismiss();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYonaGoal = getArguments().getSerializable(AppConstant.GOAL_OBJECT);
            currentTab = getArguments().getString(AppConstant.NEW_GOAL_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_detail_layout, null);

        setupToolbar(view);

        updateTitle();

        mHGoalTypeImg = (ImageView) view.findViewById(R.id.img_bucket);
        YonaFontTextView mHTxtGoalTitle = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_title);
        YonaFontTextView mHTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.goal_challenge_type_subscribeTxt);
        YonaFontTextView mFTxtGoalSubscribe = (YonaFontTextView) view.findViewById(R.id.challenges_goal_footer_subscribeTxt);
        timezoneGoalView = view.findViewById(R.id.timezoneView);
        budgetGoalView = view.findViewById(R.id.goal_item_layout);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (YonaActivity.getActivity() != null) {
                    CustomAlertDialog.show(YonaActivity.getActivity(), "", getString(R.string.challengedeletemsg), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doDeleteGoal();
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
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

        YonaActivityCategories yonaActivityCategories = null;
        if (mYonaGoal instanceof YonaActivityCategories) {
            yonaActivityCategories = (YonaActivityCategories) mYonaGoal;
        } else {
            yonaActivityCategories = APIManager.getInstance().getChallengesManager().getSelectedGoalCategories(((YonaGoal) mYonaGoal).getActivityCategoryName());
        }

        if (yonaActivityCategories != null && yonaActivityCategories.getApplications() != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String activityName : yonaActivityCategories.getApplications()) {
                stringBuilder.append(activityName);
                stringBuilder.append(", ");
            }
            if (!TextUtils.isEmpty(stringBuilder.toString()) && stringBuilder.toString().trim().length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.toString().trim().length() - 1);
                mFTxtGoalSubscribe.setText(stringBuilder.toString());
            }
        }

        if (mYonaGoal != null) {
            if (mYonaGoal instanceof YonaGoal) {
                YonaGoal yonaGoal = (YonaGoal) mYonaGoal;

                if (yonaGoal.getLinks().getEdit() != null && !TextUtils.isEmpty(yonaGoal.getLinks().getEdit().getHref())) {
                    rightIcon.setVisibility(View.VISIBLE);
                    rightIcon.setImageDrawable(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.icn_trash));
                } else {
                    rightIcon.setVisibility(View.GONE);
                }
                mHTxtGoalTitle.setText(yonaGoal.getActivityCategoryName());
                if (APIManager.getInstance().getChallengesManager().typeOfGoal(yonaGoal).equals(GoalsEnum.BUDGET_GOAL)) {
                    setBudgetGoalViewVisibility();
                    mBudgetGoalTime = (YonaFontTextView) view.findViewById(R.id.goal_minutes_num);
                    mHTxtGoalSubscribe.setText(getString(R.string.budgetgoalheadersubtext, !TextUtils.isEmpty(yonaActivityCategories.getName()) ? yonaActivityCategories.getName() : ""));
                    mBudgetGoalTime.setText(String.valueOf(yonaGoal.getMaxDurationMinutes()));
                    view.findViewById(R.id.goal_item_layout).setOnClickListener(this);

                } else if (APIManager.getInstance().getChallengesManager().typeOfGoal(yonaGoal).equals(GoalsEnum.TIME_ZONE_GOAL)) {
                    setTimezoneGoalViewVisibility();
                    mHTxtGoalSubscribe.setText(getString(R.string.timezonegoalheadersubtext, !TextUtils.isEmpty(yonaActivityCategories.getName()) ? yonaActivityCategories.getName() : ""));
                    listOfTimes.addAll(yonaGoal.getZones());
                    ((YonaFontTextView) view.findViewById(R.id.txt_header_text)).setText(getString(R.string.timezone));
                    view.findViewById(R.id.img_add_goal).setOnClickListener(this);
                } else {
                    btnChallenges.setVisibility(View.GONE);
                    mHTxtGoalSubscribe.setText(getString(R.string.nogoheadersubtext, !TextUtils.isEmpty(yonaActivityCategories.getName()) ? yonaActivityCategories.getName() : ""));
                    mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_nogo);
                    view.findViewById(R.id.goalTypeView).setVisibility(View.GONE);
                }
            } else if (mYonaGoal instanceof YonaActivityCategories) {
                //YonaActivityCategories yonaActivityCategories = (YonaActivityCategories) mYonaGoal;
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
                    view.findViewById(R.id.img_add_goal).setOnClickListener(this);
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.NOGO.getActionString())) {
                    mHTxtGoalSubscribe.setText(getString(R.string.nogoheadersubtext, yonaActivityCategories.getName()));
                    mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_nogo);
                    view.findViewById(R.id.goalTypeView).setVisibility(View.GONE);
                }
            }
        }
        return view;
    }

    /**
     * Update toolbar Title
     */
    private void updateTitle() {
        String title = null;
        if (currentTab.equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString())) {
            title = getString(R.string.challengescredit);
        } else if (currentTab.equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
            title = getString(R.string.challengeszone);
        } else if (currentTab.equalsIgnoreCase(GoalsEnum.NOGO.getActionString())) {
            title = getString(R.string.challengesnogo);
        }
        if (!TextUtils.isEmpty(title)) {
            toolbarTitle.setText(getString(R.string.challengesdetail, title));
        }
    }

    /**
     * Delete a goal which aleady added on server
     */
    private void doDeleteGoal() {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getChallengesManager().deleteGoal((YonaGoal) mYonaGoal, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                updateGoalNotify(result);
            }

            @Override
            public void onError(Object errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void showError(Object errorMessage) {
        ErrorMessage message = (ErrorMessage) errorMessage;
        YonaActivity.getActivity().showLoadingView(false, null);
        Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Show Budget Goal View
     */
    private void setBudgetGoalViewVisibility() {
        timezoneGoalView.setVisibility(View.GONE);
        budgetGoalView.setVisibility(View.VISIBLE);
        mHGoalTypeImg.setImageResource(R.drawable.icn_challenge_timezone);
    }

    /**
     * Show Time Zone Goal view
     */
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
        YonaActivity.getActivity().showLoadingView(true, null);
        if (object instanceof YonaGoal) {
            APIManager.getInstance().getChallengesManager().postBudgetGoals(minutes, ((YonaGoal) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        } else if (object instanceof YonaActivityCategories) {
            APIManager.getInstance().getChallengesManager().postBudgetGoals(minutes, ((YonaActivityCategories) object), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    /**
     * Call back
     *
     * @param result
     */
    private void updateGoalNotify(final Object result) {
        YonaActivity.getActivity().showLoadingView(false, null);
        if (result != null) {
            goBackToScreen();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_GOALS, result);
                }
            }, AppConstant.TIMER_DELAY);
        }
    }

    /**
     * Call BudgetGoal API for update
     *
     * @param minutes
     * @param yonaGoal
     */
    private void updateBudgetGoal(long minutes, YonaGoal yonaGoal) {
        YonaActivity.getActivity().showLoadingView(true, null);
        if (yonaGoal != null) {
            APIManager.getInstance().getChallengesManager().updateBudgetGoals(minutes, yonaGoal, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    /**
     * Call TimeZone Goal API for creating new goal
     *
     * @param timesList
     * @param object
     */
    private void createTimeZoneGoal(List<String> timesList, Object object) {
        YonaActivity.getActivity().showLoadingView(true, null);
        if (object instanceof YonaGoal) {
            APIManager.getInstance().getChallengesManager().postTimeGoals(timesList, (YonaGoal) object, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        } else if (object instanceof YonaActivityCategories) {
            APIManager.getInstance().getChallengesManager().postTimeGoals(timesList, (YonaActivityCategories) object, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }

    }

    /**
     * Call API of update time zone goal
     *
     * @param timeList
     * @param yonaGoal
     */
    private void updateTimeZoneGoal(List<String> timeList, YonaGoal yonaGoal) {
        YonaActivity.getActivity().showLoadingView(true, null);
        if (yonaGoal != null) {
            APIManager.getInstance().getChallengesManager().updateTimeGoals(timeList, yonaGoal, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    updateGoalNotify(result);
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    /**
     * Go back to screen
     */
    private void goBackToScreen() {
        getActivity().onBackPressed();
    }

    /**
     * Show Timepicker with selection time or default minute
     *
     * @param allowDualSelection
     * @param interval
     * @param maxTime
     * @param minTime
     * @param listener
     */
    private void showTimePicker(boolean allowDualSelection, int interval, Timepoint maxTime, Timepoint minTime, TimePickerDialog.OnTimeSelected listener) {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog().newInstance(listener, minTime, maxTime, true, allowDualSelection);
        mTimePickerDialog.setAccentColor(Color.parseColor("#8ab518"));
        mTimePickerDialog.setTimeInterval(1, interval, 1);
        mTimePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        mTimePickerDialog.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChallenges:
                if (currentTab.equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString())) {
                    if (TextUtils.isEmpty(mBudgetGoalTime.getText())) {
                        showError(new ErrorMessage(getString(R.string.add_goal_error, getString(R.string.challengescredit))));
                        return;
                    }
                    createUpdateBudgetGoal();
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString())) {
                    if (listOfTimes == null || listOfTimes.size() == 0) {
                        showError(new ErrorMessage(getString(R.string.add_goal_error, getString(R.string.challengeszone))));
                        return;
                    }
                    createUpdateTimeZoneGoal();
                } else if (currentTab.equalsIgnoreCase(GoalsEnum.NOGO.getActionString())) {
                    createNewBudgetGoal(0, mYonaGoal);
                }
                break;
            case R.id.goal_item_layout:
                if (!TextUtils.isEmpty(mBudgetGoalTime.getText())) {
                    long miliseconds = TimeUnit.MINUTES.toMillis(Integer.parseInt(mBudgetGoalTime.getText().toString()));
                    Timepoint timepoint = new Timepoint((int) TimeUnit.MILLISECONDS.toHours(miliseconds), (int) TimeUnit.MILLISECONDS.toMinutes(miliseconds), (int) TimeUnit.MILLISECONDS.toSeconds(miliseconds));
                    showTimePicker(false, AppConstant.TIME_INTERVAL_ONE, null, timepoint, budgetTimeSetListener);
                } else {
                    showTimePicker(false, AppConstant.TIME_INTERVAL_ONE, null, new Timepoint(0, 0, 0), budgetTimeSetListener);
                }
                break;
            case R.id.img_add_goal:
                showTimePicker(true, AppConstant.TIME_INTERVAL_FIFTEEN, null, new Timepoint(0, 0, 0), timeZoneSetListener);
                break;

            default:
                break;
        }
    }

    /**
     * Create or update Budget Goal
     */
    private void createUpdateBudgetGoal() {
        if (mYonaGoal instanceof YonaGoal) {
            if (((YonaGoal) mYonaGoal).getLinks().getEdit() != null && !TextUtils.isEmpty(((YonaGoal) mYonaGoal).getLinks().getEdit().getHref())) {
                updateBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), (YonaGoal) mYonaGoal);
            } else {
                createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), mYonaGoal);
            }
        } else if (mYonaGoal instanceof YonaActivityCategories) {
            createNewBudgetGoal(Long.valueOf(mBudgetGoalTime.getText().toString()), mYonaGoal);
        }
    }

    /**
     * Create or update Timezone Goal
     */
    private void createUpdateTimeZoneGoal() {
        if (listOfTimes == null || listOfTimes.size() == 0) {
            showError(new ErrorMessage(getString(R.string.add_goal_error)));
            return;
        }
        if (mYonaGoal instanceof YonaGoal) {
            if (((YonaGoal) mYonaGoal).getLinks().getEdit() != null && !TextUtils.isEmpty(((YonaGoal) mYonaGoal).getLinks().getEdit().getHref())) {
                updateTimeZoneGoal(listOfTimes, (YonaGoal) mYonaGoal);
            } else {
                createTimeZoneGoal(listOfTimes, mYonaGoal);
            }
        } else if (mYonaGoal instanceof YonaActivityCategories) {
            createTimeZoneGoal(listOfTimes, mYonaGoal);
        }
    }

    /**
     * call Timepicker for TimeZone with time
     *
     * @param v
     * @param updatingStartTime
     */
    private void callTimePickerForTimeZone(final View v, boolean updatingStartTime) {
        final Bundle bTime = (Bundle) v.getTag();
        final String updatedTime = bTime.getString(AppConstant.TIME);
        String[] startTime = AppUtils.getSplitedTime(updatedTime);
        final int position = bTime.getInt(AppConstant.POSITION);
        if (updatingStartTime) {
            showTimePicker(true, AppConstant.TIME_INTERVAL_FIFTEEN, AppUtils.getTimeInMilliseconds(startTime[1]), AppUtils.getTimeInMilliseconds(startTime[0]), new TimePickerDialog.OnTimeSelected() {
                @Override
                public void setTime(Timepoint firstTime, Timepoint secondTime) {
                    if (firstTime != null && secondTime != null) {
                        if (timeZoneGoalsAdapter != null) {
                            timeZoneGoalsAdapter.updateTimeForItem(position, mergeZoneTime(firstTime, secondTime));
                        }
                    }

                }
            });
        } else {
            showTimePicker(true, AppConstant.TIME_INTERVAL_FIFTEEN, AppUtils.getTimeInMilliseconds(startTime[1]), AppUtils.getTimeInMilliseconds(startTime[0]), new TimePickerDialog.OnTimeSelected() {
                @Override
                public void setTime(Timepoint firstTime, Timepoint secondTime) {
                    if (firstTime != null && secondTime != null) {
                        if (timeZoneGoalsAdapter != null) {
                            timeZoneGoalsAdapter.updateTimeForItem(position, mergeZoneTime(firstTime, secondTime));
                        }
                    }

                }
            });
        }
    }

    /**
     * Merge Two times in format of HH:MM-HH:MM
     *
     * @param firstTimepoint
     * @param secondTimepoint
     * @return
     */
    private String mergeZoneTime(Timepoint firstTimepoint, Timepoint secondTimepoint) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(AppUtils.getTimeDigit(firstTimepoint.getHour()))
                .append(":")
                .append(AppUtils.getTimeDigit(firstTimepoint.getMinute())).append("-")
                .append(AppUtils.getTimeDigit(secondTimepoint.getHour()))
                .append(":")
                .append(AppUtils.getTimeDigit(secondTimepoint.getMinute()));
        return strBuilder.toString();
    }


}
