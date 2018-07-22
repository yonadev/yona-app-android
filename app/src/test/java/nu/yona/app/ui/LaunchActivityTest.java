/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Button;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowSQLiteConnection;


import nu.yona.app.R;
import nu.yona.app.YonaTestCase;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.ui.login.LoginActivity;
import nu.yona.app.ui.signup.SignupActivity;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by kinnarvasa on 30/03/16.
 */
public class LaunchActivityTest extends YonaTestCase {

    private Activity activity;
    private Button jonMeBtn, loginBtn;
    LaunchActivity launchActivity;

    DatabaseHelper dbhelper = DatabaseHelper.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        if (activity == null) {
            activity = Robolectric.setupActivity(LaunchActivity.class);
            jonMeBtn = (Button) activity.findViewById(R.id.join);
            loginBtn = (Button) activity.findViewById(R.id.login);
            launchActivity = new LaunchActivity();
        }
    }

        @Test
        public void testSignupClick () {
            jonMeBtn.performClick();

            Intent startedIntent = shadowOf(activity).getNextStartedActivity();
            ShadowIntent shadowIntent = shadowOf(startedIntent);
            assertEquals(SignupActivity.class, shadowIntent.getIntentClass());

            //Assert.assertEquals(shadowOf(activity).getNextStartedActivity(), new Intent(activity, SignupActivity.class));
        }

        @Test
        public void testLoginClick () {
            loginBtn.performClick();

            Intent startedIntent = shadowOf(activity).getNextStartedActivity();
            ShadowIntent shadowIntent = shadowOf(startedIntent);
            assertEquals(LoginActivity.class, shadowIntent.getIntentClass());

            //Assert.assertEquals(shadowOf(activity).getNextStartedActivity(), new Intent(activity, SignupActivity.class));
        }

        @Test
        public void testURLEmpty () {
            String enteredURL = "";
            boolean result = launchActivity.validateUrl(enteredURL);
            boolean expectedResult = false;

            assertEquals(expectedResult, result);
        }

        @Test
        public void testUrlSuccess () {
            String url = "http://www.google.com";
            boolean result = launchActivity.validateUrl(url);
            boolean expectedResult = true;
            assertEquals(expectedResult, result);
        }

        @Test
        public void testUrlFailure () {
            String url = "abc";
            boolean result = launchActivity.validateUrl(url);
            boolean expectedResult = false;
            assertEquals(expectedResult, result);
        }
    @After
    public void tearDown() {
        dbhelper.close();
    }


}