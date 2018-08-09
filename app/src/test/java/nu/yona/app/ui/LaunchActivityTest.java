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
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowIntent;

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

    private LaunchActivity launchActivity;

    DatabaseHelper dbhelper = DatabaseHelper.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        launchActivity = Robolectric.setupActivity(LaunchActivity.class);
    }

    @Test
    public void testSignupClick() {
        Button jonMeBtn = (Button) launchActivity.findViewById(R.id.join);
        jonMeBtn.performClick();
        Intent startedIntent = shadowOf(launchActivity).getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(SignupActivity.class, shadowIntent.getIntentClass());
    }

    @Test
    public void testLoginClick() {
        Button loginBtn = (Button) launchActivity.findViewById(R.id.login);
        loginBtn.performClick();
        Intent startedIntent = shadowOf(launchActivity).getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(LoginActivity.class, shadowIntent.getIntentClass());
    }

    @Test
    public void testURLEmpty() {
        String enteredURL = "";
        boolean result = launchActivity.validateUrl(enteredURL);
        assertEquals(false, result);
    }

    @Test
    public void testUrlSuccess() {
        String url = "http://www.google.com";
        boolean result = launchActivity.validateUrl(url);
        assertEquals(true, result);
    }

    @Test
    public void testUrlFailure() {
        String url = "abc";
        boolean result = launchActivity.validateUrl(url);
        assertEquals(false, result);
    }

    @Test
    public void testEnvrironmentSwitchDialogUI(){
        ImageView environmentSwitch = launchActivity.findViewById(R.id.environmentSwitch);
        environmentSwitch.performLongClick();
        AlertDialog latestAlert = (AlertDialog) ShadowDialog.getLatestDialog();
        assertTrue(latestAlert.isShowing());
        EditText editText= latestAlert.findViewById(R.id.edittext);
        assertNotNull(editText);
        editText.setText("http://testurlforfailure");
        Button okButton = latestAlert.getButton(Dialog.BUTTON_POSITIVE);
        assertNotNull(okButton);
        Button cancelButton = latestAlert.getButton(Dialog.BUTTON_NEGATIVE);
        assertNotNull(cancelButton);
    }

    @After
    public void tearDown() {
        dbhelper.close();
    }


}