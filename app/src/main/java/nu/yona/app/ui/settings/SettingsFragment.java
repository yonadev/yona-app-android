/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.impl.DeviceManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.pincode.PinActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public class SettingsFragment extends BaseFragment {
    private DeviceManagerImpl deviceManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, null);

        setupToolbar(view);

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        deviceManager = new DeviceManagerImpl(getActivity());
        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.settings_list_item, new String[]{getString(R.string.changepin), getString(R.string.privacy), getString(R.string.adddevice), getString(R.string.deleteuser)}));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((YonaFontTextView) view).getText().toString().equals(getString(R.string.changepin))) {
                    showChangePin();
                } else if (((YonaFontTextView) view).getText().toString().equals(getString(R.string.privacy))) {
                    showPrivacy();
                } else if (((YonaFontTextView) view).getText().toString().equals(getString(R.string.adddevice))) {
                    YonaActivity.getActivity().showLoadingView(true, null);
                    addDevice(AppUtils.getRandomString(AppConstant.ADD_DEVICE_PASSWORD_CHAR_LIMIT));
                } else if (((YonaFontTextView) view).getText().toString().equals(getString(R.string.deleteuser))) {
                    unsubscribeUser();
                }
            }
        });
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ((TextView) view.findViewById(R.id.label_version)).setText(getString(R.string.version) + pInfo.versionName + getString(R.string.space) + pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            AppUtils.throwException(SettingsFragment.class.getSimpleName(), e, Thread.currentThread(), null);
        }
//        for (int i = 0; i < AppConstant.environmentList.length; i++) {
//            if (AppConstant.environemntPath[i].toString().equalsIgnoreCase(YonaApplication.getEventChangeManager().getDataState().getServerUrl())) {
//                ((TextView) view.findViewById(R.id.label_server)).setText(getString(R.string.environemnt, AppConstant.environmentList[i]));
//                break;
//            }
//        }
        return view;
    }

    private void showChangePin() {
        YonaActivity.getActivity().setSkipVerification(true);
        APIManager.getInstance().getPasscodeManager().resetWrongCounter();
        Intent intent = new Intent(getActivity(), PinActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstant.FROM_SETTINGS, true);
        bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.PIN_RESET_VERIFICATION);
        bundle.putInt(AppConstant.PROGRESS_DRAWABLE, R.drawable.pin_reset_progress_bar);
        bundle.putString(AppConstant.SCREEN_TITLE, getString(R.string.changepin));
        bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(YonaActivity.getActivity(), R.color.mango));
        bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_mango);
        bundle.putInt(AppConstant.PASSCODE_TEXT_BACKGROUND, R.drawable.passcode_edit_bg_mango);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showPrivacy() {
        Intent friendIntent = new Intent(IntentEnum.ACTION_PRIVACY_POLICY.getActionString());
        YonaActivity.getActivity().replaceFragment(friendIntent);
    }

    private void unsubscribeUser() {
        CustomAlertDialog.show(YonaActivity.getActivity(), getString(R.string.deleteuser), getString(R.string.deleteusermessage),
                getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doUnsubscribe();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private void doUnsubscribe() {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getAuthenticateManager().deleteUser(new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_NOT_EXIST, null);
                YonaActivity.getActivity().showLoadingView(false, null);
                startActivity(new Intent(YonaActivity.getActivity(), LaunchActivity.class));
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH, null);

            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                Snackbar snackbar = Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), ((ErrorMessage) errorMessage).getMessage(), Snackbar.LENGTH_SHORT);
                TextView textView = ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text));
                textView.setMaxLines(5);
                snackbar.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
        YonaActivity.getActivity().setSkipVerification(false);
    }

    private void setTitleAndIcon() {
        toolbarTitle.setText(R.string.settings);
    }

    private void addDevice(final String pin) {
        try {
            deviceManager.addDevice(pin, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    showAlert(getString(R.string.yonaadddevicemessage, pin), true);
                }

                @Override
                public void onError(Object errorMessage) {
                    showAlert(((ErrorMessage) errorMessage).getMessage(), false);
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(SettingsFragment.class.getSimpleName(), e, Thread.currentThread(), null);
            showAlert(e.toString(), false);
        }
    }

    private void showAlert(String message, final boolean doDelete) {
        try {
            if (YonaActivity.getActivity() != null) {
                YonaActivity.getActivity().showLoadingView(false, null);
                Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (doDelete) {
                                    doDeleteDeviceRequest();
                                }
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            Log.e(SettingsFragment.class.getSimpleName(), e.getMessage());
        }
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
            AppUtils.throwException(SettingsFragment.class.getSimpleName(), e, Thread.currentThread(), null);
            showAlert(e.toString(), false);
        }
    }
}
