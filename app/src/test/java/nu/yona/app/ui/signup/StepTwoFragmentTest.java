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

import org.junit.Before;
import org.junit.Test;

import nu.yona.app.YonaTestCase;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.customview.YonaFontEditTextView;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepTwoFragmentTest extends YonaTestCase {

    private SignupActivity activity;
    private YonaFontEditTextView mobileNumber;

    @Before
    public void setup() {

        /*activity = Robolectric.buildActivity(SignupActivity.class, intent)
                .create()
                .start()
                .resume()
                .get();

        StepTwoFragment stepTwoFragment = new StepTwoFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stepTwoFragment, null);
        fragmentTransaction.commit();*/
       // mobileNumber = (YonaFontEditTextView) activity.findViewById(R.id.mobileNumber);
    }

    @Test
    public void validateMobileNumber() {
        String mobileNumber = "+31123456789";
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateMobileNumber(mobileNumber));
    }
}
