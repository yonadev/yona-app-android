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

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import nu.yona.app.R;
import nu.yona.app.YonaTestCase;
import nu.yona.app.customview.YonaFontEditTextView;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepOneTest extends YonaTestCase {
    StepOne stepOne;
    SignupActivity activity;
    YonaFontEditTextView firstName, lastName;
    Button nextButton;
    @Before
    public void setup() {
        if(stepOne == null) {
            activity = Robolectric.buildActivity(SignupActivity.class)
                    .create()
                    .start()
                    .resume()
                    .get();
            stepOne = new StepOne();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(stepOne, null);
            fragmentTransaction.commit();
            firstName = (YonaFontEditTextView) activity.findViewById(R.id.first_name);
            lastName = (YonaFontEditTextView) activity.findViewById(R.id.last_name);
            nextButton = (Button) activity.findViewById(R.id.next);
        }
    }

    @Test
    public void validateFirstName(){
        firstName.setText("Kinnar");
        assertTrue(activity.getSignupManager().validateText(firstName.getText().toString()));
    }

    @Test
    public void validateLastName(){
        lastName.setText("Vasa");
        assertTrue(activity.getSignupManager().validateText(lastName.getText().toString()));
    }
}
