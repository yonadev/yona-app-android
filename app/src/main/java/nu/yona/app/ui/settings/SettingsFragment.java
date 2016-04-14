/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.api.manager.impl.DeviceManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class SettingsFragment extends BaseFragment {
    private View view;
    private DeviceManagerImpl deviceManager;
    private YonaActivity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, null);

        deviceManager = new DeviceManagerImpl(getActivity());

        activity = (YonaActivity) getActivity();

        view.findViewById(R.id.add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoadingView(true, null);
                String pin = AppUtils.getRandomString(AppConstant.ADD_DEVICE_PASSWORD_CHAR_LIMIT);
                addDevice(pin);
            }
        });
        return view;
    }

    private void addDevice(final String pin) {
        try {
            deviceManager.addDevice(pin, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    showAlert(pin + getString(R.string.yona_add_device_message), true);
                }

                @Override
                public void onError(Object errorMessage) {
                    showAlert(((ErrorMessage) errorMessage).getMessage(), false);
                }
            });
        } catch (Exception e) {
            showAlert(e.toString(), false);
        }
    }

    private void showAlert(String message, final boolean doDelete) {
        activity.showLoadingView(false, null);
        CustomAlertDialog.show(getActivity(),
                message,
                getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(doDelete) {
                            doDeleteDeviceRequest();
                        }
                        dialogInterface.dismiss();
                    }
                });
    }

    private void doDeleteDeviceRequest() {
        try {
            deviceManager.deleteDevice(new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    //do nothing if server response success
                }

                @Override
                public void onError(Object errorMessage) {
                    showAlert(((ErrorMessage) errorMessage).getMessage(), false);
                }
            });
        } catch (Exception e) {
            showAlert(e.toString(), false);
        }
    }
}
