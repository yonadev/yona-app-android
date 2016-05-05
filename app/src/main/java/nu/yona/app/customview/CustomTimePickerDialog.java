/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;

/**
 * The type Custom time picker dialog.
 */
public class CustomTimePickerDialog extends DialogFragment implements OnClickListener {
    private int timeInterval = 1;
    private Dialog d;
    private TimePicker timePicker;
    private OnTimeSetListener timeSetListener;
    private boolean isNextAllow;
    private long minSelectedTime;
    private long maxSelectedTime;
    private Calendar cal;
    private YonaFontButton btnNext;
    private YonaFontButton btnDone;
    private YonaFontButton btnPrevious;
    private String firstTime;
    private YonaFontTextView titleOfDialog;
    private YonaFontTextView errorDialog;
    private String secondTime;

    /**
     * Gets second time.
     *
     * @return the second time
     */
    public String getSecondTime() {
        return secondTime;
    }

    /**
     * Sets second time.
     *
     * @param secondTime the second time
     */
    public void setSecondTime(String secondTime) {
        this.secondTime = secondTime;
    }

    /**
     * Sets on time set listener.
     *
     * @param listener the listener
     */
    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.timeSetListener = listener;
    }

    /**
     * Sets min time.
     *
     * @param time the time
     */
    public void setMinTime(Long time) {
        minSelectedTime = time;
    }

    /**
     * Sets max time.
     *
     * @param time the time
     */
    public void setMaxTime(Long time) {
        maxSelectedTime = time;
    }

    /**
     * Sets time picker interval.
     *
     * @param tTimeInterval the t time interval
     */
    public void setTimePickerInterval(long tTimeInterval) {
        if (tTimeInterval > 0) {
            this.timeInterval = (int) TimeUnit.MILLISECONDS.toMinutes(tTimeInterval);
        }
    }

    /**
     * Sets is next allow.
     *
     * @param isNextAllow the is next allow
     */
    public void setIsNextAllow(boolean isNextAllow) {
        this.isNextAllow = isNextAllow;
    }

    /**
     * Gets first time.
     *
     * @return the first time
     */
    @NonNull
    public String getFirstTime() {
        return firstTime;
    }

    private void setFirstTime(String first_Time) {
        this.firstTime = first_Time;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        d = new Dialog(getActivity());
        getCurrentCalendar();
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawableResource(R.drawable.custom_rouded_corner);
        d.setContentView(R.layout.numberpicker_dialog);
        // calender.setTimeZone(TimeZone.getDefault());
        d.findViewById(R.id.layoutTime).setVisibility(View.VISIBLE);
        timePicker = (TimePicker) d.findViewById(R.id.time_picker);
        timePicker.setVisibility(View.VISIBLE);
        //setTimePickerInterval(timePicker);
        timePicker.setIs24HourView(true);
        btnNext = (YonaFontButton) d.findViewById(R.id.txtNext);
        btnDone = (YonaFontButton) d.findViewById(R.id.txtdone);
        btnPrevious = (YonaFontButton) d.findViewById(R.id.txtPrevious);
        titleOfDialog = (YonaFontTextView) d.findViewById(R.id.timepickerDialogTitle);
        errorDialog = (YonaFontTextView) d.findViewById(R.id.errorTimer);
        btnNext.setVisibility(isNextAllow ? View.VISIBLE : View.GONE);
        btnPrevious.setVisibility(View.GONE);
        btnDone.setVisibility(isNextAllow ? View.GONE : View.VISIBLE);
        String title = isNextAllow ? getString(R.string.timepickerstarttimelable) : getString(R.string.timepickerselect);
        titleOfDialog.setText(title);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                errorDialog.setText("");
                updateMinutesOnTimeInterval(hourOfDay, minute);
            }
        });


        refreshView();
        btnNext.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        d.show();
        return d;
    }


    @SuppressWarnings("deprecation")
    private void updateMinutesOnTimeInterval(int hourOfDay, int minute) {

        Calendar calUpdate = getCurrentCalendar();
        calUpdate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calUpdate.set(Calendar.MINUTE, minute);
        timePicker.clearFocus();
        //timePicker.invalidate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calUpdate.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calUpdate.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(calUpdate.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calUpdate.get(Calendar.MINUTE));
        }
    }

    @SuppressWarnings("deprecation")
    private void refreshView() {
        timePicker.clearFocus();
        timePicker.invalidate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(getHours());
            timePicker.setMinute(getMinutes());
        } else {
            timePicker.setCurrentHour(getHours());
            timePicker.setCurrentMinute(getMinutes());
        }
    }

    private void hideFirstScreenView() {
        btnNext.setVisibility(View.GONE);
        btnPrevious.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);
    }

    private void hideSecondScreenView() {
        btnNext.setVisibility(View.VISIBLE);
        btnPrevious.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtdone:

                final String hr = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentHour()));
                final String minute = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentMin()));
                final String time = hr + ":" + minute;
                setSecondTime(time);

                if (timeSetListener != null) {
                    if (isNextAllow && checkDateIsPast()) {
                        errorDialog.setText(getString(R.string.timepickererror));
                        return;
                    } else if (isNextAllow && !checkDateIsPast()) {
                        timeSetListener.setTime(firstTime + "-" + time);
                    } else {
                        timeSetListener.setTime(time);
                    }
                }
                d.dismiss();
                break;
            case R.id.txtNext:
                hideFirstScreenView();
                String selectedHr = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentHour()));
                String selectedMin = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentMin()));
                setFirstTime(selectedHr + ":" + selectedMin);
                setMinTime(AppUtils.getTimeInMilliseconds(getFirstTime()));
                refreshView();
                updateTitleText(getActivity().getString(R.string.timepickerendtimelable));
                break;
            case R.id.txtPrevious:
                //To get the first selected time and display on time picker
                hideSecondScreenView();
                String preHr = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentHour()));
                String preMin = AppUtils.getHourDigit(String.valueOf(getTimePickerCurrentMin()));
                setSecondTime(preHr + ":" + preMin);
                setMaxTime(AppUtils.getTimeInMilliseconds(getSecondTime()));
                refreshView();
                updateTitleText(getActivity().getString(R.string.timepickerstarttimelable));

            default:
                break;
        }
    }

    private boolean checkDateIsPast() {
        Date fromDate = null;
        Date toDate = null;
        String[] fromTimes = AppUtils.getSplitedHr(getFirstTime());
        if (fromTimes.length > 0) {
            String fromHr = fromTimes[0];
            String fromMin = fromTimes[1];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromHr));
            cal.set(Calendar.MINUTE, Integer.parseInt(fromMin));
            fromDate = cal.getTime();
        }

        String[] toTimes = AppUtils.getSplitedHr(getSecondTime());
        if (toTimes.length > 0) {
            String toHr = toTimes[0];
            String toMin = toTimes[1];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toHr));
            cal.set(Calendar.MINUTE, Integer.parseInt(toMin));
            toDate = cal.getTime();
        }

        if (fromDate != null && toDate != null) {
            if (!toDate.after(fromDate)) {
                return true;
            }
        }

        return false;
    }

    private void updateTitleText(String titleText) {
        titleOfDialog.setText(titleText);
    }

    /**
     * Get time picker's Current hour selected
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private int getTimePickerCurrentHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }


    @SuppressWarnings("deprecation")
    private int getTimePickerCurrentMin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

    /**
     * Gets hours.
     *
     * @return the hours
     */
    public int getHours() {
        Calendar cal = getCurrentCalendar();
        if ((maxSelectedTime == 0 || minSelectedTime == 0) && TextUtils.isEmpty(getFirstTime())) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else {
            if (btnDone.getVisibility() == View.VISIBLE) {
                if (maxSelectedTime > 0) {
                    cal.setTimeInMillis(maxSelectedTime);
                }
            } else {
                if (minSelectedTime > 0) {
                    cal.setTimeInMillis(minSelectedTime);
                }
            }
        }

        return cal.get(Calendar.HOUR_OF_DAY);
    }

    private int getMinutes() {
        Calendar cal = getCurrentCalendar();
        if ((maxSelectedTime == 0 || minSelectedTime == 0) && TextUtils.isEmpty(getFirstTime())) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else {
            if (btnDone.getVisibility() == View.VISIBLE) {
                if (maxSelectedTime > 0) {
                    cal.setTimeInMillis(maxSelectedTime);
                }
            } else {
                if (minSelectedTime > 0) {
                    cal.setTimeInMillis(minSelectedTime);
                }
            }
        }
        return cal.get(Calendar.MINUTE);
    }


    private Calendar getCurrentCalendar() {
        if (cal == null) {
            cal = Calendar.getInstance();
            //cal.setTimeZone(TimeZone.getDefault());
        }
        return cal;
    }

    /**
     * The interface On time set listener.
     */
    public interface OnTimeSetListener {
        /**
         * Sets time.
         *
         * @param time the time
         */
        void setTime(String time);
    }

}