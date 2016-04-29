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
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;

public class CustomTimePickerDialog extends DialogFragment implements OnClickListener {
    private int timeInterval = 1;
    private Dialog d;
    private TimePicker timePicker;
    private OnTimeSetListener timeSetListener;
    private boolean isNextAllow;
    private long minSelectedTime;
    private long maxSelectedTime;
    private NumberPicker minutePicker;
    private boolean isPastTimeSelectionAllow;
    private Calendar cal;
    private YonaFontButton txtSelect;
    private YonaFontButton txtDone;
    private String firstTime;

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.timeSetListener = listener;
    }

    public void setMinTime(Long time) {
        minSelectedTime = time;
    }

    public void setMaxTime(Long time) {
        maxSelectedTime = time;
    }

    public void setTimePickerInterval(long tTimeInterval) {
        if (tTimeInterval > 0) {
            this.timeInterval = (int) TimeUnit.MILLISECONDS.toMinutes(tTimeInterval);
        }
    }

    public void setPastTimeSelectionAllow(boolean isAllow) {
        this.isPastTimeSelectionAllow = isAllow;
    }

    public void setIsNextAllow(boolean isNextAllow) {
        this.isNextAllow = isNextAllow;
    }

    private void setFirstTime(String first_Time) {
        this.firstTime = first_Time;
    }

    @NonNull
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
        setTimePickerInterval(timePicker);
        timePicker.setIs24HourView(true);
        txtSelect = (YonaFontButton) d.findViewById(R.id.textSelected);
        txtDone = (YonaFontButton) d.findViewById(R.id.txtdone);
        txtSelect.setVisibility(isNextAllow ? View.VISIBLE : View.GONE);
        txtDone.setVisibility(isNextAllow ? View.GONE : View.VISIBLE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (!isPastTimeSelectionAllow) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    Date date = cal.getTime();
                    Date date2 = getCurrentCalendar().getTime();
                    if (!date.after(date2)) {
                        timePicker.invalidate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePicker.setHour(getHours());
                        } else {
                            timePicker.setCurrentHour(getHours());
                        }
                        setMinutesInPicker(getMinutes());
                    }
                }
            }
        });

        refreshView();
        txtSelect.setOnClickListener(this);
        txtDone.setOnClickListener(this);
        d.show();
        return d;
    }

    private void refreshView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(getHours());
        } else {
            timePicker.setCurrentHour(getHours());
        }
        setMinutesInPicker(getMinutes());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtdone:

                final String hr = AppUtils.getHourDigit(String.valueOf(timePicker.getCurrentHour()));
                final String minute = AppUtils.getHourDigit(getMinutesFromPicker());
                final String time = hr + ":" + minute;
                if (!TextUtils.isEmpty(firstTime)) {
                    timeSetListener.setTime(firstTime + "-" + time);
                } else {
                    timeSetListener.setTime(time);
                }
                d.dismiss();
                break;
            case R.id.textSelected:
                final String selectedHr = AppUtils.getHourDigit(String.valueOf(timePicker.getCurrentHour()));
                final String selectedMin = AppUtils.getHourDigit(getMinutesFromPicker());
                setFirstTime(selectedHr + ":" + selectedMin);
                txtDone.setVisibility(View.VISIBLE);
                txtSelect.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private int getHours() {
        Calendar cal = getCurrentCalendar();
        if (txtDone.getVisibility() == View.VISIBLE) {
            if (maxSelectedTime > 0) {
                cal.setTimeInMillis(maxSelectedTime);
            }
        } else {
            if (minSelectedTime > 0) {
                cal.setTimeInMillis(minSelectedTime);
            }
        }
        if (getMinutes().equalsIgnoreCase("00") || getMinutes().equalsIgnoreCase("0")) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    private String getMinutes() {
        Calendar cal = getCurrentCalendar();
        if (txtDone.getVisibility() == View.VISIBLE) {
            if (maxSelectedTime > 0) {
                cal.setTimeInMillis(maxSelectedTime);
            }
        } else {
            if (minSelectedTime > 0) {
                cal.setTimeInMillis(minSelectedTime);
            }
        }

        return getRoundedMinute(cal.get(Calendar.MINUTE));
    }

    private String getRoundedMinute(int minute) {
        if (minutePicker != null) {
            String[] values = minutePicker.getDisplayedValues();
            for (String value : values) {
                int floorValue = Integer.parseInt(value);
                if (minute <= floorValue) {
                    return value;
                } else if (minute < (floorValue + timeInterval)) {
                    if ((floorValue + timeInterval) == 60) {
                        return String.valueOf(0);
                    } else {
                        return String.valueOf(floorValue + timeInterval);
                    }
                }
            }
        }

        return "";
    }

    private void setTimePickerInterval(TimePicker timePicker) {
        try {

           /* Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = (TimePicker) timePicker.findViewById(timePickerField
                    .getInt(null));
            Field field = classForid.getField("minute");

            minutePicker = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));
*/

            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field = classForid.getField("minute");
            minutePicker = (NumberPicker) timePicker.findViewById(field.getInt(null));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59 / timeInterval);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += timeInterval) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            AppUtils.throwException(CustomTimePickerDialog.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    private void setMinutesInPicker(String minutes) {

        if (minutePicker != null && !TextUtils.isEmpty(minutes)) {
            if (minutes.length() == 1) {
                minutes = "0" + minutes;
            }

            String[] values = minutePicker.getDisplayedValues();
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(minutes)) {
                    minutePicker.setValue(i);
                    break;
                }
            }
        }
    }

    private String getMinutesFromPicker() {
        if (minutePicker != null) {
            String[] values = minutePicker.getDisplayedValues();
            return values[minutePicker.getValue()];
        } else if (timePicker != null) {
            return String.valueOf(timePicker.getCurrentMinute());
        }
        return null;
    }

    private Calendar getCurrentCalendar() {
        if (cal == null) {
            cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
        }
        return cal;
    }

    public interface OnTimeSetListener {
        void setTime(String time);
    }

}