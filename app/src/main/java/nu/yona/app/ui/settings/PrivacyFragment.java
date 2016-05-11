/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 11/05/16.
 */
public class PrivacyFragment extends BaseFragment {

    private YonaActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (YonaActivity) getActivity();
        return inflater.inflate(R.layout.privacy_fragment, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
    }

    private void setTitleAndIcon() {
        activity.getLeftIcon().setVisibility(View.GONE);
        activity.updateTitle(getString(R.string.privacy));
        activity.getRightIcon().setVisibility(View.GONE);
    }
}
