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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import nu.yona.app.R;
import nu.yona.app.YonaTestCase;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.customview.YonaFontEditTextView;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepTwoTest extends YonaTestCase {

    private SignupActivity activity;
    private YonaFontEditTextView mobileNumber;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(SignupActivity.class)
                .create()
                .start()
                .resume()
                .get();
        StepTwo stepTwo = new StepTwo();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stepTwo, null);
        fragmentTransaction.commit();
        mobileNumber = (YonaFontEditTextView) activity.findViewById(R.id.mobile_number);
    }

    @Test
    public void validateMobileNumber() {
        mobileNumber.setText("+31123456789");
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateMobileNumber(mobileNumber.getText().toString()));
    }
}
