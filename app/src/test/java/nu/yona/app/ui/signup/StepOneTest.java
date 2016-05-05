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
public class StepOneTest extends YonaTestCase {

    private SignupActivity activity;
    private YonaFontEditTextView firstName, lastName;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(SignupActivity.class)
                .create()
                .start()
                .resume()
                .get();
        StepOne stepOne = new StepOne();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stepOne, null);
        fragmentTransaction.commit();
        firstName = (YonaFontEditTextView) activity.findViewById(R.id.first_name);
        lastName = (YonaFontEditTextView) activity.findViewById(R.id.last_name);
    }

    @Test
    public void validateFirstName() {
        firstName.setText("Kinnar");
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateText(firstName.getText().toString()));
    }

    @Test
    public void validateLastName() {
        lastName.setText("Vasa");
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateText(lastName.getText().toString()));
    }
}
