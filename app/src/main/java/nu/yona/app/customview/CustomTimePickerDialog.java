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
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;
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
    public int timeInterval = 1;
    private Dialog d;
    private TimePicker timePicker;
    private OnTimeSetListener timeSetListener;
    private boolean showAsSoonAsPossible;
    private long minSelectedTime;
    private NumberPicker minutePicker;
    private long preparationTime;
    private boolean isPastTimeSelectionAllow;
    private Calendar cal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.timeSetListener = listener;
    }

    public void setMinTimeTime(Long time) {
        minSelectedTime = time;
    }

    public void setTimePickerInterval(long tTimeInterval) {
        if (tTimeInterval > 0) {
            this.timeInterval = (int) TimeUnit.MILLISECONDS.toMinutes(tTimeInterval);
        }
    }

    public void setPastTimeSelectionAllow(boolean isAllow) {
        this.isPastTimeSelectionAllow = isAllow;
    }

    public void setPreparationTime(long tPrepTime) {
        this.preparationTime = tPrepTime;
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
        setTimePickerInterval(timePicker);
        TextView txtSelect = (TextView) d.findViewById(R.id.textSelected);
        TextView txtDone = (TextView) d.findViewById(R.id.txtdone);
        txtSelect.setVisibility(showAsSoonAsPossible ? View.VISIBLE : View.GONE);
        timePicker.setVisibility(View.VISIBLE);
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(getHours());
        } else {
            timePicker.setCurrentHour(getHours());
        }
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
                        if (Build.VERSION.SDK_INT >= 23) {
                            timePicker.setHour(getHours());
                        } else {
                            timePicker.setCurrentHour(getHours());
                        }
                        setMinutesInPicker(getMinutes());
                    }
                }
            }
        });

        setMinutesInPicker(getMinutes());
        txtSelect.setOnClickListener(this);
        txtDone.setOnClickListener(this);
        d.show();
        return d;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtdone) {
            String hr = AppUtils.getHourDigit(String.valueOf(timePicker.getCurrentHour()));
            String minute = AppUtils.getHourDigit(getMinutesFromPicker());
            String time = hr + ":" + minute;
            timeSetListener.setTime(time);
            d.dismiss();
        }
    }

    public int getHours() {
        Calendar cal = getCurrentCalendar();
        if (minSelectedTime > 0) {
            cal.setTimeInMillis(minSelectedTime);
        }
        if (getMinutes().equalsIgnoreCase("00") || getMinutes().equalsIgnoreCase("0")) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public String getMinutes() {
        Calendar cal = getCurrentCalendar();
        if (minSelectedTime > 0) {
            cal.setTimeInMillis(minSelectedTime);
        }
        return getRoundedMinute(cal.get(Calendar.MINUTE));
    }

    public String getRoundedMinute(int minute) {
        if (minutePicker != null) {
            String[] values = minutePicker.getDisplayedValues();
            for (int i = 0; i < values.length; i++) {
                int floorValue = Integer.parseInt(values[i]);
                if (minute <= floorValue) {
                    return values[i];
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
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field = classForid.getField("minute");
            minutePicker = (NumberPicker) timePicker.findViewById(field.getInt(null));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59 / timeInterval);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += timeInterval) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e(CustomTimePickerDialog.class.getName(), e.getMessage());
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

    public Calendar getCurrentCalendar() {
        if (cal == null) {
            cal = Calendar.getInstance();
            if (preparationTime > 0) {
                cal.add(Calendar.MINUTE, (int) TimeUnit.MILLISECONDS.toMinutes(preparationTime));
            }
            cal.setTimeZone(TimeZone.getDefault());
        }
        return cal;
    }

    public interface OnTimeSetListener {
        void setTime(String time);
    }

}