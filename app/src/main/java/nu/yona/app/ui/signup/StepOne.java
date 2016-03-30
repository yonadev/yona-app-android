/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class StepOne extends BaseFragment {
    private TextInputLayout firstNameLayout, lastNameLayout;
    private TextView privacyPolicy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_stepone_fragment, null);
//        This will show error message under editText
//        firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
//        firstNameLayout.setErrorEnabled(true);
//        firstNameLayout.setError("You need to enter a name");
        privacyPolicy = (TextView) view.findViewById(R.id.privacyPolicy);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

}
